package dev.moduleskit.modules.media.service.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Comparator;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dev.moduleskit.modules.media.api.request.ImageConversion;
import dev.moduleskit.modules.media.api.exception.InvalidMediaException;
import dev.moduleskit.modules.media.domain.Media;
import dev.moduleskit.modules.media.enums.StorageDisk;
import dev.moduleskit.modules.media.repository.MediaRepository;
import dev.moduleskit.modules.media.service.MediaService;
import dev.moduleskit.modules.media.service.MediaStorage;
import dev.moduleskit.modules.media.service.MediaValidator;
import dev.moduleskit.modules.media.util.SvgConversionUtil;

import jakarta.transaction.Transactional;

@Service
public class MediaServiceImpl implements MediaService {
    private static final Logger log = LoggerFactory.getLogger(MediaFacadeImpl.class);

    private final MediaRepository mediaRepository;
    private final MediaStorage storage;
    private final MediaValidator validator;
    private final Path uploadDir;
    private final String publicPath;

    public MediaServiceImpl(MediaRepository mediaRepository,
            MediaValidator mediaValidator,
            MediaStorage storage,
            @Value("${app.storage.upload-dir}") String uploadDir,
            @Value("${app.storage.public-path}") String publicPath) {
        this.mediaRepository = mediaRepository;
        this.validator = mediaValidator;
        this.storage = storage;
        this.uploadDir = Paths.get(uploadDir);
        this.publicPath = publicPath;
    }

    @Override
    @Transactional
    public Media store(MultipartFile file, String targetType, Long targetId, String collectionName) throws IOException {
        if (file == null) {
            throw new InvalidMediaException("File cannot be null");
        }

        validator.validateContentType(file);
        validator.validateImage(file);

        Media media = new Media();
        media.setUuid(UUID.randomUUID().toString());
        media.setTargetType(targetType);
        media.setTargetId(targetId);
        media.setCollectionName(collectionName);
        media.setDisk(StorageDisk.LOCAL);
        media.setSize(file.getSize());
        media.setMimeType(file.getContentType());

        // Folder: uploadDir/targetType/media-uuid/
        Path mediaFolder = uploadDir.resolve(targetType.toLowerCase()).resolve(media.getUuid());
        Files.createDirectories(mediaFolder);

        media = storeOriginal(file, media, mediaFolder);

        return mediaRepository.save(media);
    }

    @Override
    public List<Media> getMediaForEntity(String targetType, Long targetId, String collectionName) {
        return mediaRepository.findByTargetTypeAndTargetIdAndCollectionNameOrderByOrderColumnAsc(
                targetType, targetId, collectionName);
    }

