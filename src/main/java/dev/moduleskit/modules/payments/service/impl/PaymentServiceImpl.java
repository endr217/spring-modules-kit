package dev.moduleskit.modules.payments.service.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Service;

import dev.moduleskit.modules.payments.domain.Payment;
import dev.moduleskit.modules.payments.enums.PaymentStatus;
import dev.moduleskit.modules.payments.infrastructure.factory.PaymentProviderFactory;
import dev.moduleskit.modules.payments.infrastructure.repository.PaymentRepository;
import dev.moduleskit.modules.payments.service.PaymentService;

import jakarta.transaction.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProviderFactory providerFactory;

    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentProviderFactory providerFactory) {
        this.paymentRepository = paymentRepository;
        this.providerFactory = providerFactory;
    }

    @Transactional
    @Override
    public CheckoutResult createPaymentIntent(Long orderId, BigDecimal amount, String providerName,
            String idempotencyKey) {
        var provider = (providerName == null || providerName.isBlank())
                ? providerFactory.getDefault()
                : providerFactory.getProvider(Enum.valueOf(
                        dev.moduleskit.modules.payments.enums.PaymentProvider.class, providerName));

        Map<String, String> metadata = Map.of("orderId", String.valueOf(orderId));
        String result = provider.createPaymentSession(orderId, amount, metadata); // "providerPaymentId::url"

        String[] parts = result.split("::", 2);
        String providerPaymentId = parts[0];
        String redirectUrl = parts.length > 1 ? parts[1] : "";

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setProvider(provider.getProviderType());
        payment.setProviderPaymentId(providerPaymentId);
        payment.setStatus(PaymentStatus.PENDING);

        // optionally set idempotencyKey in payment (if you added column)
        paymentRepository.save(payment);

        return new CheckoutResult(providerPaymentId, redirectUrl);
    }

    @Transactional
    @Override
    public void confirmPayment(String providerPaymentId) {
        paymentRepository.findByProviderPaymentId(providerPaymentId).ifPresentOrElse(payment -> {
            if (payment.getStatus() == PaymentStatus.PAID) {
                System.out.println("Payment already confirmed: " + providerPaymentId);
            } else {
                payment.setStatus(PaymentStatus.PAID);
                paymentRepository.save(payment);
                System.out.println("Payment confirmed: " + providerPaymentId);
            }
        }, () -> System.err.println("Payment not found for providerPaymentId: " + providerPaymentId));
    }

    public record CheckoutResult(String providerPaymentId, String redirectUrl) {
    }
}
