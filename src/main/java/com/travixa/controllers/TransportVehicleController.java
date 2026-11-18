package com.travixa.controllers;

import com.travixa.dto.transport.TransportVehicleRequest;
import com.travixa.dto.transport.TransportVehicleResponse;
import com.travixa.entity.transport.Vehicle;
import com.travixa.services.transport.TransportVehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transport-vehicles")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class TransportVehicleController {
    private final TransportVehicleService service;
    public TransportVehicleController(TransportVehicleService service) { this.service = service; }
    @GetMapping @Transactional(readOnly = true) public List<TransportVehicleResponse> findAll() { return service.findAll().stream().map(this::response).toList(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Transactional public TransportVehicleResponse create(@Valid @RequestBody TransportVehicleRequest request) { return response(service.create(request)); }
    @PutMapping("/{id}") @Transactional public TransportVehicleResponse update(@PathVariable Long id, @Valid @RequestBody TransportVehicleRequest request) { return response(service.update(id, request)); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Transactional public void retire(@PathVariable Long id) { service.retire(id); }
    private TransportVehicleResponse response(Vehicle vehicle) { return new TransportVehicleResponse(vehicle.getId(), vehicle.getOrganization().getId(), vehicle.getOrganization().getName(), vehicle.getVehicleType(), vehicle.getRegistrationNumber(), vehicle.getMake(), vehicle.getModelName(), vehicle.getSeatCapacity(), vehicle.getStatus()); }
}
