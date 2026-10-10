package dev.moduleskit.modules.translations.infrastructure.repository;

import java.util.Optional;

import dev.moduleskit.modules.translations.domain.Language;
import dev.moduleskit.shared.repository.BaseRepository;

public interface LanguageRepository extends BaseRepository<Language, Long> {

    /**
     * 
     * @param langCode
     * @return
     */
    Optional<Language> findByCode(String langCode);
}
