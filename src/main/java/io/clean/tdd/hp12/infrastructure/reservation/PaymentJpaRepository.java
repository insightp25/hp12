package io.clean.tdd.hp12.infrastructure.reservation;

import io.clean.tdd.hp12.infrastructure.reservation.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {

    PaymentEntity findById(long id);
}
