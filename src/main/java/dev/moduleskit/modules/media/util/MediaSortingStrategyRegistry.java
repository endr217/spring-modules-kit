package dev.moduleskit.modules.media.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Registry for media sorting strategies.
 * Maps target types (e.g., "USER", "ITEM") to their preferred sorting strategy.
 * 
 * This allows decoupling business logic from the media module.
 * New entity types can register their sorting preference without modifying the media module itself.
 * 
 * Default behavior: OrderColumnAscending (preserves insertion order)
 */
@Component
public class MediaSortingStrategyRegistry {

    private final Map<String, MediaSortingStrategy> strategies = new HashMap<>();
    private final MediaSortingStrategy defaultStrategy = new OrderColumnSortingStrategy();

    public MediaSortingStrategyRegistry() {
        // Register built-in strategies
        registerStrategy("USER", new CreatedAtDescendingSortingStrategy());
        registerStrategy("ITEM", new OrderColumnSortingStrategy());
    }

    /**
     * Register a sorting strategy for a target type.
     * Can be called by other modules to customize sorting behavior.
     * 
     * @param targetType the entity type (e.g., "USER", "ITEM", "COLLECTION")
     * @param strategy the sorting strategy to apply
     */
    public void registerStrategy(String targetType, MediaSortingStrategy strategy) {
        strategies.put(targetType.toUpperCase(), strategy);
    }

    /**
     * Retrieve the sorting strategy for a target type.
     * Returns the default strategy (OrderColumnAscending) if no specific strategy is registered.
     * 
     * @param targetType the entity type
     * @return the applicable sorting strategy
     */
    public MediaSortingStrategy getStrategy(String targetType) {
        return strategies.getOrDefault(targetType.toUpperCase(), defaultStrategy);
    }
}
