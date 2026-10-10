package dev.moduleskit.modules.media.service.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dev.moduleskit.modules.media.service.MediaStorage;

@Service
public class LocalMediaStorage implements MediaStorage {

    @Override
    public void save(MultipartFile file, Path path) throws IOException {
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void save(BufferedImage image, Path path, String format) throws IOException {
        ImageIO.write(image, format, path.toFile());
    }

    @Override
    public void delete(Path path) throws IOException {
        Files.deleteIfExists(path);
    }
}
