package com.travixa.controllers;

import com.travixa.dto.transport.TransportRouteResponse;
import com.travixa.dto.transport.TransportRouteRequest;
import com.travixa.entity.transport.TransportRoute;
import com.travixa.mapper.TransportMapper;
import com.travixa.services.transport.TransportService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/transport-routes")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class TransportController {

    private final TransportService transportService;

    public TransportController(TransportService transportService) {
        this.transportService = transportService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<TransportRouteResponse> findAll() {
        return transportService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public TransportRouteResponse findById(@PathVariable Long id) {
        return transportService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transport route not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public TransportRouteResponse create(@Valid @RequestBody TransportRouteRequest request) {
        return toResponse(transportService.create(request));
    }

    @PutMapping("/{id}")
    @Transactional
    public TransportRouteResponse update(@PathVariable Long id, @Valid @RequestBody TransportRouteRequest request) {
        return toResponse(transportService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable Long id) { transportService.delete(id); }

    private TransportRouteResponse toResponse(TransportRoute route) {
        return TransportMapper.toResponse(route);
    }
}
