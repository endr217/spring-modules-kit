package dev.moduleskit.modules.payments.service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Boundary between the payments module and whatever the host application
 * charges for (orders, invoices, subscriptions...).
 *
 * The host application must provide one bean implementing this interface.
 */
public interface PaymentOrderPort {

    /**
     * @param orderId
     * @return the order if it exists and is still awaiting payment
     */
    Optional<PayableOrder> findPayableOrder(Long orderId);

    /**
     * Called after payment confirmation (invoked by the webhook handlers)
     * @param orderId
     */
    void markOrderPaid(Long orderId);

    record PayableOrder(Long id, BigDecimal totalAmount) {
    }
}
