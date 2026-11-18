package com.travixa.repository.tour;

import com.travixa.entity.tour_guide.TourGuideProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Collection;

public interface TourGuideProfileRepository
        extends JpaRepository<TourGuideProfile, Long>, JpaSpecificationExecutor<TourGuideProfile> {
    long countByOrganization_IdIn(Collection<Long> organizationIds);

    long countByStatus(com.travixa.enums.TourGuideStatus status);
}
