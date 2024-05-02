package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import liquibase.pro.packaged.eq;
import lombok.With;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

@WebMvcTest(RecommendationRequestController.class)
@Import(TestConfig.class)
public class RecommendationRequestControllerTests extends ControllerTestCase {
    @MockBean
    RecommendationRequestRepository recommendationRequestRepository;

    @MockBean
    UserRepository userRepository;

    //Tests for GET /api/recommendationrequests/all

    @Test
    public void logged_out_users_cannot_get_all_recommendation_requests() throws Exception {
        mockMvc.perform(get("/api/recommendationrequests/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles ={"USER"})
    @Test
    public void users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/recommendationrequests/all"))
                .andExpect(status().is(200));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void users_can_get_all_recommendation_requests() throws Exception {

        RecommendationRequest expected1 = new RecommendationRequest();
        expected1.setId(0);
        expected1.setRequesterEmail("requesterEmail");
        expected1.setProfessorEmail("professorEmail");
        expected1.setExplanation("explanation");
        expected1.setDateRequested(LocalDateTime.parse("2024-04-26T08:00:00"));
        expected1.setDateNeeded(LocalDateTime.parse("2024-04-27T08:08:00"));
        expected1.setDone(false);

        RecommendationRequest expected12 = new RecommendationRequest();
        expected12.setId(1);
        expected12.setRequesterEmail("requesterEmail2");
        expected12.setProfessorEmail("professorEmail2");
        expected12.setExplanation("explanation2");
        expected12.setDateRequested(LocalDateTime.parse("2024-04-26T08:00:00"));
        expected12.setDateNeeded(LocalDateTime.parse("2024-04-27T08:08:00"));
        expected12.setDone(false);

        ArrayList<RecommendationRequest> expected1Recommendations = new ArrayList<>();
        expected1Recommendations.addAll(Arrays.asList(expected1, expected12));

        when(recommendationRequestRepository.findAll()).thenReturn(expected1Recommendations);

        MvcResult response = mockMvc.perform(get("/api/recommendationrequests/all")).andExpect(status().is(200)).andReturn();

        verify(recommendationRequestRepository, times(1)).findAll();
        String expected1Json = mapper.writeValueAsString(expected1Recommendations);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expected1Json, responseString);

    }

    @Test
    public void logged_out_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/recommendationrequests/post")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/recommendationrequests/post")).andExpect(status().is(403));
    }


    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_recommendationrequest() throws Exception {
        RecommendationRequest expected1 = new RecommendationRequest();
        expected1.setId(0);
        expected1.setRequesterEmail("requesterEmail");
        expected1.setProfessorEmail("professorEmail");
        expected1.setExplanation("explanation");
        expected1.setDateRequested(LocalDateTime.parse("2024-04-26T08:08:00"));
        expected1.setDateNeeded( LocalDateTime.parse("2024-04-27T08:08:00"));
        expected1.setDone(false);

        when(recommendationRequestRepository.save(eq(expected1))).thenReturn(expected1);

        MvcResult response = mockMvc.perform(post("/api/recommendationrequests/post?requesterEmail=requesterEmail&professorEmail=professorEmail&explanation=explanation&dateRequested=2024-04-26T08:08:00&dateNeeded=2024-04-27T08:08:00&done=true").with(csrf())).andExpect(status().is(200)).andReturn();

        verify(recommendationRequestRepository, times(1)).save(eq(expected1));
        String expected1Json = mapper.writeValueAsString(expected1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expected1Json, responseString);
    }


}