package dev.moduleskit.modules.users.infrastructure.seeder;

import dev.moduleskit.bootstrap.Seeder;
import dev.moduleskit.modules.users.domain.Role;
import dev.moduleskit.modules.users.enums.RoleEnum;
import dev.moduleskit.modules.users.infrastructure.repository.RoleRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Seeds default roles into the database
 */
@Component
@Order(1)
public class RoleSeeder implements Seeder {

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void seed() {
        // Skip if already seeded
        if (roleRepository.count() > 0) {
            return;
        }
        
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (!roleRepository.existsByName(roleEnum.name())) {
                Role role = new Role(roleEnum.name(), roleEnum.getDescription());
                roleRepository.save(role);
            }
        }
    }
}
