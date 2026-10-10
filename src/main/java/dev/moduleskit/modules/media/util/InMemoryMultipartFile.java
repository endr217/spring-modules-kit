package dev.moduleskit.modules.media.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.springframework.web.multipart.MultipartFile;

public class InMemoryMultipartFile implements MultipartFile {

    private final String name;
    private final byte[] content;

    public InMemoryMultipartFile(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    @Override
    @org.springframework.lang.NonNull
    @SuppressWarnings("null")
    public String getName() {
        return name;
    }

    @Override
    @org.springframework.lang.NonNull
    @SuppressWarnings("null")
    public String getOriginalFilename() {
        return name;
    }

    @Override
    @org.springframework.lang.NonNull
    public String getContentType() {
        return "image/jpeg";
    }

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    @org.springframework.lang.NonNull
    @SuppressWarnings("null")
    public byte[] getBytes() {
        return content;
    }

    @Override
    @org.springframework.lang.NonNull
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(@org.springframework.lang.NonNull File dest) throws IOException {
        Files.write(dest.toPath(), content);
    }
}
