package io.clean.tdd.hp12.domain.reservation.port;

import io.clean.tdd.hp12.domain.reservation.model.Payment;

public interface PaymentRepository {
    Payment findById(long id);

    Payment save(Payment payment);
}
