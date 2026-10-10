package dev.moduleskit.modules.media.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "media")
public class MediaProfilesProperties {
    // profiles -> targetType -> collectionName -> list of conversion props
    private Map<String, Map<String, List<MediaConversionProps>>> profiles = new LinkedHashMap<>();

    public Map<String, Map<String, List<MediaConversionProps>>> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<String, Map<String, List<MediaConversionProps>>> profiles) {
        this.profiles = profiles;
    }

    public static class MediaConversionProps {
        private String name;
        private int width;
        private int height;
        private String format;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }
}
