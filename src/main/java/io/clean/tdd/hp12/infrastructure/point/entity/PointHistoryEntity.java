package io.clean.tdd.hp12.infrastructure.point.entity;

import io.clean.tdd.hp12.domain.point.enums.TransactionType;
import io.clean.tdd.hp12.domain.point.model.PointHistory;
import io.clean.tdd.hp12.infrastructure.user.entity.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_history", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id")
})
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
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity userEntity;

    public static PointHistoryEntity from(PointHistory pointHistory) {
        PointHistoryEntity pointHistoryEntity = new PointHistoryEntity();
        pointHistoryEntity.id = pointHistory.id();
        pointHistoryEntity.amount = pointHistory.amount();
        pointHistoryEntity.type = pointHistory.type();
        pointHistoryEntity.updatedAt = pointHistory.updatedAt();
        pointHistoryEntity.userEntity = UserEntity.from(pointHistory.user());

        return pointHistoryEntity;
    }

    public PointHistory toModel() {
        return PointHistory.builder()
            .id(id)
            .amount(amount)
            .type(type)
            .updatedAt(updatedAt)
            .user(userEntity.toModel())
            .build();
    }
}
