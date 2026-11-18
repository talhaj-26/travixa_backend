package com.travixa.repository.tour;

import com.travixa.entity.tour_guide.PackageDeparture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import com.travixa.enums.ScheduleStatus;

public interface PackageDepartureRepository extends JpaRepository<PackageDeparture, Long> {
    List<PackageDeparture> findAllByTourPackage_IdOrderByStartsAtAsc(Long tourPackageId);

    List<PackageDeparture> findAllByTourPackage_IdAndStatusAndStartsAtAfterOrderByStartsAtAsc(
            Long tourPackageId, ScheduleStatus status, Instant startsAfter);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from PackageDeparture d where d.id = :id")
    Optional<PackageDeparture> findByIdForUpdate(@Param("id") Long id);
}
