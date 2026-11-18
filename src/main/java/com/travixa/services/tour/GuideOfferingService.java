package com.travixa.services.tour;

import com.travixa.dto.tour.GuideOfferingRequest;
import com.travixa.entity.tour_guide.GuideOffering;
import com.travixa.enums.TourGuideStatus;
import com.travixa.repository.tour.GuideOfferingRepository;
import com.travixa.repository.tour.TourGuideProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GuideOfferingService {
    private final GuideOfferingRepository offerings;
    private final TourGuideProfileRepository guides;
    public GuideOfferingService(GuideOfferingRepository offerings, TourGuideProfileRepository guides) {
        this.offerings = offerings; this.guides = guides;
    }
    public List<GuideOffering> findPublic() {
        return offerings.findAllByActiveTrueAndGuideProfile_StatusOrderByIdDesc(TourGuideStatus.ACTIVE).stream()
                .filter(item -> item.getGuideProfile().getOrganization() != null
                        && item.getGuideProfile().getOrganization().getStatus() == com.travixa.enums.PartnerOrganizationStatus.ACTIVE)
                .toList();
    }
    public List<GuideOffering> findAll() { return offerings.findAllByOrderByIdDesc(); }
    public GuideOffering findById(Long id) { return offerings.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide offering not found")); }
    @Transactional public GuideOffering create(GuideOfferingRequest request) { return offerings.save(apply(new GuideOffering(), request)); }
    @Transactional public GuideOffering update(Long id, GuideOfferingRequest request) { return offerings.save(apply(findById(id), request)); }
    @Transactional public void deactivate(Long id) { GuideOffering offering = findById(id); offering.setActive(false); offerings.save(offering); }
    private GuideOffering apply(GuideOffering offering, GuideOfferingRequest request) {
        var guide = guides.findById(request.guideProfileId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guide profile not found"));
        offering.setGuideProfile(guide); offering.setName(request.name().trim()); offering.setDescription(request.description());
        offering.setPricePerHour(request.pricePerHour()); offering.setCurrency(request.currency()); offering.setActive(request.active());
        return offering;
    }
}
