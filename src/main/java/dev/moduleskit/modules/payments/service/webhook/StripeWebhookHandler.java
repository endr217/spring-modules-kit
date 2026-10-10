package dev.moduleskit.modules.payments.service.webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dev.moduleskit.modules.payments.service.StripeEventService;
import com.stripe.model.Event;

@Service
public class StripeWebhookHandler implements PaymentWebhookHandler {

    private final StripeEventService stripeEventService;

    @Value("${payments.stripe.webhook-secret}")
    private String stripeWebhookSecret;

    public StripeWebhookHandler(StripeEventService stripeEventService) {
        this.stripeEventService = stripeEventService;
    }

    @Override
    public ResponseEntity<String> handleWebhook(String payload, String signatureHeader) {
        try {
            Event event = com.stripe.net.Webhook.constructEvent(payload, signatureHeader, stripeWebhookSecret);
            stripeEventService.handleEvent(event);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid signature or event");
        }
        return ResponseEntity.ok("received");
    }

    @Override
    public String getProviderName() {
        return "STRIPE";
    }
}

