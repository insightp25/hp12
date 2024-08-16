package io.clean.tdd.hp12.domain.data;

import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataPlatformService {

    public void sendReservationData(Reservation reservation) {
        log.info("::::::::: 외부 mock data platform 으로 reservation data 전송: 내용={}", reservation);
    }
}
