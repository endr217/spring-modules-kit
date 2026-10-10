package dev.moduleskit.modules.users.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import dev.moduleskit.modules.users.domain.User;
import dev.moduleskit.shared.repository.BaseRepository;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    /**
     *
     * @param email
     * @return
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     *
     * @param username
     * @return
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     *
     * @param email
     * @return
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     *
     * @param username
     * @return
     */
    boolean existsByUsernameIgnoreCase(String username);
}
