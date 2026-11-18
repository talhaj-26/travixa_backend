package com.travixa.services.hotel;

import com.travixa.dto.hotel.RoomAvailabilityRequest;
import com.travixa.entity.hotel_stays.Room;
import com.travixa.entity.hotel_stays.RoomAvailability;
import com.travixa.repository.booking.BookingItemRepository;
import com.travixa.repository.hotel.HotelRepository;
import com.travixa.repository.hotel.RoomAvailabilityRepository;
import com.travixa.repository.hotel.RoomRateRepository;
import com.travixa.repository.hotel.RoomRepository;
import com.travixa.repository.hotel.RoomTypeRepository;
import com.travixa.services.platform.MediaService;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HotelInventoryAvailabilityTests {

    @Test
    void cannotDeleteAvailabilityWithAnActiveRoomReservation() {
        LocalDate night = LocalDate.of(2026, 11, 10);
        Room room = new Room();
        room.setId(12L);
        RoomAvailability availability = new RoomAvailability();
        availability.setId(31L);
        availability.setRoom(room);
        availability.setAvailableOn(night);
        availability.setAvailableUnits(1);

        boolean[] deleted = {false};
        RoomRepository rooms = proxy(RoomRepository.class, (self, method, args) ->
                method.getName().equals("findByIdForUpdate") ? Optional.of(room) : defaultValue(method.getReturnType()));
        RoomAvailabilityRepository availabilityRepository = proxy(RoomAvailabilityRepository.class, (self, method, args) -> {
            if (method.getName().equals("findById")) return Optional.of(availability);
            if (method.getName().equals("delete")) { deleted[0] = true; return null; }
            return defaultValue(method.getReturnType());
        });
        BookingItemRepository bookings = proxy(BookingItemRepository.class, (self, method, args) ->
                method.getName().equals("countReservedRoomNights") ? 1L : defaultValue(method.getReturnType()));

        HotelInventoryService service = service(rooms, availabilityRepository, bookings);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.deleteAvailability(31L));

        assertEquals(409, error.getStatusCode().value());
        org.junit.jupiter.api.Assertions.assertFalse(deleted[0]);
    }

    @Test
    void cannotBlockAvailabilityAlreadyReservedForTheSameNight() {
        LocalDate night = LocalDate.of(2026, 11, 10);
        Room room = new Room();
        room.setId(12L);
        RoomAvailability availability = new RoomAvailability();
        availability.setId(31L);
        availability.setRoom(room);
        availability.setAvailableOn(night);
        availability.setAvailableUnits(1);

        boolean[] saved = {false};
        RoomRepository rooms = proxy(RoomRepository.class, (self, method, args) ->
                method.getName().equals("findByIdForUpdate") ? Optional.of(room) : defaultValue(method.getReturnType()));
        RoomAvailabilityRepository availabilityRepository = proxy(RoomAvailabilityRepository.class, (self, method, args) -> {
            if (method.getName().equals("findById")) return Optional.of(availability);
            if (method.getName().equals("existsByRoom_IdAndAvailableOnAndIdNot")) return false;
            if (method.getName().equals("save")) { saved[0] = true; return args[0]; }
            return defaultValue(method.getReturnType());
        });
        BookingItemRepository bookings = proxy(BookingItemRepository.class, (self, method, args) ->
                method.getName().equals("countReservedRoomNights") ? 1L : defaultValue(method.getReturnType()));

        HotelInventoryService service = service(rooms, availabilityRepository, bookings);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.updateAvailability(31L, new RoomAvailabilityRequest(12L, night, 0)));

        assertEquals(409, error.getStatusCode().value());
        org.junit.jupiter.api.Assertions.assertFalse(saved[0]);
    }

    private HotelInventoryService service(RoomRepository rooms,
                                          RoomAvailabilityRepository availability,
                                          BookingItemRepository bookings) {
        return new HotelInventoryService(
                rooms,
                proxy(RoomTypeRepository.class, (self, method, args) -> defaultValue(method.getReturnType())),
                proxy(RoomRateRepository.class, (self, method, args) -> defaultValue(method.getReturnType())),
                availability,
                proxy(HotelRepository.class, (self, method, args) -> defaultValue(method.getReturnType())),
                bookings,
                null);
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> repositoryType, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(repositoryType.getClassLoader(), new Class<?>[]{repositoryType},
                (self, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "equals" -> self == args[0];
                            case "hashCode" -> System.identityHashCode(self);
                            case "toString" -> repositoryType.getSimpleName() + " test proxy";
                            default -> null;
                        };
                    }
                    return handler.invoke(self, method, args);
                });
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        return 0;
    }
}
