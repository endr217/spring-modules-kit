package dev.moduleskit.shared.audit;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    public void logCartOperation(Long userId, String action, String details) {
        auditLogger.info("CART_OP|userId={}|action={}|timestamp={}|details={}", userId, action,
                Instant.now(), details);
    }

    public void logOrderOperation(Long userId, Long orderId, String action, String details) {
        auditLogger.info("ORDER_OP|userId={}|orderId={}|action={}|timestamp={}|details={}", userId, orderId,
                action, Instant.now(), details);
    }

    public void logPaymentOperation(Long orderId, String action, String details) {
        auditLogger.info("PAYMENT_OP|orderId={}|action={}|timestamp={}|details={}", orderId, action,
                Instant.now(), details);
    }

    public void logUserProfileUpdate(Long userId, String fieldUpdated, String details) {
        auditLogger.info("PROFILE_UPDATE|userId={}|field={}|timestamp={}|details={}", userId, fieldUpdated,
                Instant.now(), details);
    }

    public void logSecurityEvent(String eventType, String userId, String details) {
        auditLogger.warn("SECURITY_EVENT|type={}|userId={}|timestamp={}|details={}", eventType, userId,
                Instant.now(), details);
    }
}
