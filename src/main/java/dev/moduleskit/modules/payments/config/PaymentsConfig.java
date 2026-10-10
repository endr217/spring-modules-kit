package dev.moduleskit.modules.payments.config;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.moduleskit.modules.payments.service.PaymentOrderPort;

@Configuration
public class PaymentsConfig {

    /**
     * Placeholder so the context starts without a host application.
     * Replace it by declaring your own PaymentOrderPort bean.
     */
    @Bean
    @ConditionalOnMissingBean(PaymentOrderPort.class)
    public PaymentOrderPort paymentOrderPort() {
        return new PaymentOrderPort() {
            @Override
            public Optional<PayableOrder> findPayableOrder(Long orderId) {
                throw new IllegalStateException("No PaymentOrderPort bean defined - implement it in your application");
            }

            @Override
            public void markOrderPaid(Long orderId) {
                throw new IllegalStateException("No PaymentOrderPort bean defined - implement it in your application");
            }
        };
    }
}
