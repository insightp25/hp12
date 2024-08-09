package io.clean.tdd.hp12.domain.concert.port;

import io.clean.tdd.hp12.domain.concert.model.SeatOption;

@Deprecated
public interface SeatOptionRepository {

    SeatOption save(SeatOption seatOption);
}
