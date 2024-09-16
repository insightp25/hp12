package io.clean.tdd.hp12.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.clean.tdd.hp12.domain.concert.enums.SeatStatus;
import io.clean.tdd.hp12.domain.concert.model.Concert;
import io.clean.tdd.hp12.domain.concert.model.ConcertTitle;
import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.concert.model.SeatOption;
import io.clean.tdd.hp12.domain.concert.port.ConcertRepository;
import io.clean.tdd.hp12.domain.concert.port.ConcertTitleRepository;
import io.clean.tdd.hp12.domain.concert.port.SeatOptionRepository;
import io.clean.tdd.hp12.domain.concert.port.SeatRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Disabled // 부하 테스트의 데이터셋과 충돌을 일으키므로 임시 비활성화
@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConcertTitleRepository concertTitleRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SeatOptionRepository seatOptionRepository;

    public static final LocalDateTime SEVEN_DAYS_AHEAD_FROM_NOW =
        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(7);
    public static final LocalDateTime EIGHT_DAYS_AHEAD_FROM_NOW =
        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(8);

    @Test
    void 콘서트_메타_정보로_콘서트_목록을_조회할_수_있다() throws Exception {
        ConcertTitle concertTitle = concertTitleRepository.save(ConcertTitle.builder()
            .title("인천펜타포트 락 페스티벌")
            .description("대한민국을 대표하는 글로벌 문화관광축제 인천펜타포트 락 페스티벌! 3일간 송도달빛축제공원에서 개최됩니다.")
            .build());
        Concert concert1 = concertRepository.save(Concert.builder()
            .occasion(SEVEN_DAYS_AHEAD_FROM_NOW)
            .concertTitle(concertTitle)
            .build());
        Concert concert2 = concertRepository.save(Concert.builder()
            .occasion(EIGHT_DAYS_AHEAD_FROM_NOW)
            .concertTitle(concertTitle)
            .build());
        SeatOption seatOptionStandard = seatOptionRepository.save(SeatOption.builder()
            .classifiedAs("STANDARD")
            .price(100_000L)
            .build());
        Seat seatOfConcert1 = seatRepository.save(Seat.builder()
            .status(SeatStatus.AVAILABLE)
            .seatNumber(1)
            .concert(concert1)
            .seatOption(seatOptionStandard)
            .build());
        Seat seatOfConcert2 = seatRepository.save(Seat.builder()
            .status(SeatStatus.AVAILABLE)
            .seatNumber(1)
            .concert(concert2)
            .seatOption(seatOptionStandard)
            .build());

        MvcResult mvcResult = mockMvc.perform(
                get("/concertTitles/{concertTitleId}", concertTitle.id()))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<Concert> result = objectMapper.readValue(contentAsString, new TypeReference<>() {});

        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result.get(0).concertTitle().title()).isEqualTo("인천펜타포트 락 페스티벌");
        Assertions.assertThat(result.get(0).occasion()).isEqualTo(SEVEN_DAYS_AHEAD_FROM_NOW);
        Assertions.assertThat(result.get(1).concertTitle().title()).isEqualTo("인천펜타포트 락 페스티벌");
        Assertions.assertThat(result.get(1).occasion()).isEqualTo(EIGHT_DAYS_AHEAD_FROM_NOW);
    }
}
