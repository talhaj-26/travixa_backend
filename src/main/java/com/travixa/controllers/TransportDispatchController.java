package com.travixa.controllers;

import com.travixa.dto.transport.VehicleAssignmentResponse;
import com.travixa.dto.transport.VehicleAssignmentRequest;
import com.travixa.entity.transport.DriverProfile;
import com.travixa.entity.transport.VehicleAssignment;
import com.travixa.entity.transport.Vehicle;
import com.travixa.repository.transport.DriverProfileRepository;
import com.travixa.repository.transport.VehicleRepository;
import com.travixa.services.transport.TransportDispatchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/transport-assignments")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class TransportDispatchController {

    private final TransportDispatchService transportDispatchService;
    private final VehicleRepository vehicleRepository;
    private final DriverProfileRepository driverProfileRepository;

    public TransportDispatchController(TransportDispatchService transportDispatchService,
                                       VehicleRepository vehicleRepository,
                                       DriverProfileRepository driverProfileRepository) {
        this.transportDispatchService = transportDispatchService;
        this.vehicleRepository = vehicleRepository;
        this.driverProfileRepository = driverProfileRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<VehicleAssignmentResponse> findAll() {
        return transportDispatchService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public VehicleAssignmentResponse findById(@PathVariable Long id) {
        return transportDispatchService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle assignment not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleAssignmentResponse create(@Valid @RequestBody VehicleAssignmentRequest request) {
        return toResponse(transportDispatchService.save(toEntity(new VehicleAssignment(), request)));
    }

    @PutMapping("/{id}")
    public VehicleAssignmentResponse update(@PathVariable Long id, @Valid @RequestBody VehicleAssignmentRequest request) {
        VehicleAssignment assignment = transportDispatchService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle assignment not found"));
        return toResponse(transportDispatchService.save(toEntity(assignment, request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        VehicleAssignment assignment = transportDispatchService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle assignment not found"));
        transportDispatchService.delete(assignment);
    }

    private VehicleAssignment toEntity(VehicleAssignment assignment, VehicleAssignmentRequest request) {
        if (request.endsAt() != null && !request.endsAt().isAfter(request.startsAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment end must be after start");
        }
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle not found"));
        DriverProfile driver = driverProfileRepository.findById(request.driverProfileId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver profile not found"));
        if (!vehicle.getOrganization().getId().equals(driver.getOrganization().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle and driver must belong to the same organization");
        }
        assignment.setVehicle(vehicle); assignment.setDriverProfile(driver);
        assignment.setStartsAt(request.startsAt()); assignment.setEndsAt(request.endsAt());
        return assignment;
    }

    private VehicleAssignmentResponse toResponse(VehicleAssignment assignment) {
        return new VehicleAssignmentResponse(assignment.getId(), assignment.getVehicle().getId(),
                assignment.getDriverProfile().getId(), assignment.getStartsAt(), assignment.getEndsAt());
    }
}
