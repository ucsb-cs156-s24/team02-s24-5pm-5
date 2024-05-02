package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;


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
        @Parameter(name="dateRequested") @RequestParam("dateRequested") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateRequested,
        @Parameter(name="dateNeeded") @RequestParam("dateNeeded") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateNeeded,
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

    @Operation(summary= "Get a single recommendation request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getById(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest rec = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        return rec;
    }

    @Operation(summary= "Delete a Rec")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteRec(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest ucsbDate = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequestRepository.delete(ucsbDate);
        return genericMessage("RecommendationRequest with id %s deleted".formatted(id));
    }

    @Operation(summary= "Update a single rec")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public RecommendationRequest updateRec(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid RecommendationRequest incoming) {

        RecommendationRequest rec = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

            rec.setRequesterEmail(incoming.getRequesterEmail());
            rec.setProfessorEmail(incoming.getProfessorEmail());
            rec.setExplanation(incoming.getExplanation());
            rec.setDateRequested(incoming.getDateRequested());
            rec.setDateNeeded(incoming.getDateNeeded());
            rec.setDone(incoming.getDone());

        recommendationRequestRepository.save(rec);

        return rec;
    }
}
