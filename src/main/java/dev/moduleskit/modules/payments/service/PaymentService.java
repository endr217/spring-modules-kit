package dev.moduleskit.modules.payments.service;

import java.math.BigDecimal;

import dev.moduleskit.modules.payments.service.impl.PaymentServiceImpl.CheckoutResult;

public interface PaymentService {

    /**
     * Creates a Payment + starts provider checkout flow
     * @param orderId
     * @param amount
     * @param providerName
     * @param idempotencyKey
     * @return
     */
    public CheckoutResult createPaymentIntent(Long orderId, BigDecimal amount, String providerName, String idempotencyKey);

    /**
     * Called from payment webhook
     * @param providerPaymentId
     */
    public void confirmPayment(String providerPaymentId);
}
