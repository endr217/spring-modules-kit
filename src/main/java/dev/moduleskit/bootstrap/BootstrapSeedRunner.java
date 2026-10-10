package dev.moduleskit.bootstrap;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class BootstrapSeedRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapSeedRunner.class);

    private final ApplicationContext applicationContext;
    private final boolean seedingEnabled;

    public BootstrapSeedRunner(
            ApplicationContext applicationContext,
            @Value("${app.seed.enabled:false}") boolean seedingEnabled) {
        this.applicationContext = applicationContext;
        this.seedingEnabled = seedingEnabled;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runSeeders() {
        if (!seedingEnabled) {
            log.debug("Database seeding is disabled");
            return;
        }

        List<Seeder> seeders = applicationContext
                .getBeansOfType(Seeder.class)
                .values()
                .stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .toList();

        for (Seeder seeder : seeders) {
            executeSeeder(seeder);
        }
    }

    private void executeSeeder(Seeder seeder) {
        try {
            log.info("Running seeder: {}", seeder.getClass().getSimpleName());
            seeder.seed();
        } catch (Exception ex) {
            log.error(
                    "Seeder {} failed, continuing startup",
                    seeder.getClass().getSimpleName(),
                    ex);
        }
    }
}
