package dev.moduleskit.modules.translations.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import dev.moduleskit.modules.translations.domain.Translation;
import dev.moduleskit.shared.repository.BaseRepository;

@Repository
public interface TranslationRepository extends BaseRepository<Translation, Long> {

    List<Translation> findByEntityAndEntityId(String entity, Long entityId);

    Optional<Translation> findByEntityAndEntityIdAndFieldAndLanguage_Code(
            String entity, Long entityId, String field, String languageCode);

    List<Translation> findByEntityAndEntityIdAndField(String entity, Long entityId, String field);
}
