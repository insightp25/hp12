package io.clean.tdd.hp12.infrastructure.concert.model;

import io.clean.tdd.hp12.domain.concert.model.ConcertTitle;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "concert")
public class ConcertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    LocalDateTime occasion;

    @ManyToOne
    @JoinColumn(nullable = false)
    ConcertTitle concertTitle;
}
