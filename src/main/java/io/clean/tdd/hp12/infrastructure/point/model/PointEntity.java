package io.clean.tdd.hp12.infrastructure.point.model;

import io.clean.tdd.hp12.domain.user.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "point")
public class PointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long point;

    @Column(nullable = false)
    LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(nullable = false)
    User user;
}
