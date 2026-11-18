package com.travixa.services.tour;

import com.travixa.entity.tour_guide.TourPackage;
import com.travixa.repository.tour.TourPackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TourAgencyOperationsService {

    private final TourPackageRepository tourPackageRepository;

    public TourAgencyOperationsService(TourPackageRepository tourPackageRepository) {
        this.tourPackageRepository = tourPackageRepository;
    }

    public List<TourPackage> findAll() {
        return tourPackageRepository.findAll();
    }

    public Optional<TourPackage> findById(Long id) {
        return tourPackageRepository.findById(id);
    }

    @Transactional
    public TourPackage save(TourPackage tourPackage) {
        return tourPackageRepository.save(tourPackage);
    }
}
