package dev.moduleskit.modules.payments.infrastructure.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import dev.moduleskit.modules.payments.enums.PaymentProvider;
import dev.moduleskit.modules.payments.infrastructure.provider.PaymentProviderClient;

@Component
public class PaymentProviderFactory {

    private final List<PaymentProviderClient> providers;

    public PaymentProviderFactory(List<PaymentProviderClient> providers) {
        this.providers = providers;
    }

    public PaymentProviderClient getProvider(PaymentProvider type) {
        return providers.stream()
                .filter(p -> p.getProviderType().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
    }

    /** default provider */
    public PaymentProviderClient getDefault() {
        return getProvider(PaymentProvider.STRIPE);
    }
}

