package com.travixa.controllers;

import com.travixa.dto.tour.GuideOfferingRequest;
import com.travixa.dto.tour.GuideOfferingResponse;
import com.travixa.entity.tour_guide.GuideOffering;
import com.travixa.services.tour.GuideOfferingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/guide-offerings")
@Transactional(readOnly = true)
public class GuideOfferingController {
    private final GuideOfferingService service;
    public GuideOfferingController(GuideOfferingService service) { this.service = service; }

    @GetMapping("/public")
    @PreAuthorize("permitAll()")
    public List<GuideOfferingResponse> findPublic() { return service.findPublic().stream().map(this::response).toList(); }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public List<GuideOfferingResponse> findAll() { return service.findAll().stream().map(this::response).toList(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public GuideOfferingResponse create(@Valid @RequestBody GuideOfferingRequest request) { return response(service.create(request)); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public GuideOfferingResponse update(@PathVariable Long id, @Valid @RequestBody GuideOfferingRequest request) { return response(service.update(id, request)); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public void delete(@PathVariable Long id) { service.deactivate(id); }

    private GuideOfferingResponse response(GuideOffering offer) {
        var guide = offer.getGuideProfile();
        return new GuideOfferingResponse(offer.getId(), guide.getId(), guide.getUser().getUserName(),
                guide.getOrganization() == null ? null : guide.getOrganization().getId(),
                guide.getOrganization() == null ? null : guide.getOrganization().getName(),
                guide.getBiography(), guide.getLanguageCodes(), offer.getName(), offer.getDescription(),
                offer.getPricePerHour(), offer.getCurrency(), offer.isActive());
    }
}
