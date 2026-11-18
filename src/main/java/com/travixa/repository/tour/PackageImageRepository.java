package com.travixa.repository.tour;

import com.travixa.entity.tour_guide.PackageImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PackageImageRepository extends JpaRepository<PackageImage, Long> {
    List<PackageImage> findAllByTourPackage_IdOrderByDisplayOrderAscIdAsc(Long tourPackageId);
    Optional<PackageImage> findByStorageKey(String storageKey);
}
