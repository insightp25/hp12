package io.clean.tdd.hp12.infrastructure.concert.model;

import io.clean.tdd.hp12.domain.concert.enums.SeatStatus;
import io.clean.tdd.hp12.domain.concert.model.Concert;
import io.clean.tdd.hp12.domain.concert.model.SeatOption;
import jakarta.persistence.*;

@Entity
@Table(name = "seat")
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SeatStatus status;

    @Column(nullable = false)
    Integer seatNumber;

    @ManyToOne
    @JoinColumn(nullable = false)
    SeatOption seatOption;

    @ManyToOne
    @JoinColumn(nullable = false)
    Concert concert;
}
