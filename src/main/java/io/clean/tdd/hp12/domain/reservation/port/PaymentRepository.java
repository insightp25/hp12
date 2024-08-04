package io.clean.tdd.hp12.domain.reservation.port;

import io.clean.tdd.hp12.domain.reservation.model.Payment;

public interface PaymentRepository {
    Payment findById(long id);

    void save(Payment payment);

    void update(Payment payment);
}
