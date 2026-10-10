package dev.moduleskit.modules.payments.service.webhook;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dev.moduleskit.modules.payments.service.PaymentOrderPort;
import dev.moduleskit.modules.payments.service.PaymentService;

@Service
public class PayseraWebhookHandler implements PaymentWebhookHandler {

    private final PaymentService paymentService;
    private final PaymentOrderPort orderPort;

    @Value("${payments.paysera.project-id}")
    private String projectId;

    @Value("${payments.paysera.sign-password}")
    private String signPassword;

    public PayseraWebhookHandler(PaymentService paymentService, PaymentOrderPort orderPort) {
        this.paymentService = paymentService;
        this.orderPort = orderPort;
    }

    @Override
    public ResponseEntity<String> handleWebhook(String payload, String signatureHeader) {
        // Parse payload as Map<String,String> (Paysera sends form-encoded params)
        Map<String, String> params = parsePayload(payload);
        String orderIdStr = params.get("orderid");
        String paymentStatus = params.get("status"); // e.g., 'success'
        String providerPaymentId = orderIdStr;

        if (!verifySignature(params)) {
            return ResponseEntity.status(400).body("Invalid signature");
        }

        if ("success".equalsIgnoreCase(paymentStatus)) {
            paymentService.confirmPayment(providerPaymentId);
            orderPort.markOrderPaid(Long.valueOf(orderIdStr));
        }
        return ResponseEntity.ok("OK");
    }

    private boolean verifySignature(Map<String, String> params) {
        // TODO: implement Paysera MD5/sha signature verification
        // Fails closed until then: an unverified callback must never mark an order as paid.
        return false;
    }

    private Map<String, String> parsePayload(String payload) {
        // Simple parse logic: key=value&key2=value2
        return Arrays.stream(payload.split("&"))
                .map(s -> s.split("=", 2))
                .collect(Collectors.toMap(a -> a[0], a -> URLDecoder.decode(a[1], StandardCharsets.UTF_8)));
    }

    @Override
    public String getProviderName() {
        return "PAYSERA";
    }
}
