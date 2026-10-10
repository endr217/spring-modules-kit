package dev.moduleskit.modules.payments.api.request;

public record CheckoutRequest(Long orderId, String provider /* "STRIPE" or "MOCK" */, String idempotencyKey) {
}