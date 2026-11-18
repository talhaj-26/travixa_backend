package com.travixa.services.transport;

import com.travixa.dto.transport.TransportScheduleRequest;
import com.travixa.entity.transport.TransportSchedule;
import com.travixa.enums.RouteStatus;
import com.travixa.enums.ScheduleStatus;
import com.travixa.enums.VehicleStatus;
import com.travixa.repository.booking.BookingItemRepository;
import com.travixa.repository.partner.PartnerOrganizationRepository;
import com.travixa.repository.transport.TransportRouteRepository;
import com.travixa.repository.transport.TransportScheduleRepository;
import com.travixa.repository.transport.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TransportScheduleService {
    private final TransportScheduleRepository schedules;
    private final PartnerOrganizationRepository organizations;
    private final TransportRouteRepository routes;
    private final VehicleRepository vehicles;
    private final BookingItemRepository bookingItems;

    public TransportScheduleService(TransportScheduleRepository schedules,
                                    PartnerOrganizationRepository organizations,
                                    TransportRouteRepository routes,
                                    VehicleRepository vehicles,
                                    BookingItemRepository bookingItems) {
        this.schedules = schedules;
        this.organizations = organizations;
        this.routes = routes;
        this.vehicles = vehicles;
        this.bookingItems = bookingItems;
    }

    public List<TransportSchedule> findAll() { return schedules.findAll(); }

    public List<TransportSchedule> findPublicUpcoming() {
        return schedules.findAllByStatusAndDepartureAtAfterAndRoute_StatusOrderByDepartureAtAsc(
                        ScheduleStatus.SCHEDULED, Instant.now(), RouteStatus.ACTIVE).stream()
                .filter(schedule -> schedule.getOrganization().getStatus() == com.travixa.enums.PartnerOrganizationStatus.ACTIVE
                        && schedule.getVehicle().getStatus() == VehicleStatus.ACTIVE
                        && bookingItems.countReservedTransportSeats(schedule.getId()) < schedule.getVehicle().getSeatCapacity())
                .toList();
    }

    @Transactional
    public TransportSchedule create(TransportScheduleRequest request) {
        return schedules.save(apply(new TransportSchedule(), request));
    }

    @Transactional
    public TransportSchedule update(Long id, TransportScheduleRequest request) {
        TransportSchedule schedule = schedules.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transport schedule not found"));
        if (bookingItems.countReservedTransportSeats(id) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A schedule with bookings cannot be edited");
        }
        return schedules.save(apply(schedule, request));
    }

    @Transactional
    public void cancel(Long id) {
        TransportSchedule schedule = schedules.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transport schedule not found"));
        if (bookingItems.countReservedTransportSeats(id) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Refund or cancel existing bookings before cancelling this schedule");
        }
        schedule.setStatus(ScheduleStatus.CANCELLED);
        schedules.save(schedule);
    }

    public long reservedSeats(Long scheduleId) { return bookingItems.countReservedTransportSeats(scheduleId); }

    private TransportSchedule apply(TransportSchedule schedule, TransportScheduleRequest request) {
        if (request.vehicleId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle is required for a transport schedule");
        }
        if (!request.arrivalAt().isAfter(request.departureAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arrival must be after departure");
        }
        var organization = organizations.findById(request.organizationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organization not found"));
        var route = routes.findById(request.routeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transport route not found"));
        var vehicle = vehicles.findById(request.vehicleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle not found"));
        if (!organization.getId().equals(route.getOrganization().getId())
                || !organization.getId().equals(vehicle.getOrganization().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Route and vehicle must belong to the selected organization");
        }
        if (route.getStatus() != RouteStatus.ACTIVE || vehicle.getStatus() != VehicleStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only active routes and vehicles can be scheduled");
        }
        schedule.setOrganization(organization);
        schedule.setRoute(route);
        schedule.setVehicle(vehicle);
        schedule.setDepartureAt(request.departureAt());
        schedule.setArrivalAt(request.arrivalAt());
        schedule.setTicketPrice(request.ticketPrice());
        schedule.setCurrency(request.currency());
        schedule.setStatus(request.status());
        return schedule;
    }
}
