package dev.moduleskit.modules.translations.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import dev.moduleskit.modules.translations.domain.Language;
import dev.moduleskit.modules.translations.domain.Translation;
import dev.moduleskit.modules.translations.infrastructure.repository.LanguageRepository;
import dev.moduleskit.modules.translations.infrastructure.repository.TranslationRepository;
import dev.moduleskit.modules.translations.service.TranslationService;

@Service
public class TranslationServiceImpl implements TranslationService {

    private final TranslationRepository translationRepository;
    private final LanguageRepository languageRepository;

    public TranslationServiceImpl(TranslationRepository translationRepository, LanguageRepository languageRepository) {
        this.translationRepository = translationRepository;
        this.languageRepository = languageRepository;
    }

    @Override
    public Map<String, String> getAllTranslations(String entity, Long entityId, String field) {
        List<Translation> translations = translationRepository.findByEntityAndEntityIdAndField(entity, entityId, field);
        Map<String, String> result = new HashMap<>();
        for (Translation t : translations) {
            result.put(t.getLanguage().getCode(), t.getText());
        }
        return result;
    }

    @Override
    public void saveTranslation(String entity, Long entityId, String field, String langCode, String text) {
        Language language = languageRepository.findByCode(langCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown language: " + langCode));

        Translation translation = translationRepository.findByEntityAndEntityIdAndFieldAndLanguage_Code(entity, entityId, field, langCode)
                .orElseGet(() -> {
                    Translation t = new Translation();
                    t.setEntity(entity);
                    t.setEntityId(entityId);
                    t.setField(field);
                    t.setLanguage(language);
                    return t;
                });

        // Only save if text actually changed
        if (!text.equals(translation.getText())) {
            translation.setText(text);
            translationRepository.save(translation);
        }
    }
}
