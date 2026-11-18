package com.travixa.repository.tour;

import com.travixa.entity.tour_guide.TourPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Collection;
import com.travixa.enums.ListingStatus;
import com.travixa.enums.PartnerOrganizationStatus;
import java.util.List;
import java.util.Optional;

public interface TourPackageRepository extends JpaRepository<TourPackage, Long>, JpaSpecificationExecutor<TourPackage> {
    long countByOrganization_IdIn(Collection<Long> organizationIds);

    long countByOrganization_IdInAndListingStatusIn(
            Collection<Long> organizationIds,
            Collection<ListingStatus> statuses
    );
    long countByListingStatus(
            com.travixa.enums.ListingStatus listingStatus
    );
    boolean existsByOrganization_IdAndSlug(Long organizationId, String slug);
    boolean existsByOrganization_IdAndSlugAndIdNot(Long organizationId, String slug, Long id);
    List<TourPackage> findAllByListingStatusOrderByNameAsc(ListingStatus listingStatus);
    Optional<TourPackage> findByIdAndListingStatus(Long id, ListingStatus listingStatus);
    List<TourPackage> findAllByListingStatusAndOrganization_StatusAndDestination_PublishedOrderByNameAsc(
            ListingStatus listingStatus, PartnerOrganizationStatus organizationStatus, boolean published);
    Optional<TourPackage> findByIdAndListingStatusAndOrganization_StatusAndDestination_Published(
            Long id, ListingStatus listingStatus, PartnerOrganizationStatus organizationStatus, boolean published);
}
