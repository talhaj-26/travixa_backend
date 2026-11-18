package com.travixa.controllers;

import com.travixa.dto.engagement.TravelJournalResponse;
import com.travixa.entity.customer_engagement.TravelJournalEntry;
import com.travixa.services.customer.TravelJournalService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/travel-journal")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class TravelJournalController {

    private final TravelJournalService travelJournalService;

    public TravelJournalController(TravelJournalService travelJournalService) {
        this.travelJournalService = travelJournalService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<TravelJournalResponse> findAll() {
        return travelJournalService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public TravelJournalResponse findById(@PathVariable Long id) {
        return travelJournalService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Journal entry not found"));
    }

    private TravelJournalResponse toResponse(TravelJournalEntry entry) {
        return new TravelJournalResponse(entry.getId(),
                entry.getSavedTrip() == null ? null : entry.getSavedTrip().getId(),
                entry.getTitle(), entry.getContent(), entry.getVisibility(), entry.getRecordedAt());
    }
}
