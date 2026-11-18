package com.travixa.repository.tour;

import com.travixa.entity.tour_guide.GuideAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface GuideAvailabilityRepository extends JpaRepository<GuideAvailability, Long> {
    @Query("select count(a) > 0 from GuideAvailability a " +
            "where a.guideProfile.id = :guideId " +
            "and a.startsAt < :endsAt and a.endsAt > :startsAt " +
            "and (:excludeId is null or a.id <> :excludeId)")
    long countOverlappingAvailability(@Param("guideId") Long guideId,
                                      @Param("startsAt") Instant startsAt,
                                      @Param("endsAt") Instant endsAt,
                                      @Param("excludeId") Long excludeId);
}
