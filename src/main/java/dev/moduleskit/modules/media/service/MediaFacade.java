package dev.moduleskit.modules.media.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import dev.moduleskit.modules.media.api.response.MediaResponse;

public interface MediaFacade {

    /**
     * 
     * @param file
     * @param targetType
     * @param targetId
     * @param collectionName
     * @return
     */
    public MediaResponse attachMedia(MultipartFile file, String targetType, Long targetId, String collectionName);

    /**
     * 
     * @param targetType
     * @param targetId
     * @param collectionName
     * @return
     */
    public List<MediaResponse> getMedia(String targetType, Long targetId, String collectionName);

    /**
     * seeding friendly method
     * @param filePath
     * @param targetType
     * @param targetId
     * @param collection
     * @return
     * @throws IOException
     */
    MediaResponse attachMediaFromPath(Path filePath, String targetType, Long targetId, String collection) throws IOException;

    /**
     * 
     * @param targetType
     * @param targetId
     * @param collectionName
     */
    public void deleteMediaFor(String targetType, Long targetId, String collectionName);
}
