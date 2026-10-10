package dev.moduleskit.modules.media.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import dev.moduleskit.modules.media.api.request.ImageConversion;
import dev.moduleskit.modules.media.domain.Media;

public interface MediaService {

    /**
     * 
     * @param file
     * @param targetType
     * @param targetId
     * @param collectionName
     * @return
     * @throws IOException
     */
    Media store(MultipartFile file, String targetType, Long targetId, String collectionName) throws IOException;

    /**
     * 
     * @param file
     * @param targetType
     * @param targetId
     * @param collectionName
     * @param conversions
     * @return
     * @throws IOException
     */
    Media storeWithConversions(MultipartFile file, String targetType, Long targetId, String collectionName,
            List<ImageConversion> conversions) throws IOException;

    /**
     * 
     * @param targetType
     * @param targetId
     * @param collectionName
     * @return
     */
    List<Media> getMediaForEntity(String targetType, Long targetId, String collectionName);

    /**
     * 
     * @param uuid
     */
    void deleteMedia(String uuid);

    /**
     * 
     * @param media
     * @return
     */
    public String buildPublicUrl(Media media);

    /**
     * Save or update media entity.
     * @param media
     * @return
     */
    Media saveMedia(Media media);
}
