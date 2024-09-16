package io.clean.tdd.hp12.infrastructure.reservation;

import io.clean.tdd.hp12.domain.reservation.model.Payment;
import io.clean.tdd.hp12.domain.reservation.port.PaymentRepository;
import io.clean.tdd.hp12.infrastructure.reservation.entity.PaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment findById(long id) {
        return paymentJpaRepository.findById(id)
            .toModel();
    }

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(PaymentEntity.from(payment))
            .toModel();
    }
}
