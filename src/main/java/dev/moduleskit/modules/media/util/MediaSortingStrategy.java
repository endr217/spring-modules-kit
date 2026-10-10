package dev.moduleskit.modules.media.util;

import java.util.List;

import dev.moduleskit.modules.media.domain.Media;

/**
 * Strategy interface for media sorting.
 * Allows different sorting behaviors for different entity types without hardcoding logic.
 * 
 * This follows the Strategy Pattern and supports extensibility similar to Laravel's Spatie Media Library.
 * New sorting strategies can be registered without modifying existing code.
 */
public interface MediaSortingStrategy {
    
    /**
     * Sort media list according to the strategy's logic.
     * 
     * @param media the unsorted media list
     * @return sorted media list
     */
    List<Media> sort(List<Media> media);
    
    /**
     * Get the name of this strategy for logging/debugging.
     */
    String getName();
}
