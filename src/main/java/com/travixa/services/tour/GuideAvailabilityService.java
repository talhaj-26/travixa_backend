package com.travixa.services.tour;

import com.travixa.entity.tour_guide.GuideAvailability;
import com.travixa.repository.tour.GuideAvailabilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GuideAvailabilityService {

    private final GuideAvailabilityRepository guideAvailabilityRepository;

    public GuideAvailabilityService(GuideAvailabilityRepository guideAvailabilityRepository) {
        this.guideAvailabilityRepository = guideAvailabilityRepository;
    }

    public List<GuideAvailability> findAll() {
        return guideAvailabilityRepository.findAll();
    }

    public Optional<GuideAvailability> findById(Long id) {
        return guideAvailabilityRepository.findById(id);
    }

    @Transactional
    public GuideAvailability save(GuideAvailability guideAvailability) {
        if (guideAvailability.getStartsAt() == null || guideAvailability.getEndsAt() == null
                || !guideAvailability.getEndsAt().isAfter(guideAvailability.getStartsAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Availability end must be after start");
        }
        Long excludeId = guideAvailability.getId();
        if (guideAvailabilityRepository.countOverlappingAvailability(
                guideAvailability.getGuideProfile().getId(), guideAvailability.getStartsAt(),
                guideAvailability.getEndsAt(), excludeId) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Guide already has an availability record during this time");
        }
        return guideAvailabilityRepository.save(guideAvailability);
    }

    @Transactional
    public void delete(GuideAvailability availability) { guideAvailabilityRepository.delete(availability); }
}
