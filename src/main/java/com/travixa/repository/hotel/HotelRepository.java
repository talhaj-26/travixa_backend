package com.travixa.repository.hotel;

import com.travixa.entity.hotel_stays.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Collection;
import com.travixa.enums.ListingStatus;
import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {
    long countByOrganization_IdIn(Collection<Long> organizationIds);

    long countByOrganization_IdInAndListingStatusIn(
            Collection<Long> organizationIds,
            Collection<ListingStatus> statuses
    );
    long countByListingStatus(
            com.travixa.enums.ListingStatus listingStatus
    );
    List<Hotel> findAllByListingStatusOrderByNameAsc(
            ListingStatus listingStatus
    );

    Optional<Hotel> findByIdAndListingStatus(
            Long id,
            ListingStatus listingStatus
    );
    boolean existsByOrganization_IdAndSlug(Long organizationId, String slug);

    boolean existsByOrganization_IdAndSlugAndIdNot(
            Long organizationId,
            String slug,
            Long id
    );
}
