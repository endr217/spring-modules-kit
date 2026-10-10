package dev.moduleskit.modules.payments.infrastructure.provider;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import dev.moduleskit.modules.payments.enums.PaymentProvider;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;

@Component
public class StripePaymentProviderClient implements PaymentProviderClient {

    @Value("${payments.stripe.api-key}")
    private String stripeApiKey;

    @Value("${payments.success-url:http://localhost:3000/checkout/success}")
    private String successUrl;

    @Value("${payments.cancel-url:http://localhost:3000/checkout/cancel}")
    private String cancelUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public String createPaymentSession(Long orderId, BigDecimal amount, Map<String, String> metadata) {
        // Build a minimal single-line item session. In real product, pass line items and currency.
        SessionCreateParams.LineItem.PriceData.ProductData product = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName("Order #" + orderId)
                .build();

        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(product)
                                                .build()
                                ).build()
                )
                .putMetadata("orderId", String.valueOf(orderId))
                .build();

        try {
            Session session = Session.create(params);
            // session.getId() is providerPaymentId, session.getUrl() redirect url
            return session.getId() + "::" + session.getUrl(); // return both delimited
        } catch (Exception e) {
            throw new RuntimeException("Stripe session creation failed", e);
        }
    }

    @Override
    public PaymentProvider getProviderType() {
        return PaymentProvider.STRIPE;
    }
}