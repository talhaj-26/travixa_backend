package com.travixa.services.tour;

import com.travixa.entity.tour_guide.TourGuideProfile;
import com.travixa.repository.tour.TourGuideProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TourGuideService {

    private final TourGuideProfileRepository tourGuideProfileRepository;

    public TourGuideService(TourGuideProfileRepository tourGuideProfileRepository) {
        this.tourGuideProfileRepository = tourGuideProfileRepository;
    }

    public List<TourGuideProfile> findAll() {
        return tourGuideProfileRepository.findAll();
    }

    public Optional<TourGuideProfile> findById(Long id) {
        return tourGuideProfileRepository.findById(id);
    }

    @Transactional
    public TourGuideProfile save(TourGuideProfile tourGuideProfile) {
        return tourGuideProfileRepository.save(tourGuideProfile);
    }

}
