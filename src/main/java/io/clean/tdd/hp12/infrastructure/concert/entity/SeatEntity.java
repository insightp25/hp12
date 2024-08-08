package io.clean.tdd.hp12.infrastructure.concert.entity;

import io.clean.tdd.hp12.domain.concert.enums.SeatStatus;
import io.clean.tdd.hp12.domain.concert.model.Concert;
import io.clean.tdd.hp12.domain.concert.model.Seat;
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

    public static SeatEntity from(Seat seat) {
        SeatEntity seatEntity = new SeatEntity();
        seatEntity.id = seat.id();
        seatEntity.status = seat.status();
        seatEntity.seatNumber = seat.seatNumber();
        seatEntity.seatOption = seat.seatOption();
        seatEntity.concert = seat.concert();

        return seatEntity;
    }

    public Seat toModel() {
        return Seat.builder()
            .id(id)
            .status(status)
            .seatNumber(seatNumber)
            .seatOption(seatOption)
            .concert(concert)
            .build();
    }
}
