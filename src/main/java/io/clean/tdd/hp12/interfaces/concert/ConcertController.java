package io.clean.tdd.hp12.interfaces.concert;

import io.clean.tdd.hp12.domain.concert.ConcertService;
import io.clean.tdd.hp12.domain.concert.model.Concert;
import io.clean.tdd.hp12.domain.concert.model.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/concertTitles")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

    @GetMapping("/{concertTitleId}")
    public ResponseEntity<List<Concert>> concerts(
        @PathVariable("concertTitleId") long concertTitleId) {

        return ResponseEntity
            .ok()
            .body(concertService.find(concertTitleId));
    }

    @GetMapping("/{concertTitleId}/occasions/{occasion}")
    public ResponseEntity<List<Seat>> seats(
        @PathVariable("concertTitleId") long concertTitleId,
        @PathVariable("occasion")LocalDateTime occasion) {

        return ResponseEntity
            .ok()
            .body(concertService.findSeats(concertTitleId, occasion));
    }
}
