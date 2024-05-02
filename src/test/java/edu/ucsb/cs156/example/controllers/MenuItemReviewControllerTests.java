package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.MenuItemReview;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.repositories.MenuItemReviewRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
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

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@WebMvcTest(controllers = MenuItemReviewController.class)
@Import(TestConfig.class)
public class MenuItemReviewControllerTests extends ControllerTestCase{
    @MockBean
    MenuItemReviewRepository menuItemReviewRepository;

    @MockBean
    UserRepository userRepository;

    // Tests for GET /api/menuitemreview/all
        
    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/menuitemreview/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/menuitemreview/all"))
                            .andExpect(status().is(200)); // logged
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_review() throws Exception {

            // arrange
            LocalDateTime ldt1 = LocalDateTime.parse("2000-01-03T00:00:00");

            MenuItemReview entry1 = MenuItemReview.builder()
                            .itemId(1)
                            .reviewerEmail("a")
                            .stars(5)
                            .dateReviewed(ldt1)
                            .comments("b")
                            .build();

            LocalDateTime ldt2 = LocalDateTime.parse("2000-03-11T00:00:00");

            MenuItemReview entry2 = MenuItemReview.builder()
                            .itemId(2)
                            .reviewerEmail("a")
                            .stars(1)
                            .dateReviewed(ldt2)
                            .comments("b")
                            .build();

            ArrayList<MenuItemReview> expectedReviews = new ArrayList<>();
            expectedReviews.addAll(Arrays.asList(entry1, entry2));

            when(menuItemReviewRepository.findAll()).thenReturn(expectedReviews);

            // act
            MvcResult response = mockMvc.perform(get("/api/menuitemreview/all"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(menuItemReviewRepository, times(1)).findAll();
            String expectedJson = mapper.writeValueAsString(expectedReviews);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    // Tests for POST /api/menuitemreview/post...

    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/menuitemreview/post"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/menuitemreview/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_review() throws Exception {
            // arrange

            LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

            MenuItemReview entry1 = MenuItemReview.builder()
                            .itemId(1)
                            .reviewerEmail("a@ucsb.edu")
                            .stars(5)
                            .dateReviewed(ldt1)
                            .comments("b")
                            .build();

            when(menuItemReviewRepository.save(entry1)).thenReturn(entry1);

            // act
            MvcResult response = mockMvc.perform(
                            post("/api/menuitemreview/post?itemId=1&reviewerEmail=a@ucsb.edu&stars=5&dateReviewed=2022-01-03T00:00:00&comments=b")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(menuItemReviewRepository, times(1)).save(entry1);
            String expectedJson = mapper.writeValueAsString(entry1);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

     // Tests for GET /api/ucsbdates?id=...

     @Test
     public void logged_out_users_cannot_get_by_id() throws Exception {
             mockMvc.perform(get("/api/menuitemreview?id=7"))
                             .andExpect(status().is(403)); // logged out users can't get by id
     }

     @WithMockUser(roles = { "USER" })
     @Test
     public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

             // arrange
             LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

             MenuItemReview entry1 = MenuItemReview.builder()
                                .itemId(1)
                                .reviewerEmail("a@ucsb.edu")
                                .stars(5)
                                .dateReviewed(ldt)
                                .comments("b")
                                .build();

             when(menuItemReviewRepository.findById(eq(7L))).thenReturn(Optional.of(entry1));

             // act
             MvcResult response = mockMvc.perform(get("/api/menuitemreview?id=7"))
                             .andExpect(status().isOk()).andReturn();

             // assert

             verify(menuItemReviewRepository, times(1)).findById(eq(7L));
             String expectedJson = mapper.writeValueAsString(entry1);
             String responseString = response.getResponse().getContentAsString();
             assertEquals(expectedJson, responseString);
     }

     @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

            // arrange

            when(menuItemReviewRepository.findById(eq(123L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(get("/api/menuitemreview?id=123"))
                            .andExpect(status().isNotFound()).andReturn();

            // assert

            verify(menuItemReviewRepository, times(1)).findById(eq(123L));
            Map<String, Object> json = responseToJson(response);
            assertEquals("EntityNotFoundException", json.get("type"));
            assertEquals("MenuItemReview with id 123 not found", json.get("message"));
    }

     // Tests for DELETE /api/ucsbdates?id=... 

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_menu() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                MenuItemReview entry1 = MenuItemReview.builder()
                                .itemId(1)
                                .reviewerEmail("a@ucsb.edu")
                                .stars(5)
                                .dateReviewed(ldt1)
                                .comments("b")
                                .build();

                when(menuItemReviewRepository.findById(eq(15L))).thenReturn(Optional.of(entry1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/menuitemreview?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).findById(15L);
                verify(menuItemReviewRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("MenuItemReview with id 15 deleted", json.get("message"));
        }
        
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_menu_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(menuItemReviewRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/menuitemreview?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("MenuItemReview with id 15 not found", json.get("message"));
        }

// Tests for PUT /api/menuitemreviews?id=... 

@WithMockUser(roles = { "ADMIN", "USER" })
@Test
public void admin_can_edit_an_existing_menu() throws Exception {
        // arrange

        LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
        LocalDateTime ldt2 = LocalDateTime.parse("2023-01-03T00:00:00");


        MenuItemReview orig = MenuItemReview.builder()
                        .itemId(1)
                        .reviewerEmail("@ucdu")
                        .stars(9)
                        .dateReviewed(ldt1)
                        .comments("asda")
                        .build();

        MenuItemReview edit = MenuItemReview.builder()
                        .itemId(1)
                        .reviewerEmail("edu")
                        .stars(1)
                        .dateReviewed(ldt2)
                        .comments("wadsd")
                        .build();

        String requestBody = mapper.writeValueAsString(edit);

        when(menuItemReviewRepository.findById(eq(67L))).thenReturn(Optional.of(orig));

        // act
        MvcResult response = mockMvc.perform(
                        put("/api/menuitemreview?id=67")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .characterEncoding("utf-8")
                                        .content(requestBody)
                                        .with(csrf()))
                        .andExpect(status().isOk()).andReturn();

        // assert
        verify(menuItemReviewRepository, times(1)).findById(67L);
        verify(menuItemReviewRepository, times(1)).save(edit); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(requestBody, responseString);
}

        
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_menu_that_does_not_exist() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                MenuItemReview entry1 = MenuItemReview.builder()
                                .itemId(1)
                                .reviewerEmail("a@ucsb.edu")
                                .stars(5)
                                .dateReviewed(ldt1)
                                .comments("b")
                                .build();

                String requestBody = mapper.writeValueAsString(entry1);

                when(menuItemReviewRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/menuitemreview?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("MenuItemReview with id 67 not found", json.get("message"));

        }
}