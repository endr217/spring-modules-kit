package dev.moduleskit.modules.payments.service.impl;

import org.springframework.stereotype.Service;

import dev.moduleskit.modules.payments.service.PaymentOrderPort;
import dev.moduleskit.modules.payments.service.PaymentService;
import dev.moduleskit.modules.payments.service.StripeEventService;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;

@Service
public class StripeEventServiceImpl implements StripeEventService {

    private final PaymentService paymentService;
    private final PaymentOrderPort orderPort;

    public StripeEventServiceImpl(PaymentService paymentService, PaymentOrderPort orderPort) {
        this.paymentService = paymentService;
        this.orderPort = orderPort;
    }

    public void handleEvent(Event event) {
        switch (event.getType()) {
            case "checkout.session.completed" -> handleCheckoutCompleted(event);
        }
    }

    private void handleCheckoutCompleted(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        deserializer.getObject().ifPresentOrElse(obj -> {
            Session session = (Session) obj;
            String providerPaymentId = session.getId();
            System.out.println("Webhook received for session: " + providerPaymentId);

            try {
                paymentService.confirmPayment(providerPaymentId);
            } catch (Exception e) {
                System.err.println("Payment confirm failed for " + providerPaymentId);
                e.printStackTrace();
            }

            String orderIdStr = session.getMetadata().get("orderId");
            if (orderIdStr != null) {
                try {
                    orderPort.markOrderPaid(Long.valueOf(orderIdStr));
                } catch (Exception e) {
                    System.err.println("Mark order paid failed for orderId " + orderIdStr);
                    e.printStackTrace();
                }
            } else {
                System.err.println("OrderId metadata missing for session: " + providerPaymentId);
            }

        }, () -> System.err.println("Could not deserialize event data object: " + event.getId()));
    }
}
