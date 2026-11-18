package com.travixa.services.hotel;

import com.travixa.dto.hotel.RoomTypeAvailabilityResponse;
import com.travixa.entity.hotel_stays.Room;
import com.travixa.entity.hotel_stays.RoomAvailability;
import com.travixa.entity.hotel_stays.RoomRate;
import com.travixa.entity.hotel_stays.RoomType;
import com.travixa.enums.ListingStatus;
import com.travixa.enums.RoomStatus;
import com.travixa.repository.hotel.RoomAvailabilityRepository;
import com.travixa.repository.hotel.RoomRateRepository;
import com.travixa.repository.hotel.RoomRepository;
import com.travixa.repository.hotel.RoomTypeRepository;
import com.travixa.repository.booking.BookingItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class HotelRoomAvailabilityService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomAvailabilityRepository availabilityRepository;
    private final RoomRateRepository roomRateRepository;
    private final BookingItemRepository bookingItemRepository;

    public HotelRoomAvailabilityService(
            RoomRepository roomRepository,
            RoomTypeRepository roomTypeRepository,
            RoomAvailabilityRepository availabilityRepository,
            RoomRateRepository roomRateRepository,
            BookingItemRepository bookingItemRepository
    ) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.availabilityRepository = availabilityRepository;
        this.roomRateRepository = roomRateRepository;
        this.bookingItemRepository = bookingItemRepository;
    }

    public RoomTypeAvailabilityResponse checkAvailability(
            Long hotelId,
            Long roomTypeId,
            LocalDate checkIn,
            LocalDate checkOut,
            int requestedRooms
    ) {
        if (checkIn == null || checkOut == null
                || !checkOut.isAfter(checkIn)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Check-out must be after check-in"
            );
        }

        if (checkIn.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Check-in cannot be in the past"
            );
        }

        if (requestedRooms < 1 || requestedRooms > 10) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rooms must be between 1 and 10"
            );
        }

        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Room type not found"
                ));

        if (!roomType.getHotel().getId().equals(hotelId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Room type does not belong to this hotel"
            );
        }

        if (roomType.getHotel().getListingStatus()
                != ListingStatus.PUBLISHED
                || roomType.getListingStatus()
                != ListingStatus.PUBLISHED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Hotel or room type is not published"
            );
        }

        List<Room> rooms =
                roomRepository.findAllByHotel_IdAndRoomType_IdAndStatus(
                        hotelId,
                        roomTypeId,
                        RoomStatus.AVAILABLE
                );

        List<Long> roomIds = rooms.stream()
                .map(Room::getId)
                .toList();

        LocalDate lastNight = checkOut.minusDays(1);
        List<RoomAvailability> inventory = roomIds.isEmpty()
                ? List.of()
                : availabilityRepository
                .findAllByRoom_IdInAndAvailableOnBetween(
                        roomIds,
                        checkIn,
                        lastNight
                );

        Map<RoomNight, Integer> configuredCapacity = new HashMap<>();
        for (RoomAvailability record : inventory) {
            configuredCapacity.put(
                    new RoomNight(record.getRoom().getId(), record.getAvailableOn()),
                    Math.min(1, record.getAvailableUnits())
            );
        }

        int nights = Math.toIntExact(
                ChronoUnit.DAYS.between(checkIn, checkOut)
        );

        int minimumAvailableUnits = Integer.MAX_VALUE;

        for (int i = 0; i < nights; i++) {
            LocalDate date = checkIn.plusDays(i);
            int units = 0;
            for (Room room : rooms) {
                int capacity = configuredCapacity.getOrDefault(
                        new RoomNight(room.getId(), date), 1);
                long reserved = bookingItemRepository.countReservedRoomNights(
                        room.getId(), date, date.plusDays(1));
                units += Math.max(0, capacity - Math.toIntExact(reserved));
            }

            minimumAvailableUnits = Math.min(
                    minimumAvailableUnits,
                    units
            );
        }

        if (minimumAvailableUnits == Integer.MAX_VALUE) {
            minimumAvailableUnits = 0;
        }

        List<RoomTypeAvailabilityResponse.DailyRate> dailyRates =
                new ArrayList<>();

        BigDecimal totalRoomCost = BigDecimal.ZERO;
        String currency = null;
        boolean rateAvailable = true;

        for (int i = 0; i < nights; i++) {
            LocalDate date = checkIn.plusDays(i);

            RoomRate rate = roomRateRepository
                    .findFirstByRoomType_IdAndActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqualOrderByValidFromDescNightlyRateAsc(
                            roomTypeId,
                            date.atStartOfDay(),
                            date.atStartOfDay()
                    )
                    .orElse(null);

            if (rate == null) {
                rateAvailable = false;
                continue;
            }

            String rateCurrency = rate.getCurrency().name();

            if (currency != null && !currency.equals(rateCurrency)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Room rates use inconsistent currencies"
                );
            }

            currency = rateCurrency;

            dailyRates.add(
                    new RoomTypeAvailabilityResponse.DailyRate(
                            date,
                            rate.getNightlyRate(),
                            rateCurrency
                    )
            );

            totalRoomCost = totalRoomCost.add(
                    rate.getNightlyRate().multiply(
                            BigDecimal.valueOf(requestedRooms)
                    )
            );
        }

        boolean available =
                minimumAvailableUnits >= requestedRooms
                        && rateAvailable
                        && dailyRates.size() == nights;

        String message;

        if (!rateAvailable || dailyRates.size() != nights) {
            totalRoomCost = null;
            message = "A valid rate is not available for every night";
        } else if (minimumAvailableUnits < requestedRooms) {
            message = "Not enough rooms are available for the selected dates";
        } else {
            message = "Rooms and rates are available for the selected dates";
        }

        BigDecimal firstNightRate = dailyRates.isEmpty()
                ? null
                : dailyRates.get(0).nightlyRate();

        return new RoomTypeAvailabilityResponse(
                hotelId,
                roomTypeId,
                checkIn,
                checkOut,
                requestedRooms,
                minimumAvailableUnits,
                nights,
                available,
                firstNightRate,
                totalRoomCost,
                currency,
                dailyRates,
                message
        );
    }

    private record RoomNight(Long roomId, LocalDate date) {}
}
