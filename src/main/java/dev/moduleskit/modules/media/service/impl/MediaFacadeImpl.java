package dev.moduleskit.modules.media.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dev.moduleskit.modules.media.api.exception.InvalidMediaException;
import dev.moduleskit.modules.media.api.response.MediaResponse;
import dev.moduleskit.modules.media.domain.Media;
import dev.moduleskit.modules.media.profile.ProfileRegistry;
import dev.moduleskit.modules.media.service.MediaFacade;
import dev.moduleskit.modules.media.service.MediaService;
import dev.moduleskit.modules.media.util.InMemoryMultipartFile;
import dev.moduleskit.modules.media.util.MediaSortingStrategyRegistry;

@Service
public class MediaFacadeImpl implements MediaFacade {

    private final MediaService mediaService;
    private final ProfileRegistry profileRegistry;
    private final MediaSortingStrategyRegistry sortingStrategyRegistry;

    public MediaFacadeImpl(MediaService mediaService, ProfileRegistry profileRegistry, 
            MediaSortingStrategyRegistry sortingStrategyRegistry) {
        this.mediaService = mediaService;
        this.profileRegistry = profileRegistry;
        this.sortingStrategyRegistry = sortingStrategyRegistry;
    }

    /**
     * Generic media attachment method.
     */
    @Override
    public MediaResponse attachMedia(MultipartFile file, String targetType, Long targetId, String collectionName) {
        try {
            var conversions = profileRegistry.resolve(targetType, collectionName);

            Media media = mediaService.storeWithConversions(
                    file,
                    targetType,
                    targetId,
                    collectionName,
                    conversions);

            return MediaResponse.fromMedia(media, mediaService.buildPublicUrl(media));

        } catch (IOException e) {
            throw new InvalidMediaException("Failed to save media for " + targetType, e);
        }
    }

    /**
     * Generic media retrieval method with pluggable sorting strategies.
     * 
     * Sorting behavior is determined by the target type via the MediaSortingStrategyRegistry.
     * Default behavior: OrderColumnAscending (preserves insertion order)
     * 
     * This design allows domain modules (ITEM, USER, etc.) to define their own sorting preferences
     * without coupling the media module to specific business logic.
     * New sorting behaviors can be registered at runtime without modifying this code.
     */
    @Override
    public List<MediaResponse> getMedia(String targetType, Long targetId, String collectionName) {
        var mediaList = mediaService.getMediaForEntity(targetType, targetId, collectionName);
        
        // Apply the sorting strategy registered for this target type
        var sortingStrategy = sortingStrategyRegistry.getStrategy(targetType);
        mediaList = sortingStrategy.sort(mediaList);
        
        return mediaList.stream()
            .map(m -> MediaResponse.fromMedia(m, mediaService.buildPublicUrl(m)))
            .toList();
    }

    @Override
    public MediaResponse attachMediaFromPath(Path filePath, String targetType, Long targetId, String collection)
            throws IOException {
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + filePath);
        }

        try (InputStream is = Files.newInputStream(filePath)) {
            byte[] bytes = is.readAllBytes();
            String filename = filePath.getFileName().toString();
            return attachMediaFromBytes(bytes, filename, targetType, targetId, collection);
        }
    }

    /**
     * Seeding-friendly method that allows setting orderColumn explicitly.
     */
    public MediaResponse attachMediaFromPathWithOrder(Path filePath, String targetType, Long targetId, 
            String collection, Integer orderColumn) throws IOException {
        MediaResponse response = attachMediaFromPath(filePath, targetType, targetId, collection);
        
        if (orderColumn != null) {
            // Update the orderColumn for this media
            var mediaOptional = mediaService.getMediaForEntity(targetType, targetId, collection)
                .stream()
                .filter(m -> m.getUuid().equals(response.uuid()))
                .findFirst();
            
            if (mediaOptional.isPresent()) {
                var media = mediaOptional.get();
                media.setOrderColumn(orderColumn);
                mediaService.saveMedia(media);
            }
        }
        
        return response;
    }

    @Override
    public void deleteMediaFor(String targetType, Long targetId, String collectionName) {
        var mediaList = mediaService.getMediaForEntity(targetType, targetId, collectionName);
        for (Media media : mediaList) {
            mediaService.deleteMedia(media.getUuid());
        }
    }

    private MediaResponse attachMediaFromBytes(byte[] bytes, String filename, String targetType, Long targetId,
            String collection) throws IOException {
        MultipartFile file = new InMemoryMultipartFile(filename, bytes);
        return attachMedia(file, targetType, targetId, collection);
    }
}
