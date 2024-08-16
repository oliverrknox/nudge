package net.gb.knox.nudge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.gb.knox.nudge.domain.Communication;
import net.gb.knox.nudge.domain.CreateTrigger;
import net.gb.knox.nudge.domain.Period;
import net.gb.knox.nudge.domain.UpsertNudge;
import net.gb.knox.nudge.fixture.NudgeFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NudgeControllerIntegrationTests {

    @Autowired
    private MockMvc api;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    @DirtiesContext
    @WithMockUser(username = NudgeFixture.USER_ID)
    public void testValidGetAllNudgesForUser() throws Exception {
        var getRequest = get("/nudges");
        api.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.nudges.length()").value(2));

        getRequest.queryParam("field", "title");
        getRequest.queryParam("direction", "DESC");
        api.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.nudges.length()").value(2));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = NudgeFixture.USER_ID)
    public void testValidCreateNudgeForUser() throws Exception {
        var createNudge = new UpsertNudge("Title", "Description",
                LocalDate.of(2025, 1, 1),
                List.of(new CreateTrigger(Period.DAY, 1, Optional.empty())));
        var json = objectMapper.writeValueAsString(createNudge);

        var createRequest = post("/nudges").with(csrf()).contentType("application/json").content(json);
        api.perform(createRequest).andExpect(status().isCreated()).andExpect(header().exists("location"));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = NudgeFixture.USER_ID)
    public void testValidUpdateNudgeForUser() throws Exception {
        var updateNudge = new UpsertNudge("NewTitle", "NewDescription",
                LocalDate.of(2025, 1, 1),
                List.of(new CreateTrigger(Period.DAY, 1, Optional.of(Communication.ASSERTIVE))));
        var json = objectMapper.writeValueAsString(updateNudge);

        var updateRequest = put("/nudges/1").with(csrf()).contentType("application/json").content(json);
        api.perform(updateRequest).andExpect(status().isOk());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = NudgeFixture.USER_ID)
    public void testValidDeleteNudgeForUser() throws Exception {
        var deleteRequest = delete("/nudges/1").with(csrf());
        api.perform(deleteRequest).andExpect(status().isNoContent());
    }
}
