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

    //Tests for GET /api/RecommendationRequest/all

    @Test
    public void logged_out_users_cannot_get_all_recommendation_requests() throws Exception {
        mockMvc.perform(get("/api/RecommendationRequest/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles ={"USER"})
    @Test
    public void users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/RecommendationRequest/all"))
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

        MvcResult response = mockMvc.perform(get("/api/RecommendationRequest/all")).andExpect(status().is(200)).andReturn();

        verify(recommendationRequestRepository, times(1)).findAll();
        String expected1Json = mapper.writeValueAsString(expected1Recommendations);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expected1Json, responseString);

    }

    @Test
    public void logged_out_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/RecommendationRequest/post")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/RecommendationRequest/post")).andExpect(status().is(403));
    }


    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_recommendationrequest() throws Exception {

        LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

        RecommendationRequest expected1 = RecommendationRequest.builder()
                .id(0)
                .requesterEmail("a")
                .professorEmail("b")
                .explanation("c")
                .dateRequested(ldt1)
                .dateNeeded(ldt1)
                .done(true)
                .build();

        when(recommendationRequestRepository.save(eq(expected1))).thenReturn(expected1);

        MvcResult response = mockMvc.perform(post("/api/RecommendationRequest/post?requesterEmail=a&professorEmail=b&explanation=c&dateRequested=2022-01-03T00:00:00&dateNeeded=2022-01-03T00:00:00&done=true").with(csrf())).andExpect(status().is(200)).andReturn();

        verify(recommendationRequestRepository, times(1)).save(eq(expected1));
        String expected1Json = mapper.writeValueAsString(expected1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expected1Json, responseString);
    }

    // Tests for GET /api/RecommendationRequest?id=...

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
            mockMvc.perform(get("/api/RecommendationRequest?id=7"))
                            .andExpect(status().is(403)); // logged out users can't get by id
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

            // arrange
            LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

            RecommendationRequest expected1 = RecommendationRequest.builder()
                                        .id(0)
                                        .requesterEmail("a")
                                        .professorEmail("b")
                                        .explanation("c")
                                        .dateRequested(ldt)
                                        .dateNeeded(ldt)
                                        .done(true)
                                        .build();

            when(recommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.of(expected1));

            // act
            MvcResult response = mockMvc.perform(get("/api/RecommendationRequest?id=7"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(recommendationRequestRepository, times(1)).findById(eq(7L));
            String expectedJson = mapper.writeValueAsString(expected1);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

            // arrange

            when(recommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(get("/api/RecommendationRequest?id=7"))
                            .andExpect(status().isNotFound()).andReturn();

            // assert

            verify(recommendationRequestRepository, times(1)).findById(eq(7L));
            Map<String, Object> json = responseToJson(response);
            assertEquals("EntityNotFoundException", json.get("type"));
            assertEquals("RecommendationRequest with id 7 not found", json.get("message"));
    }

    // Tests for DELETE /api/RecommendationRequest?id=... 

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_a_rec() throws Exception {
            // arrange

            LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

            RecommendationRequest expected1 = RecommendationRequest.builder()
                                        .id(0)
                                        .requesterEmail("a")
                                        .professorEmail("b")
                                        .explanation("c")
                                        .dateRequested(ldt)
                                        .dateNeeded(ldt)
                                        .done(true)
                                        .build();

            when(recommendationRequestRepository.findById(eq(15L))).thenReturn(Optional.of(expected1));

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/RecommendationRequest?id=15")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).findById(15L);
            verify(recommendationRequestRepository, times(1)).delete(any());

            Map<String, Object> json = responseToJson(response);
            assertEquals("RecommendationRequest with id 15 deleted", json.get("message"));
    }
    
    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_tries_to_delete_non_existant_rec_and_gets_right_error_message()
                    throws Exception {
            // arrange

            when(recommendationRequestRepository.findById(eq(15L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/RecommendationRequest?id=15")
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).findById(15L);
            Map<String, Object> json = responseToJson(response);
            assertEquals("RecommendationRequest with id 15 not found", json.get("message"));
    }

}