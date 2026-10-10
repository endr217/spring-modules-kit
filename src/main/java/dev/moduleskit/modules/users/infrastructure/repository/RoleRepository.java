package dev.moduleskit.modules.users.infrastructure.repository;

import org.springframework.stereotype.Repository;
import dev.moduleskit.modules.users.domain.Role;
import dev.moduleskit.shared.repository.BaseRepository;
import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}
