package dev.moduleskit.modules.payments.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import dev.moduleskit.modules.payments.domain.Payment;
import dev.moduleskit.shared.repository.BaseRepository;

@Repository
public interface PaymentRepository extends BaseRepository<Payment, Long> {
    Optional<Payment> findByProviderPaymentId(String providerPaymentId);
}
