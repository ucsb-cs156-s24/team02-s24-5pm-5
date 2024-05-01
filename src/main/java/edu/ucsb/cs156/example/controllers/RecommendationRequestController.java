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
        Iterable<RecommendationRequest> recommendationRequest = recommendationRequestRepository.findAll();
        return recommendationRequest;
    }

    @Operation(summary= "Create a new recommendation requests")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest postRecommendationRequest(
        @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
        @Parameter(name="professorEmail") @RequestParam String professorEmail,
        @Parameter(name="explanation") @RequestParam String explanation,
        @Parameter(name="dateRequested") @RequestParam LocalDateTime dateRequested,
        @Parameter(name="dateNeeded") @RequestParam LocalDateTime dateNeeded,
        @Parameter(name="done") @RequestParam boolean done
        )
    {

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setRequesterEmail(requesterEmail);
        recommendationRequest.setProfessorEmail(professorEmail);
        recommendationRequest.setExplanation(explanation);
        recommendationRequest.setDateRequested(dateRequested);
        recommendationRequest.setDateNeeded(dateNeeded);
        recommendationRequest.setDone(done);

        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        return savedRecommendationRequest;
    }

    // @Operation(summary= "Get a single commons")
    // @PreAuthorize("hasRole('ROLE_USER')")
    // @GetMapping("")
    // public UCSBDiningCommons getById(
    //         @Parameter(name="id") @RequestParam Long id) {
    //     RecommendationRequest recommendationRequest = recommendationRequest.findById(id)
    //             .orElseThrow(() -> new EntityNotFoundException(recommendationRequest, id));

    //     return commons;
    // }

    // @Operation(summary= "Delete a UCSBDiningCommons")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // @DeleteMapping("")
    // public Object deleteCommons(
    //         @Parameter(name="code") @RequestParam String code) {
    //     UCSBDiningCommons commons = ucsbDiningCommonsRepository.findById(code)
    //             .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommons.class, code));

    //     ucsbDiningCommonsRepository.delete(commons);
    //     return genericMessage("UCSBDiningCommons with id %s deleted".formatted(code));
    // }

    // @Operation(summary= "Update a recommendation requests")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // @PutMapping("")
    // public RecommendationRequest updateRecommendationRequest(
    //         @Parameter(name="code") @RequestParam String code,
    //         @RequestBody @Valid UCSBDiningCommons incoming) {

    //     UCSBDiningCommons recommendationRequest = recommendationRequestRepository.findById(code)
    //             .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, code));


    //     commons.setName(incoming.getName());  
    //     commons.setHasSackMeal(incoming.getHasSackMeal());
    //     commons.setHasTakeOutMeal(incoming.getHasTakeOutMeal());
    //     commons.setHasDiningCam(incoming.getHasDiningCam());
    //     commons.setLatitude(incoming.getLatitude());
    //     commons.setLongitude(incoming.getLongitude());

    //     ucsbDiningCommonsRepository.save(commons);

    //     return commons;
}
