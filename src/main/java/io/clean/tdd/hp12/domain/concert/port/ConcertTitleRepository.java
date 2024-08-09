package io.clean.tdd.hp12.domain.concert.port;

import io.clean.tdd.hp12.domain.concert.model.ConcertTitle;

@Deprecated
public interface ConcertTitleRepository {

    ConcertTitle save(ConcertTitle concertTitle);
}
