package com.youshop.copilot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IncidentApiTest {

    @Autowired MockMvc mvc;

    @Test
    void listsFourScenarios() throws Exception {
        mvc.perform(get("/api/scenarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void masksPiiAndReturnsThreeSolutions() throws Exception {
        mvc.perform(get("/api/incidents/INC-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.analysis.solutions.length()").value(3))
                .andExpect(jsonPath("$.analysis.solutions[0].risk").value("BAIXO"))
                .andExpect(content().string(not(containsString("123.456.789-09"))))
                .andExpect(content().string(not(containsString("maria.silva@email.com"))))
                .andExpect(content().string(not(containsString("sk_live_"))))
                .andExpect(content().string(containsString("LGPD_MASKING")));
    }

    @Test
    void neutralizesPromptInjectionAndScript() throws Exception {
        mvc.perform(get("/api/incidents/INC-1002"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("PROMPT_INJECTION")))
                .andExpect(content().string(not(containsString("<script"))))
                .andExpect(content().string(not(containsString("IGNORE ALL PREVIOUS"))));
    }

    @Test
    void unknownIncidentReturns404() throws Exception {
        mvc.perform(get("/api/incidents/INC-9999")).andExpect(status().isNotFound());
    }

    @Test
    void registersFeedback() throws Exception {
        mvc.perform(post("/api/incidents/INC-1003/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":\"👍\",\"comment\":\"Diagnóstico certeiro\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value("THUMBS_UP"));
    }

    @Test
    void rejectsInvalidRating() throws Exception {
        mvc.perform(post("/api/incidents/INC-1003/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":\"meh\"}"))
                .andExpect(status().isBadRequest());
    }
}
