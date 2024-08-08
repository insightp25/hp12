package io.clean.tdd.hp12.infrastructure.concert.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "seat_option")
public class SeatOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String classifiedAs;

    @Column(nullable = false)
    Long price;
}
