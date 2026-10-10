package dev.moduleskit.modules.media.util;

import java.util.List;

import dev.moduleskit.modules.media.domain.Media;

/**
 * Sorts media by createdAt in descending order (newest first).
 * Used for single-avatar scenarios where only the latest version should be prominent (e.g., user profiles).
 * 
 * Useful for profile pictures, thumbnails, and other single-media-per-entity use cases.
 */
public class CreatedAtDescendingSortingStrategy implements MediaSortingStrategy {

    @Override
    public List<Media> sort(List<Media> media) {
        return media.stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .toList();
    }

    @Override
    public String getName() {
        return "CreatedAtDescending";
    }
}
