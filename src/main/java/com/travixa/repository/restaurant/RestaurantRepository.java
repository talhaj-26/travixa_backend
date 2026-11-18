package com.travixa.repository.restaurant;

import com.travixa.entity.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import com.travixa.enums.ListingStatus;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, JpaSpecificationExecutor<Restaurant> {
    long countByOrganization_IdIn(Collection<Long> organizationIds);

    long countByOrganization_IdInAndListingStatusIn(
            Collection<Long> organizationIds,
            Collection<ListingStatus> statuses
    );
    long countByListingStatus(
            com.travixa.enums.ListingStatus listingStatus
    );

    boolean existsByOrganization_IdAndSlug(Long organizationId, String slug);

    boolean existsByOrganization_IdAndSlugAndIdNot(
            Long organizationId,
            String slug,
            Long id
    );

    List<Restaurant> findAllByListingStatusOrderByNameAsc(ListingStatus listingStatus);

    Optional<Restaurant> findByIdAndListingStatus(Long id, ListingStatus listingStatus);
}
