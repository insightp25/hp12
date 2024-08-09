package io.clean.tdd.hp12.infrastructure.concert.entity;

import io.clean.tdd.hp12.domain.concert.model.SeatOption;
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

    public static SeatOptionEntity from(SeatOption seatOption) {
        SeatOptionEntity seatOptionEntity = new SeatOptionEntity();
        seatOptionEntity.id = seatOption.id();
        seatOptionEntity.classifiedAs = seatOption.classifiedAs();
        seatOptionEntity.price = seatOption.price();

        return seatOptionEntity;
    }

    public SeatOption toModel() {
        return SeatOption.builder()
            .id(id)
            .classifiedAs(classifiedAs)
            .price(price)
            .build();
    }
}
