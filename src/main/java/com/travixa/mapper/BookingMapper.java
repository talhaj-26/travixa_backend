package com.travixa.mapper;

import com.travixa.dto.booking.BookingResponse;
import com.travixa.entity.booking.Booking;
import com.travixa.entity.booking.BookingGuest;
import com.travixa.entity.booking.BookingItem;

import java.util.List;
import java.util.Map;

public final class BookingMapper {

    private BookingMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static BookingResponse toResponse(Booking booking, String paymentStatus, List<BookingItem> items,
            List<BookingGuest> guests, Map<Long, BookingResponse.HotelDetails> hotelDetailsByItemId) {
        List<BookingResponse.Item> itemResponses = items.stream()
                .map(item -> toItemResponse(item, hotelDetailsByItemId.get(item.getId())))
                .toList();
        List<BookingResponse.Guest> guestResponses = guests.stream()
                .map(BookingMapper::toGuestResponse)
                .toList();
        String firstName = booking.getCustomer().getUserFirstName();
        String lastName = booking.getCustomer().getUserLastName();
        String customerName = ((firstName == null ? "" : firstName.trim()) + " "
                + (lastName == null ? "" : lastName.trim())).trim();
        if (customerName.isEmpty()) {
            customerName = booking.getCustomer().getUserName();
        }
        return new BookingResponse(booking.getId(), booking.getReferenceNumber(), booking.getStatus(), paymentStatus,
                booking.getSubtotalAmount(), booking.getDiscountAmount(), booking.getCouponCode(),
                booking.getTotalAmount(), booking.getCurrency(), booking.getPlacedAt(),
                booking.getServiceStartDate(), booking.getServiceEndDate(), booking.getSpecialRequest(),
                booking.getPersonalTouch(),
                booking.getGuestCount(), customerName, booking.getCustomer().getEmail(), itemResponses, guestResponses);
    }

    public static BookingResponse.Item toItemResponse(BookingItem item, BookingResponse.HotelDetails hotelDetails) {
        return new BookingResponse.Item(item.getResourceType(), item.getResourceId(), item.getDescriptionSnapshot(),
                item.getQuantity(), item.getUnitPrice(), item.getLineTotal(), item.getCurrency(), hotelDetails);
    }

    public static BookingResponse.Guest toGuestResponse(BookingGuest guest) {
        return new BookingResponse.Guest(guest.getFullName(), guest.getEmail(), guest.getPhoneNumber(),
                guest.isLeadGuest());
    }
}