    @Override
    @Transactional
    public void deleteMedia(String uuid) {
        mediaRepository.findByUuid(uuid).ifPresent(media -> {
            Path mediaFolder = resolveMediaFolder(media.getTargetType(), media.getUuid());
            try {
                if (Files.exists(mediaFolder)) {
                    Files.walk(mediaFolder)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try { Files.deleteIfExists(path); } catch (IOException ignored) { }
                        });
                }
            } catch (IOException ignored) {
            }
            mediaRepository.delete(media);
        });
    }

    @Override
    @Transactional
    public Media storeWithConversions(MultipartFile file,
            String targetType,
            Long targetId,
            String collectionName,
            List<ImageConversion> conversions) throws IOException {

        if (file == null) {
            throw new InvalidMediaException("File cannot be null");
        }

        // Detect SVG
        boolean isSvg = "image/svg+xml".equals(file.getContentType())
                || (file.getOriginalFilename() != null && file.getOriginalFilename().endsWith(".svg"));

        Media media = null;
        try {
            // 1️⃣ Store original
            media = store(file, targetType, targetId, collectionName);

            // 2️⃣ Apply conversions only for raster images
            if (!isSvg && !conversions.isEmpty()) {
                Path mediaFolder = resolveMediaFolder(targetType, media.getUuid());
                Path conversionsFolder = mediaFolder.resolve("conversions");
                Files.createDirectories(conversionsFolder);

                BufferedImage originalImage;
                try (var is = file.getInputStream()) {
                    originalImage = ImageIO.read(is);
                    if (originalImage == null) {
                        throw new IOException("Invalid image file");
                    }
                }

                Map<String, String> conversionFilenames = applyConversions(originalImage, conversionsFolder, conversions);
                media.setGeneratedConversions(buildPublicConversionUrls(media, conversionFilenames));
                mediaRepository.save(media);
            }

            // 3️⃣ Generate a raster thumbnail for SVG if conversions requested
            else if (isSvg && !conversions.isEmpty()) {
                Path mediaFolder = resolveMediaFolder(targetType, media.getUuid());
                Path conversionsFolder = mediaFolder.resolve("conversions");
                Files.createDirectories(conversionsFolder);

                for (ImageConversion conv : conversions) {
                    Path outputPath = conversionsFolder.resolve(conv.getName() + ".png");
                    SvgConversionUtil.generatePngThumbnail(file.getInputStream(), outputPath, conv.getWidth(),
                            conv.getHeight());
                }

                Map<String, String> filenames = new HashMap<>();
                for (ImageConversion conv : conversions) {
                    filenames.put(conv.getName(), conv.getName() + ".png");
                }
                media.setGeneratedConversions(buildPublicConversionUrls(media, filenames));
                mediaRepository.save(media);
            }

            return media;
        } catch (Exception e) {
            if (media != null) {
                try {
                    deleteMedia(media.getUuid());
                } catch (Exception ignored) {
                }
            }
            if (e instanceof InvalidMediaException) {
                throw e;
            }
            if (e instanceof IOException io) {
                throw io;
            }
            throw new InvalidMediaException("Failed to process media", e);
        }
    }

    @Override
    public String buildPublicUrl(Media media) {
        return publicPath + "/" + media.getTargetType().toLowerCase() + "/" + media.getUuid() + "/"
                + media.getFileName();
    }

    @Override
    public Media saveMedia(Media media) {
        return mediaRepository.save(media);
    }

    private Map<String, String> buildPublicConversionUrls(Media media, Map<String, String> filenames) {
        Map<String, String> urls = new HashMap<>();
        filenames.forEach((name, fileName) -> urls.put(name,
                publicPath + "/" + media.getTargetType().toLowerCase() +
                        "/" + media.getUuid() + "/conversions/" + fileName));
        return urls;
    }

    private Media storeOriginal(MultipartFile file, Media media, Path mediaFolder) throws IOException {
        storeOriginalFile(file, mediaFolder, media);
        return mediaRepository.save(media);
    }

    private void storeOriginalFile(MultipartFile file, Path mediaFolder, Media media) throws IOException {
        String extension = getFileExtension(file.getOriginalFilename());
        String originalFileName = "original." + extension;
        Path filePath = mediaFolder.resolve(originalFileName);

        storage.save(file, filePath);
        media.setFileName(originalFileName);
    }

    private Map<String, String> applyConversions(BufferedImage originalImage, Path conversionsFolder,
            List<ImageConversion> conversions) throws IOException {
        Map<String, String> filenames = new HashMap<>();
        Files.createDirectories(conversionsFolder);

        for (ImageConversion conv : conversions) {
            String fileName = resizeAndSaveConversion(originalImage, conv, conversionsFolder);
            filenames.put(conv.getName(), fileName);
        }

        return filenames;
    }

    private String resizeAndSaveConversion(BufferedImage originalImage, ImageConversion conv, Path folder)
            throws IOException {
        String format = conv.getFormat() != null ? conv.getFormat() : "jpg";
        String fileName = conv.getName() + "." + format;
        Path path = folder.resolve(fileName);

        BufferedImage resized = Scalr.resize(
                originalImage,
                Scalr.Method.QUALITY,
                Scalr.Mode.AUTOMATIC,
                conv.getWidth(),
                conv.getHeight());

        if ("webp".equals(format) && !ImageIO.getImageWritersByFormatName("webp").hasNext()) {
            throw new IOException("No WebP writer found. Check webp-imageio plugin.");
        }

        storage.save(resized, path, format);

        return fileName;
    }

    private String getFileExtension(String filename) {
        int i = filename.lastIndexOf('.');
        return (i > 0) ? filename.substring(i + 1) : "png";
    }

    private Path resolveMediaFolder(String targetType, String uuid) {
        return uploadDir.resolve(targetType.toLowerCase()).resolve(uuid);
    }
}
