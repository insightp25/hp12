package io.clean.tdd.hp12.interfaces.point;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.clean.tdd.hp12.domain.point.PointService;
import io.clean.tdd.hp12.domain.point.model.Point;
import io.clean.tdd.hp12.domain.point.model.PointHistory;
import io.clean.tdd.hp12.domain.user.model.User;
import io.clean.tdd.hp12.interfaces.point.request.PointRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PointController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PointService pointService;

    private final long SAMPLE_ID_1L = 1L;

    @Test
    void 사용자_정보를_입력받아_사용자_포인트를_조회할_수_있다() throws Exception {
        Point point = Point.builder()
            .id(SAMPLE_ID_1L)
            .point(1_000L)
            .build();

        given(pointService.getOf(anyLong()))
            .willReturn(point);

        mockMvc.perform(get("/points")
                .param("userId", String.valueOf(SAMPLE_ID_1L)))
            .andDo(print())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.point").value(1_000L));
    }

    @Test
    void 사용자_정보를_입력받아_사용자_포인트_충전_및_사용_내역을_조회할_수_있다() throws Exception {
        given(pointService.getHistoriesOf(anyLong()))
            .willReturn(new ArrayList<>(Arrays.asList(
                PointHistory.builder().build(),
                PointHistory.builder().build())));

        mockMvc.perform(get("/points/history")
                .param("userId", String.valueOf(SAMPLE_ID_1L)))
            .andDo(print())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void 사용자_정보와_포인트_충전_액수를_입력받아_포인트를_충전하고_충전후_잔여_포인트를_조회할_수_있다() throws Exception {
        Point point = Point.builder()
            .point(2_000L)
            .build();
        PointRequest pointRequest = PointRequest.builder()
            .userId(SAMPLE_ID_1L)
            .amount(1_000L)
            .build();

        when(pointService.charge(pointRequest.userId(), pointRequest.amount()))
            .thenReturn(Point.builder()
                .point(point.point() + pointRequest.amount())
                .build());

        mockMvc.perform(post("/points/charge")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(pointRequest)))
            .andDo(print())
            .andExpect(jsonPath("$.point").value(3_000L));
    }

    @Test
    void 사용자_정보와_포인트_사용_액수를_입력받아_포인트를_사용하고_사용후_잔여_포인트를_조회할_수_있다() throws Exception {
        Point point = Point.builder()
            .point(3_000L)
            .build();
        PointRequest pointRequest = PointRequest.builder()
            .userId(SAMPLE_ID_1L)
            .amount(1_000L)
            .build();

        when(pointService.use(pointRequest.userId(), pointRequest.amount()))
            .thenReturn(Point.builder()
                .point(point.point() - pointRequest.amount())
                .build());

        mockMvc.perform(post("/points/use")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pointRequest)))
            .andDo(print())
            .andExpect(jsonPath("$.point").value(2_000L));
    }
}
