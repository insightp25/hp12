package io.clean.tdd.hp12.infrastructure.concert.entity;

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
}
