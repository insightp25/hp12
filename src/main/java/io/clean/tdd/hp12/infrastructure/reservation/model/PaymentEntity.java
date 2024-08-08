package io.clean.tdd.hp12.infrastructure.reservation.model;

import io.clean.tdd.hp12.domain.reservation.enums.PaymentStatus;
import io.clean.tdd.hp12.domain.reservation.model.Payment;
import io.clean.tdd.hp12.domain.user.model.User;
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
    @JoinColumn(nullable = false)
    User user;

    public static PaymentEntity from(Payment payment) {
        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.id = payment.id();
        paymentEntity.amount = payment.amount();
        paymentEntity.status = payment.status();
        paymentEntity.createdAt = payment.createdAt();
        paymentEntity.user = payment.user();

        return paymentEntity;
    }

    public Payment toModel() {
        return Payment.builder()
            .id(id)
            .amount(amount)
            .status(status)
            .createdAt(createdAt)
            .user(user)
            .build();
    }
}
