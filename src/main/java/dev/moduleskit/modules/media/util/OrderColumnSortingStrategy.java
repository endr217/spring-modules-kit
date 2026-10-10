package dev.moduleskit.modules.media.util;

import java.util.List;

import dev.moduleskit.modules.media.domain.Media;

/**
 * Sorts media by orderColumn in ascending order.
 * Used for galleries where insertion order matters (e.g., item product galleries).
 * 
 * Media without an orderColumn are placed at the end.
 */
public class OrderColumnSortingStrategy implements MediaSortingStrategy {

    @Override
    public List<Media> sort(List<Media> media) {
        return media.stream()
            .sorted((a, b) -> {
                Integer orderA = a.getOrderColumn() != null ? a.getOrderColumn() : Integer.MAX_VALUE;
                Integer orderB = b.getOrderColumn() != null ? b.getOrderColumn() : Integer.MAX_VALUE;
                return orderA.compareTo(orderB);
            })
            .toList();
    }

    @Override
    public String getName() {
        return "OrderColumnAscending";
    }
}
