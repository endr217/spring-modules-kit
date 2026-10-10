package dev.moduleskit.modules.media.service;

import org.springframework.web.multipart.MultipartFile;

public interface MediaValidator {
    /**
     * 
     * @param file
     */
    public void validateContentType(MultipartFile file);

    /**
     * 
     * @param file
     */
    public void validateImage(MultipartFile file);
}
