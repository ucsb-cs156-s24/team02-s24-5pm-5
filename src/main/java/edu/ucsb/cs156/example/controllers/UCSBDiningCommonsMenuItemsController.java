package edu.ucsb.cs156.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItems;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UCSBDiningCommonsMenuItems")
@RequestMapping("/api/ucsbdiningcommonsmenuitems")
@RestController
@Slf4j
public class UCSBDiningCommonsMenuItemsController extends ApiController {

    @Autowired
    UCSBDiningCommonsMenuItemsRepository ucsbDiningCommonsMenuItemsRepository;

    @Operation(summary = "List all ucsb dining commons menu items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<
        UCSBDiningCommonsMenuItems
    > allUCSBDiningCommonsMenuItems() {
        Iterable<UCSBDiningCommonsMenuItems> diningCommonsMenuItems =
            ucsbDiningCommonsMenuItemsRepository.findAll();
        return diningCommonsMenuItems;
    }

    @Operation(summary = "Create a new dining commons menu item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBDiningCommonsMenuItems postUCSBDiningCommonsMenuItem(
        @Parameter(
            name = "diningCommonsCode"
        ) @RequestParam String diningCommonsCode,
        @Parameter(name = "name") @RequestParam String name,
        @Parameter(name = "station") @RequestParam String station
    ) throws JsonProcessingException {
        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        // log.info("localDateTime={}", localDateTime);

        UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItem =
            new UCSBDiningCommonsMenuItems();
        ucsbDiningCommonsMenuItem.setDiningCommonsCode(diningCommonsCode);
        ucsbDiningCommonsMenuItem.setName(name);
        ucsbDiningCommonsMenuItem.setStation(station);

        UCSBDiningCommonsMenuItems savedUcsbDiningCommonsMenuItem =
            ucsbDiningCommonsMenuItemsRepository.save(
                ucsbDiningCommonsMenuItem
            );

        return savedUcsbDiningCommonsMenuItem;
    }

    @Operation(summary = "Get a single dining commons menu item")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBDiningCommonsMenuItems getById(
        @Parameter(name = "id") @RequestParam Long id
    ) {
        UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItem =
            ucsbDiningCommonsMenuItemsRepository
                .findById(id)
                .orElseThrow(
                    () ->
                        new EntityNotFoundException(
                            UCSBDiningCommonsMenuItems.class,
                            id
                        )
                );

        return ucsbDiningCommonsMenuItem;
    }

    @Operation(summary = "Delete a UCSBDiningCommonsMenuItems")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteUCSBDiningCommonsMenuItems(
        @Parameter(name = "id") @RequestParam Long id
    ) {
        UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItems =
            ucsbDiningCommonsMenuItemsRepository
                .findById(id)
                .orElseThrow(
                    () ->
                        new EntityNotFoundException(
                            UCSBDiningCommonsMenuItems.class,
                            id
                        )
                );

        ucsbDiningCommonsMenuItemsRepository.delete(ucsbDiningCommonsMenuItems);
        return genericMessage(
            "UCSBDiningCommonsMenuItems with id %s deleted".formatted(id)
        );
    }
}
