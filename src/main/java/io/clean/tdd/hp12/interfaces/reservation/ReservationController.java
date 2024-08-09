package io.clean.tdd.hp12.interfaces.reservation;

import io.clean.tdd.hp12.domain.reservation.ReservationService;
import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import io.clean.tdd.hp12.interfaces.reservation.request.ReservationFinalizeRequest;
import io.clean.tdd.hp12.interfaces.reservation.request.ReservationHoldRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/hold")
    public ResponseEntity<List<Reservation>> hold(
        @RequestBody ReservationHoldRequest reservationHoldRequest) {

        return ResponseEntity
            .ok()
            .body(reservationService.hold(
                reservationHoldRequest.userId(),
                reservationHoldRequest.concertId(),
                reservationHoldRequest.seatNumbers()));
    }

    @PostMapping("/finalize")
    public ResponseEntity<List<Reservation>> finalization(
        @RequestBody ReservationFinalizeRequest reservationFinalizeRequest,
        @RequestHeader(HttpHeaders.AUTHORIZATION) String accessKey) {

        return ResponseEntity
            .ok()
            .body(reservationService.finalize(
                reservationFinalizeRequest.userId(),
                reservationFinalizeRequest.paymentId(),
                accessKey));
    }
}
