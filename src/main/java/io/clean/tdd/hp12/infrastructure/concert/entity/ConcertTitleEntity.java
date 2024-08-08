package io.clean.tdd.hp12.infrastructure.concert.entity;

import io.clean.tdd.hp12.domain.concert.model.ConcertTitle;
import jakarta.persistence.*;

@Entity
@Table(name = "concert_title")
public class ConcertTitleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String title;

    @Column(nullable = false)
    String description;

    public static ConcertTitleEntity from(ConcertTitle concertTitle) {
        ConcertTitleEntity concertTitleEntity = new ConcertTitleEntity();
        concertTitleEntity.id = concertTitle.id();
        concertTitleEntity.title = concertTitle.title();
        concertTitleEntity.description = concertTitle.description();

        return concertTitleEntity;
    }

    public ConcertTitle toModel() {
        return ConcertTitle.builder()
            .id(id)
            .title(title)
            .description(description)
            .build();
    }
}
