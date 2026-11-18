package com.travixa.mapper;

import com.travixa.dto.transport.TransportRouteRequest;
import com.travixa.dto.transport.TransportRouteResponse;
import com.travixa.dto.transport.TransportTicketResponse;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.entity.transport.TransportRoute;
import com.travixa.entity.transport.TransportSeat;

import java.util.Objects;

public final class TransportMapper {

    private TransportMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static TransportRoute toEntity(TransportRouteRequest request, PartnerOrganization organization,
            Destination origin, Destination destination) {
        TransportRoute route = new TransportRoute();
        route.setOrganization(Objects.requireNonNull(organization, "organization must not be null"));
        route.setOriginDestination(origin);
        route.setDestination(destination);
        updateEntity(route, request);
        return route;
    }

    public static void updateEntity(TransportRoute route, TransportRouteRequest request) {
        route.setOriginName(request.originName());
        route.setDestinationName(request.destinationName());
        route.setDistanceKm(request.distanceKm());
        route.setStatus(request.status());
    }

    public static TransportRouteResponse toResponse(TransportRoute route) {
        return new TransportRouteResponse(route.getId(), route.getOrganization().getId(),
                route.getOriginDestination() == null ? null : route.getOriginDestination().getId(),
                route.getDestination() == null ? null : route.getDestination().getId(), route.getOriginName(),
                route.getDestinationName(), route.getDistanceKm(), route.getStatus());
    }

    public static TransportTicketResponse toTicketResponse(TransportSeat seat) {
        var schedule = seat.getSchedule();
        return new TransportTicketResponse(seat.getId(), seat.getSchedule().getId(), seat.getSeatNumber(),
                seat.getStatus(), seat.getPriceOverride() == null ? schedule.getTicketPrice() : seat.getPriceOverride(),
                seat.getPriceOverride() == null ? schedule.getCurrency() : seat.getCurrency());
    }
}
