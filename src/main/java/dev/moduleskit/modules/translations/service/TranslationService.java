package dev.moduleskit.modules.translations.service;

import java.util.Map;

public interface TranslationService {
    
    /**
     * 
     * @param entity
     * @param entityId
     * @param field
     * @return
     */
    Map<String, String> getAllTranslations(String entity, Long entityId, String field);
    
    /**
     * 
     * @param entity
     * @param entityId
     * @param field
     * @param langCode
     * @param text
     */
    void saveTranslation(String entity, Long entityId, String field, String langCode, String text);
}
