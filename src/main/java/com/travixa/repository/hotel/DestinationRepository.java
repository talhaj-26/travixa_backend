package com.travixa.repository.hotel;

import com.travixa.entity.hotel_stays.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DestinationRepository
        extends JpaRepository<Destination, Long>,
        JpaSpecificationExecutor<Destination> {

    List<Destination> findAllByPublishedTrueOrderByFeaturedDescNameAsc();

    Optional<Destination> findByIdAndPublishedTrue(Long id);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);
}