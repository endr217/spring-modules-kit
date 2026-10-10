package dev.moduleskit.modules.payments.infrastructure.provider;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import dev.moduleskit.modules.payments.enums.PaymentProvider;

@Component
public class PayseraPaymentProviderClient implements PaymentProviderClient {

    @Value("${payments.paysera.project-id}")
    private String projectId;

    @Value("${payments.paysera.sign-password}")
    private String signPassword;

    @Value("${payments.success-url:http://localhost:3000/checkout/success}")
    private String successUrl;

    @Value("${payments.cancel-url:http://localhost:3000/checkout/cancel}")
    private String cancelUrl;

    @Override
    public String createPaymentSession(Long orderId, BigDecimal amount, Map<String, String> metadata) {
        // 1. Convert amount to smallest currency unit if needed
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

        // 2. Build Paysera redirect URL with parameters
        String payseraUrl = "https://www.paysera.com/pay/?projectid=" + projectId
                + "&orderid=" + orderId
                + "&amount=" + amountInCents
                + "&currency=EUR"
                + "&accepturl=" + successUrl
                + "&cancelurl=" + cancelUrl;

        // 3. Optionally add signature and metadata
        // TODO: compute MD5 signature if Paysera requires it
        // TODO: Implement actual Paysera signature generation
        // String signature = "signed_with_md5"; // placeholder

        return orderId + "::" + payseraUrl;
    }

    @Override
    public PaymentProvider getProviderType() {
        return PaymentProvider.PAYSERA;
    }
}
