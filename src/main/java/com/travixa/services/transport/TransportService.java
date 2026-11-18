package com.travixa.services.transport;

import com.travixa.entity.transport.TransportRoute;
import com.travixa.dto.transport.TransportRouteRequest;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.repository.hotel.DestinationRepository;
import com.travixa.repository.partner.PartnerOrganizationRepository;
import com.travixa.repository.transport.TransportRouteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TransportService {

    private final TransportRouteRepository transportRouteRepository;
    private final PartnerOrganizationRepository organizationRepository;
    private final DestinationRepository destinationRepository;

    public TransportService(TransportRouteRepository transportRouteRepository,
                            PartnerOrganizationRepository organizationRepository,
                            DestinationRepository destinationRepository) {
        this.transportRouteRepository = transportRouteRepository;
        this.organizationRepository = organizationRepository;
        this.destinationRepository = destinationRepository;
    }

    public List<TransportRoute> findAll() {
        return transportRouteRepository.findAll();
    }

    public Optional<TransportRoute> findById(Long id) {
        return transportRouteRepository.findById(id);
    }

    @Transactional
    public TransportRoute save(TransportRoute transportRoute) {
        return transportRouteRepository.save(transportRoute);
    }

    @Transactional
    public TransportRoute create(TransportRouteRequest request) {
        TransportRoute route = new TransportRoute();
        apply(route, request);
        return transportRouteRepository.save(route);
    }

    @Transactional
    public TransportRoute update(Long id, TransportRouteRequest request) {
        TransportRoute route = transportRouteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transport route not found"));
        apply(route, request);
        return transportRouteRepository.save(route);
    }

    @Transactional
    public void delete(Long id) {
        TransportRoute route = transportRouteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transport route not found"));
        // Keep route history intact for existing schedules and stops.
        route.setStatus(com.travixa.enums.RouteStatus.ARCHIVED);
        transportRouteRepository.save(route);
    }

    private void apply(TransportRoute route, TransportRouteRequest request) {
        PartnerOrganization organization = organizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organization not found"));
        Destination origin = request.originDestinationId() == null ? null : destinationRepository.findById(request.originDestinationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Origin destination not found"));
        Destination destination = request.destinationId() == null ? null : destinationRepository.findById(request.destinationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destination not found"));
        route.setOrganization(organization); route.setOriginDestination(origin); route.setDestination(destination);
        route.setOriginName(request.originName().trim()); route.setDestinationName(request.destinationName().trim());
        route.setDistanceKm(request.distanceKm()); route.setStatus(request.status());
    }
}
