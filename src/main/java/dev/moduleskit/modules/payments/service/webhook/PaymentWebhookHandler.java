package dev.moduleskit.modules.payments.service.webhook;

import org.springframework.http.ResponseEntity;

public interface PaymentWebhookHandler {
    /**
     * Handle a webhook call from a specific payment provider
     */
    ResponseEntity<String> handleWebhook(String payload, String signatureHeader);

    /**
     * Returns the provider name this handler supports (e.g., STRIPE, PAYSERA)
     */
    String getProviderName();
}
