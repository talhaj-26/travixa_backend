package com.travixa.repository.tour;

import com.travixa.entity.tour_guide.GuideOffering;
import com.travixa.enums.TourGuideStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuideOfferingRepository extends JpaRepository<GuideOffering, Long> {
    List<GuideOffering> findAllByActiveTrueAndGuideProfile_StatusOrderByIdDesc(TourGuideStatus status);
    List<GuideOffering> findAllByOrderByIdDesc();
}
