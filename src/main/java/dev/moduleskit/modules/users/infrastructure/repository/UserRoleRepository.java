package dev.moduleskit.modules.users.infrastructure.repository;

import org.springframework.stereotype.Repository;
import dev.moduleskit.modules.users.domain.UserRole;
import dev.moduleskit.shared.repository.BaseRepository;
import java.util.List;

@Repository
public interface UserRoleRepository extends BaseRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);
    void deleteByUserIdAndRoleId(Long userId, Long roleId);
}
