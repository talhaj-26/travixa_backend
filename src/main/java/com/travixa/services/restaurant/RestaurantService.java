package com.travixa.services.restaurant;

import com.travixa.dto.restaurant.RestaurantRequest;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.entity.restaurant.Restaurant;
import com.travixa.enums.ListingStatus;
import com.travixa.mapper.RestaurantMapper;
import com.travixa.repository.hotel.DestinationRepository;
import com.travixa.repository.partner.PartnerOrganizationRepository;
import com.travixa.repository.restaurant.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final DestinationRepository destinationRepository;
    private final PartnerOrganizationRepository organizationRepository;

    public RestaurantService(
            RestaurantRepository restaurantRepository,
            DestinationRepository destinationRepository,
            PartnerOrganizationRepository organizationRepository
    ) {
        this.restaurantRepository = restaurantRepository;
        this.destinationRepository = destinationRepository;
        this.organizationRepository = organizationRepository;
    }

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public Optional<Restaurant> findById(Long id) {
        return restaurantRepository.findById(id);
    }

    public List<Restaurant> findPublished() {
        return restaurantRepository.findAllByListingStatusOrderByNameAsc(
                ListingStatus.PUBLISHED
        );
    }

    public Optional<Restaurant> findPublishedById(Long id) {
        return restaurantRepository.findByIdAndListingStatus(
                id,
                ListingStatus.PUBLISHED
        );
    }

    @Transactional
    public Restaurant create(RestaurantRequest request) {
        PartnerOrganization organization = requireOrganization(request.organizationId());
        Destination destination = requireDestination(request.destinationId());
        String slug = request.slug().trim().toLowerCase();

        if (restaurantRepository.existsByOrganization_IdAndSlug(
                organization.getId(), slug
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Restaurant slug already exists for this organization"
            );
        }

        validatePublication(request.listingStatus(), destination);

        Restaurant restaurant = RestaurantMapper.toEntity(
                request,
                organization,
                destination
        );
        restaurant.setSlug(slug);
        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public Restaurant update(Long id, RestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Restaurant not found"
                ));

        PartnerOrganization organization = requireOrganization(request.organizationId());
        Destination destination = requireDestination(request.destinationId());
        String slug = request.slug().trim().toLowerCase();

        if (restaurantRepository.existsByOrganization_IdAndSlugAndIdNot(
                organization.getId(), slug, id
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Restaurant slug already exists for this organization"
            );
        }

        validatePublication(request.listingStatus(), destination);

        restaurant.setOrganization(organization);
        restaurant.setDestination(destination);
        RestaurantMapper.updateEntity(restaurant, request);
        restaurant.setSlug(slug);
        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public void delete(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Restaurant not found"
                ));

        try {
            restaurantRepository.delete(restaurant);
            restaurantRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this restaurant because it is linked to other records",
                    ex
            );
        }
    }

    @Transactional
    public Restaurant save(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    private PartnerOrganization requireOrganization(Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Organization not found"
                ));
    }

    private Destination requireDestination(Long destinationId) {
        if (destinationId == null) {
            return null;
        }

        return destinationRepository.findById(destinationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Destination not found"
                ));
    }

    private void validatePublication(
            ListingStatus listingStatus,
            Destination destination
    ) {
        if (listingStatus == ListingStatus.PUBLISHED
                && (destination == null || !destination.isPublished())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A published restaurant requires a published destination"
            );
        }
    }
}
