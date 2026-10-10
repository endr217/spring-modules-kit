package dev.moduleskit.modules.media.profile;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import dev.moduleskit.modules.media.api.request.ImageConversion;
import dev.moduleskit.modules.media.config.MediaProfilesProperties;

@Component
public class ProfileRegistry {
    private final MediaProfilesProperties properties;

    public ProfileRegistry(MediaProfilesProperties properties) {
        this.properties = properties;
    }

    /**
     * Resolves conversions for given targetType and collectionName.
     * Precedence:
     * 1) YAML config media.profiles[Target][collection]
     * 2) fallback
     */
    public List<ImageConversion> resolve(String targetType, String collectionName) {
        // 1) YAML
        var profiles = properties.getProfiles();
        if (profiles != null) {
            var targetMap = profiles.get(targetType);
            if (targetMap != null) {
                var list = targetMap.get(collectionName);
                if (list != null && !list.isEmpty()) {
                    return list.stream()
                            .map(p -> new ImageConversion(p.getName(), p.getWidth(), p.getHeight(), p.getFormat()))
                            .collect(Collectors.toList());
                }
            }
        }

        // 2) fallback
        return List.of(
                            new ImageConversion("thumb", 100, 100, "webp"),
                            new ImageConversion("medium", 300, 300, "webp"),
                            new ImageConversion("large", 800, 800, "webp"),
                            new ImageConversion("extra_large", 1600, 1200, "webp"));
    }
}
