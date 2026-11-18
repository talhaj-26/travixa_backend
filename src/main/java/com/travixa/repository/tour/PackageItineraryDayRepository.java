package com.travixa.repository.tour;

import com.travixa.entity.tour_guide.PackageItineraryDay;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PackageItineraryDayRepository extends JpaRepository<PackageItineraryDay, Long> {
    List<PackageItineraryDay> findAllByTourPackage_IdOrderByDayNumberAsc(Long tourPackageId);
    boolean existsByTourPackage_IdAndDayNumber(Long tourPackageId, int dayNumber);
    boolean existsByTourPackage_IdAndDayNumberAndIdNot(Long tourPackageId, int dayNumber, Long id);
}
