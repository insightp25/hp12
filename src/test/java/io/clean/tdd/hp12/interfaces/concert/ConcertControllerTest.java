package io.clean.tdd.hp12.interfaces.concert;

import io.clean.tdd.hp12.domain.concert.ConcertService;
import io.clean.tdd.hp12.domain.concert.model.Concert;
import io.clean.tdd.hp12.domain.concert.model.Seat;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConcertController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ConcertControllerTest {

    private static final long RANDOM_ID_1L = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConcertService concertService;

    @Test
    void 콘서트_메타_정보로_콘서트_목록을_조회할_수_있다() throws Exception {
        BDDMockito.given(concertService.find(anyLong()))
            .willReturn(new ArrayList<>(Arrays.asList(
                Concert.builder().build(),
                Concert.builder().build())));

        mockMvc.perform(get("/concertTitles/{concertTitleId}", RANDOM_ID_1L))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void 콘서트_메타_정보와_특정_날짜_정보로_해당_일에_예약가능한_좌석_목록을_불러올_수_있다() throws Exception {
        BDDMockito.given(concertService.findSeats(anyLong(), any()))
            .willReturn(new ArrayList<>(Arrays.asList(
                Seat.builder().build(),
                Seat.builder().build())));

        mockMvc.perform(get(
            "/concertTitles/{concertTitleId}/occasions/{occasion}",
                RANDOM_ID_1L,
                LocalDateTime.now().plusDays(7)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }
}
