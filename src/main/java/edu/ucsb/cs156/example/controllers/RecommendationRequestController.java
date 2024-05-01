package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Tag(name = "RecommendationRequest")
@RequestMapping("/api/RecommendationRequest")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController {

    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;

    @Operation(summary= "get all recommendation")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> allRequests() {
        Iterable<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll();
        return recommendationRequests;
    }

    @Operation(summary= "Create a new recommendation requests")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBDiningCommons postRecommendationRequests(
        @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
        @Parameter(name="professorEmail") @RequestParam String professorEmail,
        @Parameter(name="explanation") @RequestParam String explanation,
        @Parameter(name="dateRequested") @RequestParam LocalDateTime dateRequested,
        @Parameter(name="dateNeeded") @RequestParam LocalDateTime dateNeeded,
        @Parameter(name="longitude") @RequestParam boolean longitude
        )
        {

        RecommendationRequest recommendationRequests = new RecommendationRequest();
        commons.setRequesterEmail(requesterEmail);
        commons.setProfessorEmail(professorEmail);
        commons.setExplanation(explanation);
        commons.setDateRequested(dateRequested);
        commons.setHasDiningCam(hasDiningCam);
        commons.setLatitude(latitude);
        commons.setLongitude(longitude);

        UCSBDiningCommons recommendationRequests = ucsbDiningCommonsRepository.save(request);

        return recommendationRequests;
    }
}
