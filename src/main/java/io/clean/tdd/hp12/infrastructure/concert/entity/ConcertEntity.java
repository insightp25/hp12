package io.clean.tdd.hp12.infrastructure.concert.entity;

import io.clean.tdd.hp12.domain.concert.model.Concert;
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

    public Concert toModel() {
        return Concert.builder()
            .id(id)
            .occasion(occasion)
            .concertTitle(concertTitle)
            .build();
    }
}
