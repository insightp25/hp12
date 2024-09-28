package io.clean.tdd.hp12.interfaces.reservation;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.clean.tdd.hp12.domain.reservation.ReservationService;
import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import io.clean.tdd.hp12.facade.reservation.ReservationFacade;
import io.clean.tdd.hp12.interfaces.reservation.request.ReservationFinalizeRequest;
import io.clean.tdd.hp12.interfaces.reservation.request.ReservationHoldRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReservationController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private ReservationFacade reservationFacade;

    private static final long RANDOM_ID_1L = 1L;
    private static final String RANDOM_UUID_STRING = UUID.randomUUID().toString();

    @Test
    void 사용자_정보와_콘서트_정보_및_희망_좌석_정보를_입력받아_좌석을_임시예약_처리하고_예약_정보를_반환할_수_있다() throws Exception {
        ReservationHoldRequest reservationHoldRequest = ReservationHoldRequest.builder()
            .userId(RANDOM_ID_1L)
            .concertId(RANDOM_ID_1L)
            .seatNumbers(new ArrayList<>(Arrays.asList(1, 2, 3)))
            .build();
        BDDMockito.given(reservationService.hold(anyLong(), anyLong(), anyList()))
            .willReturn(new ArrayList<>(Arrays.asList(
                Reservation.builder().build(),
                Reservation.builder().build(),
                Reservation.builder().build())));

        mockMvc.perform(post("/reservations/hold")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationHoldRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void 사용자_정보와_콘서트_좌석_결제정보_및_접근권한키를_입력받아_좌석_예약을_확정하고_예약_정보를_반환할_수_있다() throws Exception {
        ReservationFinalizeRequest reservationFinalizeRequest =
            ReservationFinalizeRequest.builder()
                .userId(RANDOM_ID_1L)
                .paymentId(RANDOM_ID_1L)
                .build();

        BDDMockito.given(reservationService.finalize(anyLong(), anyLong(), anyString()))
            .willReturn(new ArrayList<>(Arrays.asList(
                Reservation.builder().build(),
                Reservation.builder().build(),
                Reservation.builder().build())));

        mockMvc.perform(post("/reservations/finalize")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, RANDOM_UUID_STRING)
                .content(objectMapper.writeValueAsString(reservationFinalizeRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }
}
