package dev.moduleskit.modules.media.api.response;

import java.util.Map;

import dev.moduleskit.modules.media.domain.Media;

public record MediaResponse(
        String uuid,
        String url,
        Map<String, String> conversions) {
    public static MediaResponse fromMedia(Media media, String publicUrl) {
        return new MediaResponse(
                media.getUuid(),
                publicUrl,
                media.getGeneratedConversions());
    }
}
