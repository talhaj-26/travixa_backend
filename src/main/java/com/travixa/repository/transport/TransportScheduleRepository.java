package com.travixa.repository.transport;

import com.travixa.entity.transport.TransportSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;
import com.travixa.enums.ScheduleStatus;
import java.time.Instant;

public interface TransportScheduleRepository
        extends JpaRepository<TransportSchedule, Long>, JpaSpecificationExecutor<TransportSchedule> {

    boolean existsByVehicle_Id(Long vehicleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from TransportSchedule s where s.id = :id")
    Optional<TransportSchedule> findByIdForUpdate(@Param("id") Long id);

    List<TransportSchedule> findAllByStatusAndDepartureAtAfterAndRoute_StatusOrderByDepartureAtAsc(
            ScheduleStatus status, Instant departureAt, com.travixa.enums.RouteStatus routeStatus);
}
