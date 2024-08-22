package io.clean.tdd.hp12.infrastructure.point.entity;

import io.clean.tdd.hp12.domain.point.model.Point;
import io.clean.tdd.hp12.infrastructure.user.entity.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "point", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id")
})
public class PointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long point;

    @Column(nullable = false)
    LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity userEntity;

    public static PointEntity from(Point point) {
        PointEntity pointEntity = new PointEntity();
        pointEntity.id = point.id();
        pointEntity.point = point.point();
        pointEntity.updatedAt = point.updatedAt();
        pointEntity.userEntity = UserEntity.from(point.user());

        return pointEntity;
    }
    public Point toModel() {
        return Point.builder()
            .id(id)
            .point(point)
            .updatedAt(updatedAt)
            .user(userEntity.toModel())
            .build();
    }
}
