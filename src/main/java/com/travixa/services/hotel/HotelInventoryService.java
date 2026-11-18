
package com.travixa.services.hotel;

import com.travixa.dto.hotel.RoomRateRequest;
import com.travixa.dto.hotel.RoomAvailabilityRequest;
import com.travixa.dto.hotel.RoomTypeRequest;
import com.travixa.entity.hotel_stays.Hotel;
import com.travixa.entity.hotel_stays.Room;
import com.travixa.entity.hotel_stays.RoomAvailability;
import com.travixa.entity.hotel_stays.RoomRate;
import com.travixa.entity.hotel_stays.RoomType;
import com.travixa.enums.BookingType;
import com.travixa.enums.ListingStatus;
import com.travixa.repository.hotel.HotelRepository;
import com.travixa.repository.hotel.RoomAvailabilityRepository;
import com.travixa.repository.hotel.RoomRateRepository;
import com.travixa.repository.hotel.RoomRepository;
import com.travixa.repository.hotel.RoomTypeRepository;
import com.travixa.repository.booking.BookingItemRepository;
import com.travixa.mapper.RoomTypeMapper;
import com.travixa.mapper.RoomMapper;
import com.travixa.services.platform.MediaService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class HotelInventoryService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRateRepository roomRateRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final HotelRepository hotelRepository;
    private final BookingItemRepository bookingItemRepository;
    private final MediaService mediaService;

    public HotelInventoryService(
            RoomRepository roomRepository,
            RoomTypeRepository roomTypeRepository,
            RoomRateRepository roomRateRepository,
            RoomAvailabilityRepository roomAvailabilityRepository,
            HotelRepository hotelRepository,
            BookingItemRepository bookingItemRepository,
            MediaService mediaService
    ) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomRateRepository = roomRateRepository;
        this.roomAvailabilityRepository = roomAvailabilityRepository;
        this.hotelRepository = hotelRepository;
        this.bookingItemRepository = bookingItemRepository;
        this.mediaService = mediaService;
    }

    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> findRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public List<RoomType> findAllRoomTypes() {
        return roomTypeRepository.findAll();
    }

    public Optional<RoomType> findRoomTypeById(Long id) {
        return roomTypeRepository.findById(id);
    }

    public List<RoomType> findPublishedRoomTypesByHotelId(Long hotelId) {
        return roomTypeRepository.findAllByHotel_IdAndListingStatusOrderByNameAsc(
                hotelId,
                ListingStatus.PUBLISHED
        );
    }

    public Optional<RoomRate> findCurrentRateForRoomType(Long roomTypeId) {
        java.time.LocalDateTime today = LocalDate.now().atStartOfDay();

        return roomRateRepository
                .findFirstByRoomType_IdAndActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqualOrderByValidFromDescNightlyRateAsc(
                        roomTypeId,
                        today,
                        today
                );
    }

    public Optional<RoomRate> findRateById(Long id) {
        return roomRateRepository.findById(id);
    }

    public List<RoomRate> findAllRates() {
        return roomRateRepository.findAll();
    }

    public List<RoomAvailability> findAllAvailability() {
        return roomAvailabilityRepository.findAll(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Order.asc("availableOn"),
                org.springframework.data.domain.Sort.Order.asc("id")));
    }

    @Transactional
    public RoomAvailability createAvailability(RoomAvailabilityRequest request) {
        Room room = roomRepository.findByIdForUpdate(request.roomId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        if (roomAvailabilityRepository.existsByRoom_IdAndAvailableOn(request.roomId(), request.availableOn())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Availability is already configured for this room and date");
        }
        RoomAvailability availability = new RoomAvailability();
        availability.setRoom(room);
        availability.setAvailableOn(request.availableOn());
        availability.setAvailableUnits(request.availableUnits());
        return roomAvailabilityRepository.save(availability);
    }

    @Transactional
    public RoomAvailability updateAvailability(Long id, RoomAvailabilityRequest request) {
        RoomAvailability availability = roomAvailabilityRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Room availability not found"));
        Long previousRoomId = availability.getRoom().getId();
        Long[] roomIdsToLock = previousRoomId.equals(request.roomId())
                ? new Long[]{previousRoomId}
                : java.util.stream.Stream.of(previousRoomId, request.roomId()).sorted().toArray(Long[]::new);
        Room room = null;
        for (Long roomId : roomIdsToLock) {
            Room lockedRoom = roomRepository.findByIdForUpdate(roomId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
            if (roomId.equals(request.roomId())) room = lockedRoom;
        }
        if (roomAvailabilityRepository.existsByRoom_IdAndAvailableOnAndIdNot(
                request.roomId(), request.availableOn(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Availability is already configured for this room and date");
        }
        boolean movingBookedNight = !previousRoomId.equals(room.getId())
                || !availability.getAvailableOn().equals(request.availableOn());
        long reservedOnExistingNight = bookingItemRepository.countReservedRoomNights(
                previousRoomId, availability.getAvailableOn(), availability.getAvailableOn().plusDays(1));
        if (reservedOnExistingNight > 0
                && (movingBookedNight || request.availableUnits() < 1)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Availability for a room night with an active booking cannot be moved or blocked");
        }
        long reservedOnRequestedNight = bookingItemRepository.countReservedRoomNights(
                room.getId(), request.availableOn(), request.availableOn().plusDays(1));
        if (reservedOnRequestedNight > request.availableUnits()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Availability cannot be reduced below the number of existing reservations");
        }
        availability.setRoom(room);
        availability.setAvailableOn(request.availableOn());
        availability.setAvailableUnits(request.availableUnits());
        return roomAvailabilityRepository.save(availability);
    }

    @Transactional
    public void deleteAvailability(Long id) {
        RoomAvailability availability = roomAvailabilityRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Room availability not found"));
        roomRepository.findByIdForUpdate(availability.getRoom().getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        long reservedOnNight = bookingItemRepository.countReservedRoomNights(
                availability.getRoom().getId(), availability.getAvailableOn(),
                availability.getAvailableOn().plusDays(1));
        if (reservedOnNight > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Availability for a room night with an active booking cannot be deleted");
        }
        roomAvailabilityRepository.delete(availability);
    }

    @Transactional
    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    @Transactional
    public Room createRoom(com.travixa.dto.hotel.RoomRequest request) {
        Hotel hotel = requireHotel(request.hotelId());
        RoomType roomType = requireRoomTypeForHotel(request.roomTypeId(), hotel.getId());
        if (roomRepository.existsByHotel_IdAndRoomNumberIgnoreCase(hotel.getId(), request.roomNumber().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A room with this number already exists for this hotel");
        }
        com.travixa.dto.hotel.RoomRequest normalized = new com.travixa.dto.hotel.RoomRequest(
                hotel.getId(), roomType.getId(), request.roomNumber().trim(), request.status(),
                request.floorLabel(), request.viewDescription());
        return roomRepository.save(RoomMapper.toEntity(normalized, hotel, roomType));
    }

    @Transactional
    public Room updateRoom(Long id, com.travixa.dto.hotel.RoomRequest request) {
        Room room = roomRepository.findByIdForUpdate(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        if (bookingItemRepository.existsByResourceTypeAndResourceId(BookingType.HOTEL, id)
                && (!room.getHotel().getId().equals(request.hotelId())
                || !room.getRoomType().getId().equals(request.roomTypeId())
                || !room.getRoomNumber().equalsIgnoreCase(request.roomNumber().trim())
                || !java.util.Objects.equals(room.getFloorLabel(), request.floorLabel())
                || !java.util.Objects.equals(room.getViewDescription(), request.viewDescription()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Room identity and guest-facing details cannot change after a booking references this room");
        }
        Hotel hotel = requireHotel(request.hotelId());
        RoomType roomType = requireRoomTypeForHotel(request.roomTypeId(), hotel.getId());
        if (roomRepository.existsByHotel_IdAndRoomNumberIgnoreCaseAndIdNot(
                hotel.getId(), request.roomNumber().trim(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A room with this number already exists for this hotel");
        }
        room.setHotel(hotel);
        room.setRoomType(roomType);
        RoomMapper.updateEntity(room, new com.travixa.dto.hotel.RoomRequest(
                hotel.getId(), roomType.getId(), request.roomNumber().trim(), request.status(),
                request.floorLabel(), request.viewDescription()));
        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findByIdForUpdate(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        if (bookingItemRepository.existsByResourceTypeAndResourceId(BookingType.HOTEL, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete a room referenced by booking history");
        }
        mediaService.deleteRoomImages(id);
        try {
            roomRepository.delete(room);
            roomRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete this room because it is linked to other records", ex);
        }
    }

    @Transactional
    public RoomType createRoomType(RoomTypeRequest request) {
        Hotel hotel = hotelRepository.findById(request.hotelId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hotel not found"
                ));

        if (roomTypeRepository.existsByHotel_IdAndNameIgnoreCase(
                request.hotelId(), request.name().trim())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A room type with this name already exists for this hotel"
            );
        }

        request = new RoomTypeRequest(
                request.hotelId(),
                request.name().trim(),
                request.category(),
                request.bedType(),
                request.maxOccupancy(),
                request.sizeSquareMeters(),
                request.airConditioned(),
                request.description(),
                request.listingStatus(),
                request.amenities()
        );

        return roomTypeRepository.save(
                RoomTypeMapper.toEntity(request, hotel)
        );
    }

    @Transactional
    public RoomType updateRoomType(Long id, RoomTypeRequest request) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room type not found"
                ));

        Hotel hotel = hotelRepository.findById(request.hotelId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hotel not found"
                ));

        if (roomTypeRepository.existsByHotel_IdAndNameIgnoreCaseAndIdNot(
                request.hotelId(), request.name().trim(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A room type with this name already exists for this hotel"
            );
        }

        roomType.setHotel(hotel);

        RoomTypeRequest normalizedRequest = new RoomTypeRequest(
                request.hotelId(),
                request.name().trim(),
                request.category(),
                request.bedType(),
                request.maxOccupancy(),
                request.sizeSquareMeters(),
                request.airConditioned(),
                request.description(),
                request.listingStatus(),
                request.amenities()
        );

        RoomTypeMapper.updateEntity(roomType, normalizedRequest);
        return roomTypeRepository.save(roomType);
    }

    @Transactional
    public void deleteRoomType(Long id) {
        if (!roomTypeRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Room type not found"
            );
        }

        mediaService.deleteRoomTypeImages(id);
        roomTypeRepository.deleteById(id);
    }

    @Transactional
    public RoomRate createRate(RoomRateRequest request) {
        validateRateDates(request);

        RoomType roomType = roomTypeRepository.findById(request.roomTypeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room type not found"
                ));

        RoomRate rate = new RoomRate();
        applyRateRequest(rate, request, roomType);

        return roomRateRepository.save(rate);
    }

    @Transactional
    public RoomRate updateRate(Long id, RoomRateRequest request) {
        validateRateDates(request);

        RoomRate rate = roomRateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room rate not found"
                ));

        RoomType roomType = roomTypeRepository.findById(request.roomTypeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room type not found"
                ));

        applyRateRequest(rate, request, roomType);
        return roomRateRepository.save(rate);
    }

    @Transactional
    public void deleteRate(Long id) {
        if (!roomRateRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Room rate not found"
            );
        }

        roomRateRepository.deleteById(id);
    }

    private void validateRateDates(RoomRateRequest request) {
        if (request.validUntil().isBefore(request.validFrom())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "validUntil must be on or after validFrom"
            );
        }
    }

    private void applyRateRequest(
            RoomRate rate,
            RoomRateRequest request,
            RoomType roomType
    ) {
        rate.setRoomType(roomType);
        rate.setRatePlanType(request.ratePlanType());
        rate.setNightlyRate(request.nightlyRate());
        rate.setCurrency(request.currency());
        rate.setValidFrom(request.validFrom());
        rate.setValidUntil(request.validUntil());
        rate.setActive(request.active());
    }

    @Transactional
    public RoomRate saveRate(RoomRate roomRate) {
        return roomRateRepository.save(roomRate);
    }

    @Transactional
    public RoomAvailability saveAvailability(
            RoomAvailability roomAvailability
    ) {
        return roomAvailabilityRepository.save(roomAvailability);
    }

    private Hotel requireHotel(Long hotelId) {
        return hotelRepository.findById(hotelId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));
    }

    private RoomType requireRoomTypeForHotel(Long roomTypeId, Long hotelId) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Room type not found"));
        if (!roomType.getHotel().getId().equals(hotelId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The room type does not belong to the selected hotel");
        }
        return roomType;
    }
}
