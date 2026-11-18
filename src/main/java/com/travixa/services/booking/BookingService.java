package com.travixa.services.booking;

import com.travixa.common.ReferenceNumberGenerator;
import com.travixa.dto.booking.BookingCreateRequest;
import com.travixa.dto.booking.BookingItemRequest;
import com.travixa.dto.booking.BookingResponse;
import com.travixa.entity.booking.Booking;
import com.travixa.entity.booking.BookingGuest;
import com.travixa.entity.booking.BookingItem;
import com.travixa.entity.hotel_stays.Room;
import com.travixa.entity.hotel_stays.RoomRate;
import com.travixa.entity.hotel_stays.RoomAvailability;
import com.travixa.entity.identity_access.User;
import com.travixa.entity.restaurant.MenuItem;
import com.travixa.entity.tour_guide.TourPackage;
import com.travixa.entity.tour_guide.PackageDeparture;
import com.travixa.entity.tour_guide.GuideOffering;
import com.travixa.entity.transport.TransportSchedule;
import com.travixa.enums.*;
import com.travixa.repository.booking.BookingGuestRepository;
import com.travixa.repository.booking.BookingItemRepository;
import com.travixa.repository.booking.BookingRepository;
import com.travixa.repository.hotel.RoomRateRepository;
import com.travixa.repository.hotel.RoomRepository;
import com.travixa.repository.hotel.RoomAvailabilityRepository;
import com.travixa.repository.hotel.HotelImageRepository;
import com.travixa.repository.hotel.RoomTypeImageRepository;
import com.travixa.repository.identity.UserRepository;
import com.travixa.repository.restaurant.MenuItemRepository;
import com.travixa.repository.tour.TourPackageRepository;
import com.travixa.repository.tour.PackageDepartureRepository;
import com.travixa.repository.tour.GuideOfferingRepository;
import com.travixa.repository.transport.TransportScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.travixa.repository.payment.PaymentRepository;
import com.travixa.services.payment.CouponService;
import com.travixa.services.payment.InvoiceService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final BookingGuestRepository bookingGuestRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoomRateRepository roomRateRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final HotelImageRepository hotelImageRepository;
    private final RoomTypeImageRepository roomTypeImageRepository;
    private final TourPackageRepository tourPackageRepository;
    private final PackageDepartureRepository packageDepartureRepository;
    private final GuideOfferingRepository guideOfferingRepository;
    private final MenuItemRepository menuItemRepository;
    private final TransportScheduleRepository transportScheduleRepository;
    private final PaymentRepository paymentRepository;
    private final CouponService couponService;
    private final InvoiceService invoiceService;

    public BookingService(
            BookingRepository bookingRepository,
            BookingItemRepository bookingItemRepository,
            BookingGuestRepository bookingGuestRepository,
            UserRepository userRepository,
            RoomRepository roomRepository,
            RoomRateRepository roomRateRepository,
            RoomAvailabilityRepository roomAvailabilityRepository,
            HotelImageRepository hotelImageRepository,
            RoomTypeImageRepository roomTypeImageRepository,
            TourPackageRepository tourPackageRepository,
            PackageDepartureRepository packageDepartureRepository,
            GuideOfferingRepository guideOfferingRepository,
            MenuItemRepository menuItemRepository,
            TransportScheduleRepository transportScheduleRepository,
            PaymentRepository paymentRepository,
            CouponService couponService,
            InvoiceService invoiceService) {

        this.bookingRepository = bookingRepository;
        this.bookingItemRepository = bookingItemRepository;
        this.bookingGuestRepository = bookingGuestRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.roomRateRepository = roomRateRepository;
        this.roomAvailabilityRepository = roomAvailabilityRepository;
        this.hotelImageRepository = hotelImageRepository;
        this.roomTypeImageRepository = roomTypeImageRepository;
        this.tourPackageRepository = tourPackageRepository;
        this.packageDepartureRepository = packageDepartureRepository;
        this.guideOfferingRepository = guideOfferingRepository;
        this.menuItemRepository = menuItemRepository;
        this.transportScheduleRepository = transportScheduleRepository;
        this.paymentRepository = paymentRepository;
        this.couponService = couponService;
        this.invoiceService = invoiceService;
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> findAllByCustomer(String userName) {
        return bookingRepository.findAllByCustomer_UserName(userName);
    }

    public Optional<Booking> findByIdAndCustomer(
            Long id, String userName) {
        return bookingRepository.findByIdAndCustomer_UserName(
                id, userName);
    }

    public List<BookingItem> findItemsByBookingId(Long bookingId) {
        return bookingItemRepository.findAllByBooking_Id(bookingId);
    }

    public List<BookingGuest> findGuestsByBookingId(Long bookingId) {
        return bookingGuestRepository.findAllByBooking_Id(bookingId);
    }

    public java.util.Map<Long, BookingResponse.HotelDetails> hotelDetailsByBookingItem(List<BookingItem> items) {
        java.util.Map<Long, BookingResponse.HotelDetails> details = new java.util.HashMap<>();
        for (BookingItem item : items) {
            if (item.getResourceType() != BookingType.HOTEL) continue;
            roomRepository.findById(item.getResourceId()).ifPresent(room -> {
                var hotel = room.getHotel();
                var destination = hotel.getDestination();
                String hotelImageUrl = hotelImageRepository.findAllByHotel_IdOrderByDisplayOrderAscIdAsc(hotel.getId())
                        .stream().filter(image -> image.isCoverImage()).findFirst()
                        .or(() -> hotelImageRepository.findAllByHotel_IdOrderByDisplayOrderAscIdAsc(hotel.getId())
                                .stream().findFirst())
                        .map(image -> "/api/media/" + image.getStorageKey()).orElse(null);
                String roomImageUrl = roomTypeImageRepository
                        .findAllByRoomType_IdOrderByDisplayOrderAscIdAsc(room.getRoomType().getId())
                        .stream().filter(image -> image.isCoverImage()).findFirst()
                        .or(() -> roomTypeImageRepository
                                .findAllByRoomType_IdOrderByDisplayOrderAscIdAsc(room.getRoomType().getId())
                                .stream().findFirst())
                        .map(image -> "/api/media/" + image.getStorageKey()).orElse(null);
                details.put(item.getId(), new BookingResponse.HotelDetails(hotel.getId(), hotel.getName(),
                        destination.getName(), destination.getRegion(), destination.getCountryCode(), hotel.getAddress(),
                        hotel.getStarRating(), hotelImageUrl, room.getRoomType().getId(), room.getRoomType().getName(),
                        room.getRoomType().getCategory().name(), room.getRoomNumber(), room.getFloorLabel(),
                        room.getViewDescription(), roomImageUrl, hotel.getCheckInTime(), hotel.getCheckOutTime(),
                        hotel.getCancellationPolicy(), hotel.getChildPolicy()));
            });
        }
        return details;
    }

    public String paymentStatusForBooking(Long bookingId) {
        return paymentRepository.findFirstByBooking_IdOrderByCreatedAtDesc(bookingId)
                .map(payment -> payment.getStatus().name())
                .orElse("UNPAID");
    }

    @Transactional
    public Booking create(
            BookingCreateRequest request,
            String username) {

        Booking existing = bookingRepository
                .findByIdempotencyKey(request.idempotencyKey())
                .orElse(null);

        if (existing != null) {
            if (existing.getCustomer() == null
                    || !existing.getCustomer().getUserName()
                    .equals(username)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Idempotency key already used");
            }

            return existing;
        }

        User customer = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"));

        if (request.items() == null || request.items().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one booking item is required");
        }

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setReferenceNumber(
                ReferenceNumberGenerator.generate("TRV"));
        booking.setIdempotencyKey(request.idempotencyKey());
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setPlacedAt(Instant.now());
        booking.setServiceStartDate(request.serviceStartDate());
        booking.setServiceEndDate(request.serviceEndDate());
        booking.setSpecialRequest(request.specialRequest() == null || request.specialRequest().isBlank()
                ? null : request.specialRequest().trim());
        booking.setPersonalTouch(request.personalTouch() == null || request.personalTouch().isBlank()
                ? null : request.personalTouch().trim());
        int guestCount = request.guestCount() == null ? 1 : request.guestCount();
        booking.setGuestCount(guestCount);

        boolean hasHotelItem = request.items().stream().anyMatch(item -> item.resourceType() == BookingType.HOTEL);
        if (hasHotelItem && (request.serviceStartDate() == null || request.serviceEndDate() == null
                || !request.serviceEndDate().isAfter(request.serviceStartDate()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Hotel bookings require a check-in date and a check-out date after check-in");
        }
        if (request.serviceEndDate() != null && request.serviceStartDate() != null
                && !request.serviceEndDate().isAfter(request.serviceStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service end date must be after start date");
        }

        BigDecimal total = BigDecimal.ZERO;
        CurrencyCode currency = null;
        List<BookingItem> items = new ArrayList<>();
        Set<Long> hotelRoomsAllocatedInRequest = new HashSet<>();

        for (BookingItemRequest requested : request.items()) {

            if (requested.quantity() <= 0
                    || requested.quantity() > 100) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Quantity must be between 1 and 100");
            }

            if (requested.resourceType() == BookingType.HOTEL) {
                Room seedRoom = roomRepository.findById(requested.resourceId()).orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel room not found"));
                long guestCapacity = (long) seedRoom.getRoomType().getMaxOccupancy() * requested.quantity();
                if (guestCount > guestCapacity) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Guest count exceeds the selected room capacity");
                }
            }

            List<BookingItem> pricedItems = requested.resourceType() == BookingType.HOTEL
                    ? priceHotelRooms(requested, request.serviceStartDate(), request.serviceEndDate(),
                    hotelRoomsAllocatedInRequest)
                    : List.of(priceItem(requested, request.serviceStartDate(), request.serviceEndDate()));

            for (BookingItem item : pricedItems) {
                item.setBooking(booking);
                item.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

                if (currency != null && currency != item.getCurrency()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A booking cannot mix currencies");
                }

                currency = item.getCurrency();
                total = total.add(item.getLineTotal());
                items.add(item);
            }
        }

        CurrencyCode bookingCurrency = currency == null ? CurrencyCode.BDT : currency;
        booking.setCurrency(bookingCurrency);
        booking.setSubtotalAmount(total);
        CouponService.AppliedCoupon appliedCoupon = couponService.validateForBooking(
                request.couponCode(), username, total, bookingCurrency);
        BigDecimal discount = appliedCoupon == null ? BigDecimal.ZERO : appliedCoupon.discountAmount();
        booking.setDiscountAmount(discount);
        booking.setCouponCode(appliedCoupon == null ? null : appliedCoupon.coupon().getCode());
        booking.setTotalAmount(total.subtract(discount));

        Booking saved = bookingRepository.saveAndFlush(booking);
        couponService.recordRedemption(appliedCoupon, customer, saved, bookingCurrency);

        for (BookingItem item : items) {
            item.setBooking(saved);
        }
        bookingItemRepository.saveAll(items);

        if (request.guests() != null) {
            for (var guestRequest : request.guests()) {
                BookingGuest guest = new BookingGuest();
                guest.setBooking(saved);
                guest.setFullName(guestRequest.fullName().trim());
                guest.setEmail(guestRequest.email());
                guest.setPhoneNumber(guestRequest.phoneNumber());
                guest.setLeadGuest(guestRequest.leadGuest());

                bookingGuestRepository.save(guest);
            }
        }

        invoiceService.issueForBooking(saved);

        return saved;
    }

    private BookingItem priceItem(BookingItemRequest request, LocalDate serviceStartDate, LocalDate serviceEndDate) {

        BookingItem item = new BookingItem();
        item.setResourceType(request.resourceType());
        item.setResourceId(request.resourceId());

        switch (request.resourceType()) {

            case HOTEL -> {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Hotel rooms must be priced from a locked physical-room allocation");
            }

            case TOUR -> {
                TourPackage tourPackage =
                        tourPackageRepository.findById(
                                        request.resourceId())
                                .orElseThrow(
                                        () -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Tour package not found"));

                if (tourPackage.getListingStatus()
                        != ListingStatus.PUBLISHED) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Tour package is not published");
                }
                if (request.quantity() > tourPackage.getMaximumGroupSize()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Requested party size exceeds this package's maximum group size");
                }

                PackageDeparture departure = request.departureId() == null
                        ? findNextBookableDeparture(tourPackage.getId(), request.quantity())
                        : packageDepartureRepository.findByIdForUpdate(request.departureId())
                                .orElseThrow(() -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Package departure not found"));
                if (!departure.getTourPackage().getId().equals(tourPackage.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Selected departure does not belong to this package");
                }
                validateDeparture(departure, request.quantity());
                departure.setSeatsRemaining(departure.getSeatsRemaining() - request.quantity());
                if (departure.getSeatsRemaining() == 0) departure.setStatus(ScheduleStatus.FULL);
                packageDepartureRepository.save(departure);

                item.setOrganization(tourPackage.getOrganization());
                item.setDescriptionSnapshot(
                        safeDescription(tourPackage.getName()));
                item.setUnitPrice(departure.getPriceOverride() == null
                        ? tourPackage.getBasePrice() : departure.getPriceOverride());
                item.setCurrency(departure.getPriceOverride() == null
                        ? tourPackage.getCurrency() : departure.getCurrency());
                item.setTourDeparture(departure);
            }

            case TRANSPORT -> {
                TransportSchedule schedule =
                        transportScheduleRepository.findByIdForUpdate(
                                        request.resourceId())
                                .orElseThrow(
                                        () -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Transport schedule not found"));

                if (schedule.getStatus() != ScheduleStatus.SCHEDULED
                        || !schedule.getDepartureAt().isAfter(Instant.now())
                        || schedule.getRoute().getStatus() != RouteStatus.ACTIVE
                        || schedule.getVehicle().getStatus() != VehicleStatus.ACTIVE
                        || !schedule.getOrganization().getId().equals(schedule.getRoute().getOrganization().getId())
                        || !schedule.getOrganization().getId().equals(schedule.getVehicle().getOrganization().getId())) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Transport schedule is not bookable");
                }

                long reservedSeats = bookingItemRepository.countReservedTransportSeats(schedule.getId());
                if (reservedSeats + request.quantity() > schedule.getVehicle().getSeatCapacity()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Not enough seats remain on this transport schedule");
                }

                item.setOrganization(schedule.getOrganization());
                item.setDescriptionSnapshot(
                        safeDescription(
                                schedule.getRoute().getOriginName()
                                        + " to "
                                        + schedule.getRoute()
                                        .getDestinationName()));
                item.setUnitPrice(schedule.getTicketPrice());
                item.setCurrency(schedule.getCurrency());
            }

            case GUIDE -> {
                GuideOffering offering = guideOfferingRepository.findById(request.resourceId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide offering not found"));
                if (!offering.isActive()
                        || offering.getGuideProfile().getStatus() != TourGuideStatus.ACTIVE
                        || offering.getGuideProfile().getOrganization() == null
                        || offering.getGuideProfile().getOrganization().getStatus() != PartnerOrganizationStatus.ACTIVE) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Guide offering is not bookable");
                }
                item.setOrganization(offering.getGuideProfile().getOrganization());
                item.setDescriptionSnapshot(safeDescription(offering.getGuideProfile().getUser().getUserName() + " - " + offering.getName()));
                item.setUnitPrice(offering.getPricePerHour());
                item.setCurrency(offering.getCurrency());
            }

            case RESTAURANT -> {
                MenuItem menuItem = menuItemRepository.findById(
                                request.resourceId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Restaurant menu item not found"));

                if (menuItem.getStatus() != MenuItemStatus.AVAILABLE
                        || !menuItem.getCategory().isActive()
                        || menuItem.getCategory().getRestaurant()
                        .getListingStatus()
                        != ListingStatus.PUBLISHED) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Restaurant menu item is not bookable");
                }

                item.setOrganization(
                        menuItem.getCategory().getRestaurant()
                                .getOrganization());
                item.setDescriptionSnapshot(
                        safeDescription(
                                menuItem.getCategory().getRestaurant()
                                        .getName()
                                        + " - " + menuItem.getName()));
                item.setUnitPrice(menuItem.getPrice());
                item.setCurrency(menuItem.getCurrency());
            }
        }

        return item;
    }

    private List<BookingItem> priceHotelRooms(BookingItemRequest request, LocalDate checkIn, LocalDate checkOut,
                                               Set<Long> hotelRoomsAllocatedInRequest) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid hotel stay is required");
        }
        if (request.quantity() > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hotel bookings are limited to 10 rooms");
        }

        Room requestedRoom = roomRepository.findById(request.resourceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel room not found"));
        Long hotelId = requestedRoom.getHotel().getId();
        Long roomTypeId = requestedRoom.getRoomType().getId();
        List<Room> candidates = roomRepository.findAllByHotel_IdAndRoomType_IdAndStatusOrderByIdAsc(
                hotelId, roomTypeId, RoomStatus.AVAILABLE);

        List<Long> candidateIds = new ArrayList<>();
        candidateIds.add(requestedRoom.getId());
        candidates.stream().map(Room::getId).filter(id -> !id.equals(requestedRoom.getId())).forEach(candidateIds::add);
        candidateIds.sort(Long::compareTo);

        List<BookingItem> allocated = new ArrayList<>();
        for (Long candidateId : candidateIds) {
            if (allocated.size() >= request.quantity()) break;
            if (hotelRoomsAllocatedInRequest.contains(candidateId)) continue;
            Room room = roomRepository.findByIdForUpdate(candidateId).orElse(null);
            if (room == null || room.getStatus() != RoomStatus.AVAILABLE
                    || !room.getHotel().getId().equals(hotelId)
                    || !room.getRoomType().getId().equals(roomTypeId)
                    || room.getHotel().getListingStatus() != ListingStatus.PUBLISHED
                    || room.getRoomType().getListingStatus() != ListingStatus.PUBLISHED
                    || !hasInventoryForStay(room, checkIn, checkOut)
                    || bookingItemRepository.countReservedRoomNights(room.getId(), checkIn, checkOut) > 0) {
                continue;
            }
            allocated.add(priceHotelRoom(room, checkIn, checkOut));
            hotelRoomsAllocatedInRequest.add(room.getId());
        }

        if (allocated.size() != request.quantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Not enough physical rooms are available for the complete stay");
        }
        return allocated;
    }

    private boolean hasInventoryForStay(Room room, LocalDate checkIn, LocalDate checkOut) {
        List<RoomAvailability> entries = roomAvailabilityRepository.findAllByRoom_IdInAndAvailableOnBetween(
                List.of(room.getId()), checkIn, checkOut.minusDays(1));
        for (LocalDate night = checkIn; night.isBefore(checkOut); night = night.plusDays(1)) {
            LocalDate currentNight = night;
            RoomAvailability entry = entries.stream()
                    .filter(candidate -> candidate.getAvailableOn().equals(currentNight))
                    .findFirst().orElse(null);
            int physicalRoomCapacity = entry == null ? 1 : Math.min(1, entry.getAvailableUnits());
            if (physicalRoomCapacity < 1
                    || bookingItemRepository.countReservedRoomNights(room.getId(), night, night.plusDays(1))
                    >= physicalRoomCapacity) {
                return false;
            }
        }
        return true;
    }

    private BookingItem priceHotelRoom(Room room, LocalDate checkIn, LocalDate checkOut) {
        BigDecimal stayTotal = BigDecimal.ZERO;
        CurrencyCode currency = null;
        for (LocalDate night = checkIn; night.isBefore(checkOut); night = night.plusDays(1)) {
            RoomRate rate = roomRateRepository
                    .findFirstByRoomType_IdAndActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqualOrderByValidFromDescNightlyRateAsc(
                            room.getRoomType().getId(), night.atStartOfDay(), night.atStartOfDay())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                            "No active room rate is configured for every night of this stay"));
            if (currency != null && currency != rate.getCurrency()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Room rates use inconsistent currencies across the selected stay");
            }
            currency = rate.getCurrency();
            stayTotal = stayTotal.add(rate.getNightlyRate());
        }

        BookingItem item = new BookingItem();
        item.setResourceType(BookingType.HOTEL);
        item.setResourceId(room.getId());
        item.setOrganization(room.getHotel().getOrganization());
        item.setDescriptionSnapshot(safeDescription(room.getHotel().getName() + " · "
                + room.getHotel().getDestination().getName() + " · " + room.getHotel().getAddress() + " · "
                + room.getRoomType().getName() + " · Room " + room.getRoomNumber()
                + (room.getFloorLabel() == null || room.getFloorLabel().isBlank() ? "" : " · " + room.getFloorLabel())
                + (room.getViewDescription() == null || room.getViewDescription().isBlank() ? "" : " · " + room.getViewDescription())));
        item.setQuantity(1);
        item.setUnitPrice(stayTotal);
        item.setCurrency(currency);
        return item;
    }

    private String safeDescription(String value) {
        return value.length() <= 240
                ? value
                : value.substring(0, 240);
    }

    @Transactional
    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }


    @Transactional
    public void deleteById(Long id, String username) {
        Booking booking = bookingRepository
                .findByIdAndCustomerForUpdate(id, username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found"
                ));

        if (paymentRepository.existsByBooking_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Booking cannot be deleted because payment records exist"
            );
        }

        if (booking.getStatus() != BookingStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cancel the booking before deleting it"
            );
        }

        // Capacity was released when the booking was cancelled.

        bookingGuestRepository.deleteAll(
                bookingGuestRepository.findAllByBooking_Id(id)
        );

        bookingItemRepository.deleteAll(
                bookingItemRepository.findAllByBooking_Id(id)
        );

        bookingRepository.delete(booking);
    }



    public Optional<Booking> findByIdempotencyKey(String idempotencyKey) {
        return bookingRepository.findByIdempotencyKey(idempotencyKey);
    }

    @Transactional
    public Booking cancelByCustomer(Long id, String username) {
        Booking booking = bookingRepository
                .findByIdAndCustomerForUpdate(id, username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found"
                ));

        return cancelPendingBooking(booking);
    }

    @Transactional
    public Booking cancelByAdmin(Long id) {
        Booking booking = bookingRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found"
                ));
        return cancelPendingBooking(booking);
    }

    private Booking cancelPendingBooking(Booking booking) {
        Long id = booking.getId();
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return booking;
        }

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This booking cannot be cancelled in its current status"
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);
        invoiceService.voidForBooking(id);
        for (BookingItem item : bookingItemRepository.findAllByBooking_Id(id)) {
            PackageDeparture departure = item.getTourDeparture();
            if (departure == null) continue;
            PackageDeparture locked = packageDepartureRepository.findByIdForUpdate(departure.getId())
                    .orElse(null);
            if (locked == null) continue;
            locked.setSeatsRemaining(Math.min(locked.getSeatCapacity(),
                    locked.getSeatsRemaining() + item.getQuantity()));
            if (locked.getStatus() == ScheduleStatus.FULL && locked.getSeatsRemaining() > 0) {
                locked.setStatus(ScheduleStatus.SCHEDULED);
            }
            packageDepartureRepository.save(locked);
        }
        return bookingRepository.save(booking);
    }

    private PackageDeparture findNextBookableDeparture(Long packageId, int quantity) {
        Instant now = Instant.now();
        return packageDepartureRepository.findAllByTourPackage_IdAndStatusAndStartsAtAfterOrderByStartsAtAsc(
                        packageId, ScheduleStatus.SCHEDULED, now).stream()
                .filter(item -> item.getSeatsRemaining() >= quantity)
                .map(item -> packageDepartureRepository.findByIdForUpdate(item.getId()).orElse(null))
                .filter(java.util.Objects::nonNull)
                .filter(item -> item.getStartsAt().isAfter(now)
                        && item.getStatus() == ScheduleStatus.SCHEDULED
                        && item.getSeatsRemaining() >= quantity)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                        "No upcoming package departure has enough seats"));
    }

    private void validateDeparture(PackageDeparture departure, int quantity) {
        if (departure.getStatus() != ScheduleStatus.SCHEDULED
                || !departure.getStartsAt().isAfter(Instant.now())
                || departure.getSeatsRemaining() < quantity) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Selected package departure is not available for the requested party size");
        }
    }

}
