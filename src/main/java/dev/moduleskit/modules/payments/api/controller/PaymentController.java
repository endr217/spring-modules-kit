package dev.moduleskit.modules.payments.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.moduleskit.modules.payments.api.request.CheckoutRequest;
import dev.moduleskit.modules.payments.api.response.CheckoutResponse;
import dev.moduleskit.modules.payments.service.PaymentOrderPort;
import dev.moduleskit.modules.payments.service.PaymentOrderPort.PayableOrder;
import dev.moduleskit.modules.payments.service.PaymentService;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentOrderPort orderPort;

    public PaymentController(PaymentService paymentService, PaymentOrderPort orderPort) {
        this.paymentService = paymentService;
        this.orderPort = orderPort;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody CheckoutRequest req) {
        PayableOrder order = orderPort.findPayableOrder(req.orderId()).orElse(null);

        if (order == null) {
            return ResponseEntity.badRequest().build();
        }

        var result = paymentService.createPaymentIntent(
                order.id(),
                order.totalAmount(),
                req.provider(),
                req.idempotencyKey()
        );

        return ResponseEntity.ok(new CheckoutResponse(result.providerPaymentId(), result.redirectUrl()));
    }
}
