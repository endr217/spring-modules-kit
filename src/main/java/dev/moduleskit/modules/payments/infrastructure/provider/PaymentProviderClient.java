package dev.moduleskit.modules.payments.infrastructure.provider;

import java.math.BigDecimal;
import java.util.Map;

import dev.moduleskit.modules.payments.enums.PaymentProvider;

public interface PaymentProviderClient {

    /**
     * Creates a payment/checkout session in external provider.
     * @return providerPaymentId (e.g. Stripe sessionId)
     */
    String createPaymentSession(Long orderId, BigDecimal amount, Map<String, String> metadata);

    PaymentProvider getProviderType();
}

