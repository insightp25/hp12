package io.clean.tdd.hp12.infrastructure.point.model;

import io.clean.tdd.hp12.domain.point.enums.TransactionType;
import io.clean.tdd.hp12.domain.user.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_history")
public class PointHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TransactionType type;

    @Column(nullable = false)
    LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    User user;
}
