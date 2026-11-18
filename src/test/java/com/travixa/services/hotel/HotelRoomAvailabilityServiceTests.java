package com.travixa.services.hotel;

import com.travixa.entity.hotel_stays.Hotel;
import com.travixa.entity.hotel_stays.Room;
import com.travixa.entity.hotel_stays.RoomAvailability;
import com.travixa.entity.hotel_stays.RoomRate;
import com.travixa.entity.hotel_stays.RoomType;
import com.travixa.dto.hotel.RoomTypeAvailabilityResponse;
import com.travixa.enums.BedType;
import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ListingStatus;
import com.travixa.enums.RatePlanType;
import com.travixa.enums.RoomStatus;
import com.travixa.repository.booking.BookingItemRepository;
import com.travixa.repository.hotel.RoomAvailabilityRepository;
import com.travixa.repository.hotel.RoomRateRepository;
import com.travixa.repository.hotel.RoomRepository;
import com.travixa.repository.hotel.RoomTypeRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HotelRoomAvailabilityServiceTests {

    @Test
    void treatsUnconfiguredCalendarNightsAsAvailableForAnAvailablePhysicalRoom() {
        LocalDate checkIn = LocalDate.now().plusDays(10);
        RoomTypeAvailabilityResponse response = checkStay(checkIn, List.of());

        assertTrue(response.available());
        assertTrue(response.availableUnits() >= 1);
        assertTrue(response.totalRoomCost().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void respectsAnExplicitBlockedNight() {
        LocalDate checkIn = LocalDate.now().plusDays(10);
        Room room = room();
        RoomAvailability blocked = new RoomAvailability();
        blocked.setRoom(room);
        blocked.setAvailableOn(checkIn.plusDays(1));
        blocked.setAvailableUnits(0);

        RoomTypeAvailabilityResponse response = checkStay(checkIn, List.of(blocked));

        assertFalse(response.available());
        assertTrue(response.availableUnits() == 0);
    }

    private RoomTypeAvailabilityResponse checkStay(LocalDate checkIn, List<RoomAvailability> inventory) {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setListingStatus(ListingStatus.PUBLISHED);

        RoomType roomType = new RoomType();
        roomType.setId(2L);
        roomType.setHotel(hotel);
        roomType.setListingStatus(ListingStatus.PUBLISHED);

        Room room = room();
        room.setHotel(hotel);
        room.setRoomType(roomType);

        RoomRate rate = new RoomRate();
        rate.setNightlyRate(new BigDecimal("125.00"));
        rate.setCurrency(CurrencyCode.BDT);
        rate.setRatePlanType(RatePlanType.FLEXIBLE);
        rate.setValidFrom(checkIn.atStartOfDay());
        rate.setValidUntil(checkIn.plusDays(1).atStartOfDay());

        RoomTypeRepository roomTypeRepository = proxy(RoomTypeRepository.class, (method, args) ->
                method.getName().equals("findById") ? Optional.of(roomType) : defaultValue(method));
        RoomRepository roomRepository = proxy(RoomRepository.class, (method, args) ->
                method.getName().equals("findAllByHotel_IdAndRoomType_IdAndStatus") ? List.of(room) : defaultValue(method));
        RoomAvailabilityRepository availabilityRepository = proxy(RoomAvailabilityRepository.class, (method, args) ->
                method.getName().equals("findAllByRoom_IdInAndAvailableOnBetween") ? inventory : defaultValue(method));
        RoomRateRepository rateRepository = proxy(RoomRateRepository.class, (method, args) ->
                method.getName().startsWith("findFirstByRoomType_IdAndActiveTrue") ? Optional.of(rate) : defaultValue(method));
        BookingItemRepository bookingItemRepository = proxy(BookingItemRepository.class, (method, args) ->
                method.getName().equals("countReservedRoomNights") ? 0L : defaultValue(method));

        return new HotelRoomAvailabilityService(roomRepository, roomTypeRepository, availabilityRepository,
                rateRepository, bookingItemRepository)
                .checkAvailability(1L, 2L, checkIn, checkIn.plusDays(2), 1);
    }

    private static <T> T proxy(Class<T> type, RepositoryAnswer answer) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (instance, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "equals" -> instance == args[0];
                            case "hashCode" -> System.identityHashCode(instance);
                            case "toString" -> type.getSimpleName() + " test proxy";
                            default -> null;
                        };
                    }
                    return answer.answer(method, args);
                }));
    }

    private static Object defaultValue(java.lang.reflect.Method method) {
        Class<?> type = method.getReturnType();
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        return 0;
    }

    @FunctionalInterface
    private interface RepositoryAnswer {
        Object answer(java.lang.reflect.Method method, Object[] args);
    }

    private Room room() {
        Room room = new Room();
        room.setId(3L);
        room.setStatus(RoomStatus.AVAILABLE);
        return room;
    }
}
