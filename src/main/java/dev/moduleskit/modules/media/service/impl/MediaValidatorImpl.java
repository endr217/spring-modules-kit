package dev.moduleskit.modules.media.service.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import dev.moduleskit.modules.media.enums.MediaError;
import dev.moduleskit.modules.media.api.exception.InvalidMediaException;
import dev.moduleskit.modules.media.service.MediaValidator;

@Component
public class MediaValidatorImpl implements MediaValidator {

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/png", "image/jpeg", "image/gif", "image/webp", "image/svg+xml");

    private final MessageSource messageSource;

    public MediaValidatorImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void validateContentType(MultipartFile file) {
        if (file == null) {
            throw new InvalidMediaException("File cannot be null 3");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new InvalidMediaException(
                    MediaError.IMAGE_UNSUPPORTED_TYPE.getMessage(messageSource) + file.getContentType());
        }
    }

    @Override
    public void validateImage(MultipartFile file) {
        if (file == null) {
            throw new InvalidMediaException("File cannot be null");
        }

        String filename = file.getOriginalFilename();
        boolean isSvg = "image/svg+xml".equals(file.getContentType())
                || (filename != null && filename.toLowerCase().endsWith(".svg"));

        if (isSvg) {
            // Skip raster validation for SVG
            System.out.println("Detected svg!");
            return;
        }

        try (var is = file.getInputStream()) {
            BufferedImage img = ImageIO.read(is);
            if (img == null) {
                throw new InvalidMediaException(MediaError.IMAGE_INVALID.getMessage(messageSource));
            }
        } catch (IOException e) {
            throw new InvalidMediaException(MediaError.IMAGE_INVALID.getMessage(messageSource), e);
        }
    }
}
