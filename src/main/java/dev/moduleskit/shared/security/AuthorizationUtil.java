package dev.moduleskit.shared.security;

import org.springframework.security.access.AccessDeniedException;

public class AuthorizationUtil {

    public static void ensureOwnership(Long resourceOwnerId, Long userId) {
        if (!resourceOwnerId.equals(userId)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
    }

    public static void ensureAuthenticated(Long userId) {
        if (userId == null) {
            throw new AccessDeniedException("Authentication required");
        }
    }
}
