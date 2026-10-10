package dev.moduleskit.modules.payments.api.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.moduleskit.modules.payments.service.webhook.PaymentWebhookHandler;

@RestController
@RequestMapping("/payments/webhook")
public class PaymentWebhookController {

    private final Map<String, PaymentWebhookHandler> handlerMap;

    public PaymentWebhookController(List<PaymentWebhookHandler> handlers) {
        // Map provider name to handler for quick lookup
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(PaymentWebhookHandler::getProviderName, h -> h));
    }

    @PostMapping("/{provider}")
    public ResponseEntity<String> handleWebhook(
            @PathVariable String provider,
            @RequestHeader(value = "Stripe-Signature", required = false) String signatureHeader,
            @RequestBody String payload) {

        PaymentWebhookHandler handler = handlerMap.get(provider.toUpperCase());
        if (handler == null) {
            return ResponseEntity.status(404).body("No handler for provider " + provider);
        }

        return handler.handleWebhook(payload, signatureHeader);
    }
}
