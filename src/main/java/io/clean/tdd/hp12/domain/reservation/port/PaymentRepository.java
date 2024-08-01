package io.clean.tdd.hp12.domain.reservation.port;

import io.clean.tdd.hp12.domain.reservation.model.Payment;

public interface PaymentRepository {
    void save(Payment payment);
}
