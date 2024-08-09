package io.clean.tdd.hp12.domain.concert.port;

import io.clean.tdd.hp12.domain.concert.model.Concert;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertRepository {
    List<Concert> findByConcertTitleId(long concertTitleId);

    Concert findByConcertTitleIdAndOccasion(long concertTitleId, LocalDateTime occasion);

    Concert save(Concert concert);
}
