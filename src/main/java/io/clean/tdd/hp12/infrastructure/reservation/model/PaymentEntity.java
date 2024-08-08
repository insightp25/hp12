package io.clean.tdd.hp12.infrastructure.reservation.model;

import io.clean.tdd.hp12.domain.reservation.enums.PaymentStatus;
import io.clean.tdd.hp12.domain.reservation.model.Payment;
import io.clean.tdd.hp12.domain.user.model.User;
import io.clean.tdd.hp12.infrastructure.user.entity.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PaymentStatus status;

    @Column(nullable = false)
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity userEntity;

    public static PaymentEntity from(Payment payment) {
        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.id = payment.id();
        paymentEntity.amount = payment.amount();
        paymentEntity.status = payment.status();
        paymentEntity.createdAt = payment.createdAt();
        paymentEntity.userEntity = UserEntity.from(payment.user());

        return paymentEntity;
    }

    public Payment toModel() {
        return Payment.builder()
            .id(id)
            .amount(amount)
            .status(status)
            .createdAt(createdAt)
            .user(userEntity.toModel())
            .build();
    }
}
