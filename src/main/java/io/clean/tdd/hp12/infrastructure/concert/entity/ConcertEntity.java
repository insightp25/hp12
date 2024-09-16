package io.clean.tdd.hp12.infrastructure.concert.entity;

import io.clean.tdd.hp12.domain.concert.model.Concert;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "concert", indexes = {
    @Index(name = "idx_concert_title_id", columnList = "concert_title_id")
})
public class ConcertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    LocalDateTime occasion;

    @ManyToOne
    @JoinColumn(name = "concert_title_id", nullable = false)
    ConcertTitleEntity concertTitleEntity;

    public static ConcertEntity from(Concert concert) {
        ConcertEntity concertEntity = new ConcertEntity();
        concertEntity.id = concert.id();
        concertEntity.occasion = concert.occasion();
        concertEntity.concertTitleEntity = ConcertTitleEntity.from(concert.concertTitle());

        return concertEntity;
    }

    public Concert toModel() {
        return Concert.builder()
            .id(id)
            .occasion(occasion)
            .concertTitle(concertTitleEntity.toModel())
            .build();
    }
}
