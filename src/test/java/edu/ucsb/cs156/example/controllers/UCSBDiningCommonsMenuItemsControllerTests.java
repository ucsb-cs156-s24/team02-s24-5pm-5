package edu.ucsb.cs156.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItems;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemsRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = UCSBDiningCommonsMenuItemsController.class)
@Import(TestConfig.class)
public class UCSBDiningCommonsMenuItemsControllerTests
    extends ControllerTestCase {

    @MockBean
    UCSBDiningCommonsMenuItemsRepository ucsbDiningCommonsMenuItemsRepository;

    @MockBean
    UserRepository userRepository;

    // Tests for GET /api/ucsbdiningcommonsmenuitems/all

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc
            .perform(get("/api/ucsbdiningcommonsmenuitems/all"))
            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
        mockMvc
            .perform(get("/api/ucsbdiningcommonsmenuitems/all"))
            .andExpect(status().is(200)); // logged
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_ucsbdiningcommonsmenuitems()
        throws Exception {
        // arrange

        UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItems1 =
            UCSBDiningCommonsMenuItems.builder()
                .diningCommonsCode("ortega")
                .name("Baked Pesto Pasta with Chicken")
                .station("Entree Specials")
                .build();

        UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItems2 =
            UCSBDiningCommonsMenuItems.builder()
                .diningCommonsCode("ortega")
                .name("Tofu Banh Mi Sandwich (v)")
                .station("Entree Specials")
                .build();

        ArrayList<UCSBDiningCommonsMenuItems> expectedDiningCommonsMenuItems =
            new ArrayList<>();
        expectedDiningCommonsMenuItems.addAll(
            Arrays.asList(
                ucsbDiningCommonsMenuItems1,
                ucsbDiningCommonsMenuItems2
            )
        );

        when(ucsbDiningCommonsMenuItemsRepository.findAll()).thenReturn(
            expectedDiningCommonsMenuItems
        );

        // act
        MvcResult response = mockMvc
            .perform(get("/api/ucsbdiningcommonsmenuitems/all"))
            .andExpect(status().isOk())
            .andReturn();

        // assert

        verify(ucsbDiningCommonsMenuItemsRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(
            expectedDiningCommonsMenuItems
        );
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    // Tests for POST /api/ucsbdiningcommonsmenuitems/post...

    @Test
    public void logged_out_users_cannot_post() throws Exception {
        mockMvc
            .perform(post("/api/ucsbdiningcommonsmenuitems/post"))
            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
        mockMvc
            .perform(post("/api/ucsbdiningcommonsmenuitems/post"))
            .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_ucsbdiningcommonsmenuitems()
        throws Exception {
        // arrange

        UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItems1 =
            UCSBDiningCommonsMenuItems.builder()
                .diningCommonsCode("ortega")
                .name("Chicken Caesar Salad")
                .station("Entree")
                .build();

        when(
            ucsbDiningCommonsMenuItemsRepository.save(
                eq(ucsbDiningCommonsMenuItems1)
            )
        ).thenReturn(ucsbDiningCommonsMenuItems1);

        // act
        MvcResult response = mockMvc
            .perform(
                post(
                    "/api/ucsbdiningcommonsmenuitems/post?diningCommonsCode=ortega&name=Chicken Caesar Salad&station=Entree"
                ).with(csrf())
            )
            .andExpect(status().isOk())
            .andReturn();

        // assert
        verify(ucsbDiningCommonsMenuItemsRepository, times(1)).save(
            ucsbDiningCommonsMenuItems1
        );
        String expectedJson = mapper.writeValueAsString(
            ucsbDiningCommonsMenuItems1
        );
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    // Tests for GET /api/ucsbdiningcommonsmenuitems?id=...

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
        mockMvc
            .perform(get("/api/ucsbdiningcommonsmenuitems?id=7"))
            .andExpect(status().is(403)); // logged out users can't get by id
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists()
        throws Exception {
        // arrange
        UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItem =
            UCSBDiningCommonsMenuItems.builder()
                .diningCommonsCode("ortega")
                .name("Chicken Caesar Salad")
                .station("Entree")
                .build();

        when(ucsbDiningCommonsMenuItemsRepository.findById(eq(7L))).thenReturn(
            Optional.of(ucsbDiningCommonsMenuItem)
        );

        // act
        MvcResult response = mockMvc
            .perform(get("/api/ucsbdiningcommonsmenuitems?id=7"))
            .andExpect(status().isOk())
            .andReturn();

        // assert

        verify(ucsbDiningCommonsMenuItemsRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(
            ucsbDiningCommonsMenuItem
        );
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist()
        throws Exception {
        // arrange

        when(ucsbDiningCommonsMenuItemsRepository.findById(eq(7L))).thenReturn(
            Optional.empty()
        );

        // act
        MvcResult response = mockMvc
            .perform(get("/api/ucsbdiningcommonsmenuitems?id=7"))
            .andExpect(status().isNotFound())
            .andReturn();

        // assert

        verify(ucsbDiningCommonsMenuItemsRepository, times(1)).findById(eq(7L));
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals(
            "UCSBDiningCommonsMenuItems with id 7 not found",
            json.get("message")
        );
    }

    // Tests for DELETE /api/ucsbdiningcommonsmenuitems?id=...

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_a_dining_commons_menu_item() throws Exception {
        // arrange

        UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItem =
            UCSBDiningCommonsMenuItems.builder()
                .diningCommonsCode("ortega")
                .name("Chicken Caesar Salad")
                .station("Entree")
                .build();

        when(ucsbDiningCommonsMenuItemsRepository.findById(eq(15L))).thenReturn(
            Optional.of(ucsbDiningCommonsMenuItem)
        );

        // act
        MvcResult response = mockMvc
            .perform(
                delete("/api/ucsbdiningcommonsmenuitems?id=15").with(csrf())
            )
            .andExpect(status().isOk())
            .andReturn();

        // assert
        verify(ucsbDiningCommonsMenuItemsRepository, times(1)).findById(15L);
        verify(ucsbDiningCommonsMenuItemsRepository, times(1)).delete(any());

        Map<String, Object> json = responseToJson(response);
        assertEquals(
            "UCSBDiningCommonsMenuItems with id 15 deleted",
            json.get("message")
        );
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_tries_to_delete_non_existant_ucsbdiningcommonsmenuitems_and_gets_right_error_message()
        throws Exception {
        // arrange

        when(ucsbDiningCommonsMenuItemsRepository.findById(eq(15L))).thenReturn(
            Optional.empty()
        );

        // act
        MvcResult response = mockMvc
            .perform(
                delete("/api/ucsbdiningcommonsmenuitems?id=15").with(csrf())
            )
            .andExpect(status().isNotFound())
            .andReturn();

        // assert
        verify(ucsbDiningCommonsMenuItemsRepository, times(1)).findById(15L);
        Map<String, Object> json = responseToJson(response);
        assertEquals(
            "UCSBDiningCommonsMenuItems with id 15 not found",
            json.get("message")
        );
    }

    // Tests for PUT /api/ucsbdiningcommonsmenuitems?id=...

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_edit_an_existing_ucsbdiningcommonsmenuitem()
        throws Exception {
        // arrange
        UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItemsOrig =
            UCSBDiningCommonsMenuItems.builder()
                .diningCommonsCode("ortega")
                .name("Chicken Noodle Soup")
                .station("Entree")
                .build();

        UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItemsEdited =
            UCSBDiningCommonsMenuItems.builder()
                .diningCommonsCode("portola")
                .name("Banana Pie")
                .station("Dessert")
                .build();

        String requestBody = mapper.writeValueAsString(
            ucsbDiningCommonsMenuItemsEdited
        );

        when(ucsbDiningCommonsMenuItemsRepository.findById(eq(67L))).thenReturn(
            Optional.of(ucsbDiningCommonsMenuItemsOrig)
        );

        // act
        MvcResult response = mockMvc
            .perform(
                put("/api/ucsbdiningcommonsmenuitems?id=67")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestBody)
                    .with(csrf())
            )
            .andExpect(status().isOk())
            .andReturn();

        // assert
        verify(ucsbDiningCommonsMenuItemsRepository, times(1)).findById(67L);
        verify(ucsbDiningCommonsMenuItemsRepository, times(1)).save(
            ucsbDiningCommonsMenuItemsEdited
        ); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(requestBody, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_cannot_edit_ucsbdiningcommonsmenuitems_that_does_not_exist()
        throws Exception {
        // arrange

        UCSBDiningCommonsMenuItems ucsbEditedDiningCommonsMenuItems =
            UCSBDiningCommonsMenuItems.builder()
                .diningCommonsCode("ortega")
                .name("Chicken Noodle Soup")
                .station("Entree")
                .build();

        String requestBody = mapper.writeValueAsString(
            ucsbEditedDiningCommonsMenuItems
        );

        when(ucsbDiningCommonsMenuItemsRepository.findById(eq(67L))).thenReturn(
            Optional.empty()
        );

        // act
        MvcResult response = mockMvc
            .perform(
                put("/api/ucsbdiningcommonsmenuitems?id=67")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestBody)
                    .with(csrf())
            )
            .andExpect(status().isNotFound())
            .andReturn();

        // assert
        verify(ucsbDiningCommonsMenuItemsRepository, times(1)).findById(67L);
        Map<String, Object> json = responseToJson(response);
        assertEquals(
            "UCSBDiningCommonsMenuItems with id 67 not found",
            json.get("message")
        );
    }
}
