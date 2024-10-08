package io.clean.tdd.hp12.domain.concert;

import io.clean.tdd.hp12.domain.concert.model.Concert;
import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.concert.port.ConcertRepository;
import io.clean.tdd.hp12.domain.concert.port.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;

    public List<Concert> find(long concertTitleId) {

        return concertRepository.findByConcertTitleId(concertTitleId);
    }

    public List<Seat> findSeats(long concertTitleId, LocalDateTime occasion) {
        Concert concert = concertRepository.findByConcertTitleIdAndOccasion(concertTitleId, occasion);

        return seatRepository.findByConcertId(concert.id());
    }
}
