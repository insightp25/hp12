package io.clean.tdd.hp12.interfaces.queue;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.clean.tdd.hp12.common.BusinessPolicies;
import io.clean.tdd.hp12.domain.queue.WaitingQueueService;
import io.clean.tdd.hp12.domain.queue.enums.WaitingQueueStatus;
import io.clean.tdd.hp12.domain.queue.model.WaitingQueue;
import io.clean.tdd.hp12.domain.user.model.User;
import io.clean.tdd.hp12.interfaces.queue.request.TokenIssueRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WaitingQueueController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class WaitingQueueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WaitingQueueService waitingQueueService;

    private static final long SAMPLE_ID_1L = 1L;
    private static final String SAMPLE_ACCESS_KEY_STRING = "sample-access-key";

    @Test
    void 사용자_정보를_입력받아_사용자의_대기열_토큰을_발급하고_토큰_정보를_반환할_수_있다() throws Exception{
        TokenIssueRequest tokenIssueRequest = TokenIssueRequest.builder()
            .userId(SAMPLE_ID_1L)
            .build();
        User user = User.builder()
            .id(SAMPLE_ID_1L)
            .build();
        WaitingQueue waitingQueue = WaitingQueue.builder()
            .id(SAMPLE_ID_1L)
            .accessKey(SAMPLE_ACCESS_KEY_STRING)
            .status(WaitingQueueStatus.WAITING)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .lastAccessAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .expireAt(LocalDateTime.now().plusMinutes(BusinessPolicies.WAITING_TOKEN_DURATION_MINUTES).truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .build();
        BDDMockito.given(waitingQueueService.push(anyLong()))
            .willReturn(waitingQueue);

        mockMvc.perform(post("/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenIssueRequest)))
            .andDo(print())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.accessKey").value("sample-access-key"))
            .andExpect(jsonPath("$.status").value("WAITING"))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.lastAccessAt").exists())
            .andExpect(jsonPath("$.expireAt").exists())
            .andExpect(jsonPath("$.user.id").value(1L));
    }
}
