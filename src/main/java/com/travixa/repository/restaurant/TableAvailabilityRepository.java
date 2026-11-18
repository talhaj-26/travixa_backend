package com.travixa.repository.restaurant;

import com.travixa.entity.restaurant.TableAvailability;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TableAvailabilityRepository extends JpaRepository<TableAvailability, Long> {
//    List<TableAvailability> findAllByTable_Restaurant_IdOrderByAvailableOnAscStartsAtAsc(Long restaurantId);
//    List<TableAvailability> findAllByTable_Restaurant_IdAndAvailableOnAndAvailableSeatsGreaterThanOrderByStartsAtAsc(
//            Long restaurantId, LocalDate availableOn, int availableSeats);

    @EntityGraph(attributePaths = {"table", "table.restaurant"})
    List<TableAvailability> findAllByTable_Restaurant_IdOrderByAvailableOnAscStartsAtAsc(
            Long restaurantId);

    @EntityGraph(attributePaths = {"table", "table.restaurant"})
    List<TableAvailability> findAllByTable_Restaurant_IdAndAvailableOnAndAvailableSeatsGreaterThanOrderByStartsAtAsc(
            Long restaurantId,
            LocalDate availableOn,
            int availableSeats
    );

    boolean existsByTable_IdAndAvailableOnAndStartsAt(Long tableId, LocalDate availableOn, LocalTime startsAt);

    boolean existsByTable_IdAndAvailableOnAndStartsAtAndIdNot(Long tableId, LocalDate availableOn, LocalTime startsAt, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select a from TableAvailability a where a.table.id = :tableId and a.availableOn = :date and a.startsAt = :startsAt")
    Optional<TableAvailability> lockByTableAndDateAndStart(@Param("tableId") Long tableId,
                                                           @Param("date") LocalDate date,
                                                           @Param("startsAt") LocalTime startsAt);
}
