package dev.moduleskit.modules.translations.infrastructure.seeder;

import java.util.Arrays;
import java.util.List;

import dev.moduleskit.bootstrap.Seeder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import dev.moduleskit.modules.translations.domain.Language;
import dev.moduleskit.modules.translations.enums.LanguageCode;
import dev.moduleskit.modules.translations.infrastructure.repository.LanguageRepository;

@Component
@Order(3)
public class LanguageSeeder implements Seeder {

    private final LanguageRepository languageRepository;

    public LanguageSeeder(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    @Override
    @SuppressWarnings("null")
    public void seed() {
        if (languageRepository.count() == 0) {
            List<Language> languages = Arrays.stream(LanguageCode.values())
                    .map(code -> new Language(null, code.getCode(), code.getDisplayName()))
                    .toList();

            languageRepository.saveAll(languages);
        }
    }
}

