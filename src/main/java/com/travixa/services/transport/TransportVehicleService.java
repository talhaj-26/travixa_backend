package com.travixa.services.transport;

import com.travixa.dto.transport.TransportVehicleRequest;
import com.travixa.entity.transport.Vehicle;
import com.travixa.repository.partner.PartnerOrganizationRepository;
import com.travixa.repository.transport.TransportScheduleRepository;
import com.travixa.repository.transport.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TransportVehicleService {
    private final VehicleRepository vehicles;
    private final PartnerOrganizationRepository organizations;
    private final TransportScheduleRepository schedules;

    public TransportVehicleService(VehicleRepository vehicles, PartnerOrganizationRepository organizations,
                                   TransportScheduleRepository schedules) {
        this.vehicles = vehicles; this.organizations = organizations; this.schedules = schedules;
    }
    public List<Vehicle> findAll() { return vehicles.findAll(); }
    @Transactional public Vehicle create(TransportVehicleRequest request) { return vehicles.save(apply(new Vehicle(), request)); }
    @Transactional public Vehicle update(Long id, TransportVehicleRequest request) {
        Vehicle vehicle = vehicles.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));
        if (schedules.existsByVehicle_Id(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "A vehicle with schedules cannot be edited; retire it and create a replacement");
        return vehicles.save(apply(vehicle, request));
    }
    @Transactional public void retire(Long id) {
        Vehicle vehicle = vehicles.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));
        if (schedules.existsByVehicle_Id(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "A vehicle with schedules cannot be retired");
        vehicle.setStatus(com.travixa.enums.VehicleStatus.RETIRED); vehicles.save(vehicle);
    }
    private Vehicle apply(Vehicle vehicle, TransportVehicleRequest request) {
        vehicle.setOrganization(organizations.findById(request.organizationId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organization not found")));
        vehicle.setVehicleType(request.vehicleType()); vehicle.setRegistrationNumber(request.registrationNumber().trim());
        vehicle.setMake(request.make()); vehicle.setModelName(request.modelName()); vehicle.setSeatCapacity(request.seatCapacity());
        vehicle.setStatus(request.status()); return vehicle;
    }
}
