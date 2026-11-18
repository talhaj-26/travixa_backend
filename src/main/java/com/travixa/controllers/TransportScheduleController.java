package com.travixa.controllers;

import com.travixa.dto.transport.TransportScheduleRequest;
import com.travixa.dto.transport.TransportScheduleResponse;
import com.travixa.entity.transport.TransportSchedule;
import com.travixa.services.transport.TransportScheduleService;
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
@RequestMapping("/api/transport-schedules")
public class TransportScheduleController {
    private final TransportScheduleService service;

    public TransportScheduleController(TransportScheduleService service) { this.service = service; }

    @GetMapping("/public")
    @PreAuthorize("permitAll()")
    @Transactional(readOnly = true)
    public List<TransportScheduleResponse> findPublicUpcoming() {
        return service.findPublicUpcoming().stream().map(this::response).toList();
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional(readOnly = true)
    public List<TransportScheduleResponse> findAll() {
        return service.findAll().stream().map(this::response).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public TransportScheduleResponse create(@Valid @RequestBody TransportScheduleRequest request) {
        return response(service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public TransportScheduleResponse update(@PathVariable Long id, @Valid @RequestBody TransportScheduleRequest request) {
        return response(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public void cancel(@PathVariable Long id) { service.cancel(id); }

    private TransportScheduleResponse response(TransportSchedule schedule) {
        long reserved = service.reservedSeats(schedule.getId());
        int capacity = schedule.getVehicle().getSeatCapacity();
        long available = Math.max(0, capacity - reserved);
        return new TransportScheduleResponse(schedule.getId(), schedule.getOrganization().getId(),
                schedule.getRoute().getId(), schedule.getRoute().getOriginName(),
                schedule.getRoute().getDestinationName(), schedule.getVehicle().getId(),
                schedule.getVehicle().getVehicleType(), schedule.getOrganization().getName(),
                String.join(" ", java.util.stream.Stream.of(schedule.getVehicle().getMake(),
                        schedule.getVehicle().getModelName()).filter(java.util.Objects::nonNull)
                        .filter(value -> !value.isBlank()).toList()),
                schedule.getDepartureAt(), schedule.getArrivalAt(), schedule.getTicketPrice(),
                schedule.getCurrency(), schedule.getStatus(), capacity, available);
    }
}
