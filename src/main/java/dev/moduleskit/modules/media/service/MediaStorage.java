package dev.moduleskit.modules.media.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public interface MediaStorage {
    /**
     * 
     * @param file
     * @param path
     * @throws IOException
     */
    void save(MultipartFile file, Path path) throws IOException;

    /**
     * 
     * @param image
     * @param path
     * @param format
     * @throws IOException
     */
    void save(BufferedImage image, Path path, String format) throws IOException;

    /**
     * 
     * @param path
     * @throws IOException
     */
    void delete(Path path) throws IOException;
}

