package dev.moduleskit.modules.media.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import dev.moduleskit.modules.media.domain.Media;
import dev.moduleskit.shared.repository.BaseRepository;

@Repository
public interface MediaRepository extends BaseRepository<Media, Long> {
    
    List<Media> findByTargetTypeAndTargetIdAndCollectionNameOrderByOrderColumnAsc(
            String targetType, Long targetId, String collectionName);

    Optional<Media> findByUuid(String uuid);
}
