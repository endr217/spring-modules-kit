package dev.moduleskit.modules.payments.api.response;

public record CheckoutResponse(String providerPaymentId, String redirectUrl) {
}
