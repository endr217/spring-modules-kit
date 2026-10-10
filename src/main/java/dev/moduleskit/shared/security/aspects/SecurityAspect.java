package dev.moduleskit.shared.security.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import dev.moduleskit.security.CustomUserDetails;
import dev.moduleskit.shared.audit.AuditLogger;

/**
 * Spring AOP aspect implementing custom security annotations.
 *
 * 1. @RequiresAdmin: Role-based access control for admin operations
 *
 * All intercepted denials logged to audit trail for compliance.
 */
@Aspect
@Component
public class SecurityAspect {
    private static final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);
    private final AuditLogger auditLogger;

    public SecurityAspect(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    /**
     * Pointcut for @RequiresAdmin annotation
     */
    @Pointcut("@annotation(dev.moduleskit.shared.security.annotations.RequiresAdmin)")
    public void requiresAdminMethods() {
    }

    /**
     * Aspect for @RequiresAdmin: ensures user has ADMIN role
     */
    @Around("requiresAdminMethods()")
    public Object checkAdminRole(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            logSecurityDenial(joinPoint, "User not authenticated");
            throw new AccessDeniedException("User must be authenticated to perform this operation");
        }

        Object principal = auth.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            logSecurityDenial(joinPoint, "Invalid principal type");
            throw new AccessDeniedException("Invalid authentication");
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;

        // Check ADMIN role only
        boolean hasAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!hasAdmin) {
            logSecurityDenial(joinPoint, "User does not have ADMIN role", userDetails.getId());
            throw new AccessDeniedException("User must have ADMIN role to perform this operation");
        }

        logger.info("ASPECT: @RequiresAdmin granted for user {} on method {}",
            userDetails.getId(), joinPoint.getSignature().getName());

        return joinPoint.proceed();
    }

    private void logSecurityDenial(JoinPoint joinPoint, String reason) {
        logSecurityDenial(joinPoint, reason, null);
    }

    private void logSecurityDenial(JoinPoint joinPoint, String reason, Long userId) {
        String message = String.format("SECURITY DENIAL: Method=%s, Reason=%s, UserId=%s",
            joinPoint.getSignature().getName(), reason, userId != null ? userId : "UNKNOWN");
        logger.warn(message);

        if (userId != null) {
            auditLogger.logSecurityEvent("AUTHORIZATION_FAILURE", userId.toString(), reason);
        }
    }
}
