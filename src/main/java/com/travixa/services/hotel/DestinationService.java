package com.travixa.services.hotel;

import com.travixa.dto.hotel.DestinationRequest;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.mapper.DestinationMapper;
import com.travixa.repository.hotel.DestinationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DestinationService {

    private final DestinationRepository destinationRepository;

    public DestinationService(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    public List<Destination> findPublished() {
        return destinationRepository.findAllByPublishedTrueOrderByFeaturedDescNameAsc();
    }

    public Destination findPublishedById(Long id) {
        return destinationRepository.findByIdAndPublishedTrue(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Published destination not found"));
    }

    public List<Destination> findAllForAdmin() {
        return destinationRepository.findAll();
    }

    public Destination findByIdForAdmin(Long id) {
        return destinationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Destination not found"));
    }

    @Transactional
    public Destination create(DestinationRequest request) {
        String slug = request.slug().trim().toLowerCase();

        if (destinationRepository.existsBySlug(slug)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Destination slug already exists");
        }

        return destinationRepository.save(DestinationMapper.toEntity(request));
    }

    @Transactional
    public Destination update(Long id, DestinationRequest request) {
        Destination destination = findByIdForAdmin(id);
        String slug = request.slug().trim().toLowerCase();

        if (destinationRepository.existsBySlugAndIdNot(slug, id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Destination slug already exists");
        }

        DestinationMapper.updateEntity(destination, request);
        return destinationRepository.save(destination);
    }

    @Transactional
    public void delete(Long id) {
        Destination destination = findByIdForAdmin(id);

        try {
            destinationRepository.delete(destination);
            destinationRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this destination because it is linked to other records",
                    ex);
        }
    }
}