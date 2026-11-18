package com.travixa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.ObjectMapper;
import com.travixa.dto.auth.RefreshTokenRequest;
import com.travixa.dto.auth.TokenResponse;
import com.travixa.entity.identity_access.User;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.entity.hotel_stays.Hotel;
import com.travixa.entity.hotel_stays.Room;
import com.travixa.entity.hotel_stays.RoomRate;
import com.travixa.entity.hotel_stays.RoomType;
import com.travixa.entity.partner_management.PartnerMembership;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.enums.PartnerBusinessType;
import com.travixa.enums.PartnerMembershipRole;
import com.travixa.enums.PartnerOrganizationStatus;
import com.travixa.enums.BedType;
import com.travixa.enums.BookingType;
import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ListingStatus;
import com.travixa.enums.RatePlanType;
import com.travixa.enums.RoomStatus;
import com.travixa.exceptions.PartnerDataIsolationException;
import com.travixa.repository.identity.UserRepository;
import com.travixa.repository.partner.PartnerMembershipRepository;
import com.travixa.repository.partner.PartnerOrganizationRepository;
import com.travixa.repository.hotel.DestinationRepository;
import com.travixa.repository.hotel.HotelRepository;
import com.travixa.repository.hotel.RoomRateRepository;
import com.travixa.repository.hotel.RoomRepository;
import com.travixa.repository.hotel.RoomTypeRepository;
import com.travixa.security.PartnerAccessEvaluator;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@SpringBootTest
@AutoConfigureMockMvc
// These integration tests use the configured PostgreSQL datasource. Roll back
// registration and fixture writes so running the test suite cannot seed the
// developer database with Test Traveler customer accounts.
@Transactional
class TravixaApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PartnerOrganizationRepository partnerOrganizationRepository;

	@Autowired
	private PartnerMembershipRepository partnerMembershipRepository;

	@Autowired
	private PartnerAccessEvaluator partnerAccessEvaluator;

	@Autowired
	private DestinationRepository destinationRepository;

	@Autowired
	private HotelRepository hotelRepository;

	@Autowired
	private RoomTypeRepository roomTypeRepository;

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private RoomRateRepository roomRateRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void hotelBookingInvoiceDemoPaymentAndCustomerIsolationWorkEndToEnd() throws Exception {
		String username = "hotel_guest_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		userRepository.saveAndFlush(new User(username, username + "@example.com", "encoded-password"));

		var organization = createActiveOrganization("booking-" + username);
		Destination destination = new Destination();
		destination.setName("Test Beach");
		destination.setSlug("test-beach-" + username);
		destination.setCountryCode("BD");
		destination.setRegion("Cox's Bazar");
		destination.setPublished(true);
		destination = destinationRepository.saveAndFlush(destination);

		Hotel hotel = new Hotel();
		hotel.setOrganization(organization);
		hotel.setDestination(destination);
		hotel.setName("Test Beach Hotel");
		hotel.setSlug("test-beach-hotel-" + username);
		hotel.setDescription("Integration test hotel");
		hotel.setAddress("Marine Drive, Test Beach");
		hotel.setContactEmail("hotel-" + username + "@example.com");
		hotel.setContactPhone("+8801700000000");
		hotel.setStarRating(4);
		hotel.setListingStatus(ListingStatus.PUBLISHED);
		hotel = hotelRepository.saveAndFlush(hotel);

		RoomType roomType = new RoomType();
		roomType.setHotel(hotel);
		roomType.setName("Deluxe Test Room");
		roomType.setCategory(com.travixa.enums.RoomType.DELUXE);
		roomType.setBedType(BedType.KING);
		roomType.setMaxOccupancy(2);
		roomType.setAirConditioned(true);
		roomType.setAmenities(new ArrayList<>(List.of("Wi-Fi", "Work desk", "Minibar")));
		roomType.setListingStatus(ListingStatus.PUBLISHED);
		roomType = roomTypeRepository.saveAndFlush(roomType);
		var roomTypeUpdate = new com.travixa.dto.hotel.RoomTypeRequest(
				hotel.getId(), "Deluxe Test Room", com.travixa.enums.RoomType.DELUXE, BedType.KING,
				2, null, true, "Room with guest amenities", ListingStatus.PUBLISHED,
				List.of("Wi-Fi", "Work desk", "Minibar"));
		mockMvc.perform(put("/api/hotel-inventory/room-types/{id}", roomType.getId())
				.with(SecurityMockMvcRequestPostProcessors.user("admin").authorities(createAuthorityList("ADMIN")))
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(roomTypeUpdate)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.amenities[0]").value("Wi-Fi"));
		mockMvc.perform(get("/api/hotel-inventory/room-types/{id}", roomType.getId())
				.with(SecurityMockMvcRequestPostProcessors.user("admin").authorities(createAuthorityList("ADMIN"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.amenities[2]").value("Minibar"));
		mockMvc.perform(get("/api/hotels/{hotelId}/room-types", hotel.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].amenities[0]").value("Wi-Fi"))
				.andExpect(jsonPath("$[0].amenities[1]").value("Work desk"))
				.andExpect(jsonPath("$[0].amenities[2]").value("Minibar"));

		Room room = new Room();
		room.setHotel(hotel);
		room.setRoomType(roomType);
		room.setRoomNumber("T-" + username.substring(username.length() - 6));
		room.setFloorLabel("5th Floor");
		room.setViewDescription("Sea view");
		room.setStatus(RoomStatus.AVAILABLE);
		room = roomRepository.saveAndFlush(room);

		LocalDate checkIn = LocalDate.now().plusDays(20);
		LocalDate checkOut = checkIn.plusDays(2);
		RoomRate rate = new RoomRate();
		rate.setRoomType(roomType);
		rate.setRatePlanType(RatePlanType.FLEXIBLE);
		rate.setNightlyRate(new java.math.BigDecimal("12500.00"));
		rate.setCurrency(CurrencyCode.BDT);
		rate.setValidFrom(checkIn.atStartOfDay());
		rate.setValidUntil(checkOut.minusDays(1).atTime(23, 59, 59));
		rate.setActive(true);
		roomRateRepository.saveAndFlush(rate);

		long availabilityId = jdbcTemplate.queryForObject(
				"select coalesce(max(id), 0) + 1 from room_availability", Long.class);
		for (LocalDate night = checkIn; night.isBefore(checkOut); night = night.plusDays(1)) {
			jdbcTemplate.update("insert into room_availability (id, room_id, available_on, available_units, created_at, updated_at) "
					+ "values (?, ?, ?, 1, now(), now())", availabilityId++, room.getId(), night);
		}

		String cartItemJson = objectMapper.writeValueAsString(new com.travixa.dto.booking.CartItemRequest(
				BookingType.HOTEL, room.getId(), 1, "{}"));
		mockMvc.perform(post("/api/carts/items")
				.with(SecurityMockMvcRequestPostProcessors.user(username))
				.contentType(APPLICATION_JSON)
				.content(cartItemJson))
				.andExpect(status().isCreated());

		String checkoutJson = """
				{"idempotencyKey":"hotel-booking-%s","guests":[{"fullName":"Test Guest","email":"guest@example.com","phoneNumber":"+8801800000000","leadGuest":true}],"serviceStartDate":"%s","serviceEndDate":"%s","guestCount":2,"specialRequest":"Quiet room"}
				""".formatted(username, checkIn, checkOut);
		var bookingResult = mockMvc.perform(post("/api/bookings/from-cart")
				.with(SecurityMockMvcRequestPostProcessors.user(username))
				.contentType(APPLICATION_JSON)
				.content(checkoutJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("PENDING_PAYMENT"))
				.andExpect(jsonPath("$.paymentStatus").value("UNPAID"))
				.andExpect(jsonPath("$.subtotalAmount").value(25000.00))
				.andExpect(jsonPath("$.items[0].hotelDetails.hotelName").value("Test Beach Hotel"))
				.andExpect(jsonPath("$.items[0].hotelDetails.destinationName").value("Test Beach"))
				.andExpect(jsonPath("$.items[0].hotelDetails.roomTypeName").value("Deluxe Test Room"))
				.andExpect(jsonPath("$.items[0].hotelDetails.roomNumber").value(room.getRoomNumber()))
				.andExpect(jsonPath("$.items[0].hotelDetails.floorLabel").value("5th Floor"))
				.andExpect(jsonPath("$.items[0].hotelDetails.viewDescription").value("Sea view"))
				.andReturn();
		var bookingJson = objectMapper.readTree(bookingResult.getResponse().getContentAsString());
		long bookingId = bookingJson.get("id").asLong();

		var issuedInvoices = mockMvc.perform(get("/api/invoices")
				.with(SecurityMockMvcRequestPostProcessors.user(username)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].status").value("ISSUED"))
				.andReturn();
		long invoiceId = objectMapper.readTree(issuedInvoices.getResponse().getContentAsString()).get(0).get("id").asLong();
		byte[] invoicePdf = mockMvc.perform(get("/api/invoices/{id}/pdf", invoiceId)
				.with(SecurityMockMvcRequestPostProcessors.user(username)))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/pdf"))
				.andReturn().getResponse().getContentAsByteArray();
		assertTrue(new String(invoicePdf, 0, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("%PDF"));

		String paymentRequest = """
				{"bookingId":%d,"method":"DEMO","idempotencyKey":"hotel-demo-payment-%s"}
				""".formatted(bookingId, username);
		mockMvc.perform(post("/api/payments/initiate")
				.with(SecurityMockMvcRequestPostProcessors.user(username))
				.contentType(APPLICATION_JSON)
				.content(paymentRequest))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status").value("SUCCEEDED"))
				.andExpect(jsonPath("$.demoMode").value(true))
				.andExpect(jsonPath("$.gatewayConfigured").value(false));

		mockMvc.perform(get("/api/bookings/{id}", bookingId)
				.with(SecurityMockMvcRequestPostProcessors.user(username)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("CONFIRMED"))
				.andExpect(jsonPath("$.paymentStatus").value("SUCCEEDED"));
		mockMvc.perform(get("/api/invoices/{id}", invoiceId)
				.with(SecurityMockMvcRequestPostProcessors.user(username)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("PAID"));

		String otherUsername = "other_guest_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		userRepository.saveAndFlush(new User(otherUsername, otherUsername + "@example.com", "encoded-password"));
		mockMvc.perform(get("/api/bookings/{id}", bookingId)
				.with(SecurityMockMvcRequestPostProcessors.user(otherUsername)))
				.andExpect(status().isNotFound());
		mockMvc.perform(get("/api/invoices/{id}", invoiceId)
				.with(SecurityMockMvcRequestPostProcessors.user(otherUsername)))
				.andExpect(status().isNotFound());
	}

	@Test
	void adminEndpointRejectsAnonymousRequests() throws Exception {
		mockMvc.perform(get("/api/admin/security-probe"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void adminEndpointAcceptsAdminAuthority() throws Exception {
		mockMvc.perform(get("/api/admin/security-probe")
				.with(SecurityMockMvcRequestPostProcessors.user("admin")
						.authorities(createAuthorityList("ADMIN"))))
				.andExpect(status().isNotFound());
	}

	@Test
	void adminEndpointRejectsNonAdminAuthority() throws Exception {
		mockMvc.perform(get("/api/admin/security-probe")
				.with(SecurityMockMvcRequestPostProcessors.user("customer")
						.authorities(createAuthorityList("CUSTOMER"))))
				.andExpect(status().isForbidden());
	}

	@Test
	void registrationReturnsCustomerTokenWithoutSensitiveFields() throws Exception {
		String username = "traveler_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		String email = username + "@example.com";

		mockMvc.perform(post("/api/auth/register")
				.contentType(APPLICATION_JSON)
				.content(registrationJson(username, email)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.username").value(username))
				.andExpect(jsonPath("$.roles[0]").value("CUSTOMER"))
				.andExpect(jsonPath("$.password").doesNotExist());
	}

	@Test
	void registeredCustomerCanSignIn() throws Exception {
		String username = "traveler_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		String email = username + "@example.com";
		mockMvc.perform(post("/api/auth/register")
				.contentType(APPLICATION_JSON)
				.content(registrationJson(username, email)))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/auth/login")
				.contentType(APPLICATION_JSON)
				.content("""
						{"username":"%s","password":"StrongPass123"}
						""".formatted(username)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.username").value(username));
	}

	@Test
	void refreshRotatesTokenAndRejectsReplay() throws Exception {
		String username = "traveler_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		var registration = mockMvc.perform(post("/api/auth/register")
				.contentType(APPLICATION_JSON)
				.content(registrationJson(username, username + "@example.com")))
				.andExpect(status().isCreated())
				.andReturn();
		TokenResponse original = objectMapper.readValue(registration.getResponse().getContentAsString(),
				TokenResponse.class);

		var refresh = mockMvc.perform(post("/api/auth/refresh")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new RefreshTokenRequest(original.refreshToken()))))
				.andExpect(status().isOk())
				.andReturn();
		TokenResponse rotated = objectMapper.readValue(refresh.getResponse().getContentAsString(), TokenResponse.class);
		org.junit.jupiter.api.Assertions.assertNotEquals(original.refreshToken(), rotated.refreshToken());

		mockMvc.perform(post("/api/auth/refresh")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new RefreshTokenRequest(original.refreshToken()))))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void logoutRevokesRefreshTokenForAuthenticatedUser() throws Exception {
		String username = "traveler_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		var registration = mockMvc.perform(post("/api/auth/register")
				.contentType(APPLICATION_JSON)
				.content(registrationJson(username, username + "@example.com")))
				.andExpect(status().isCreated())
				.andReturn();
		TokenResponse tokens = objectMapper.readValue(registration.getResponse().getContentAsString(),
				TokenResponse.class);

		mockMvc.perform(post("/api/auth/logout")
				.header("Authorization", "Bearer " + tokens.accessToken())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new RefreshTokenRequest(tokens.refreshToken()))))
				.andExpect(status().isNoContent());

		mockMvc.perform(post("/api/auth/refresh")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new RefreshTokenRequest(tokens.refreshToken()))))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void registrationRejectsDuplicateEmail() throws Exception {
		String username = "traveler_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		String email = username + "@example.com";
		String request = registrationJson(username, email);
		mockMvc.perform(post("/api/auth/register").contentType(APPLICATION_JSON).content(request))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/auth/register").contentType(APPLICATION_JSON).content(request))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.status").value(409));
	}

	@Test
	void registrationValidatesRequestFields() throws Exception {
		mockMvc.perform(post("/api/auth/register")
				.contentType(APPLICATION_JSON)
				.content("""
						{"username":"x","email":"not-an-email","password":"short"}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	@Transactional
	void partnerAccessIsRestrictedToActiveOrganizationMembership() throws Exception {
		String username = "partner_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		User partnerUser = userRepository.saveAndFlush(
				new User(username, username + "@example.com", "encoded-password"));
		PartnerOrganization ownOrganization = createActiveOrganization("own");
		PartnerOrganization otherOrganization = createActiveOrganization("other");

		PartnerMembership membership = new PartnerMembership();
		membership.setUser(partnerUser);
		membership.setOrganization(ownOrganization);
		membership.setMembershipRole(PartnerMembershipRole.OWNER);
		partnerMembershipRepository.saveAndFlush(membership);

		assertTrue(partnerAccessEvaluator.canAccessOrganization(username, ownOrganization.getId()));
		assertFalse(partnerAccessEvaluator.canAccessOrganization(username, otherOrganization.getId()));
		assertThrows(PartnerDataIsolationException.class,
				() -> partnerAccessEvaluator.requireOrganizationAccess(username, otherOrganization.getId()));

		mockMvc.perform(get("/api/partners-organizations/{id}", ownOrganization.getId())
				.with(SecurityMockMvcRequestPostProcessors.user(username)))
				.andExpect(status().isOk());
		mockMvc.perform(get("/api/partners-organizations/{id}", otherOrganization.getId())
				.with(SecurityMockMvcRequestPostProcessors.user(username)))
				.andExpect(status().isForbidden());
		mockMvc.perform(get("/api/partners-organizations")
				.with(SecurityMockMvcRequestPostProcessors.user(username)))
				.andExpect(status().isForbidden());
	}

	private PartnerOrganization createActiveOrganization(String label) {
		String slug = "partner-" + label + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		PartnerOrganization organization = new PartnerOrganization();
		organization.setName("Partner " + label);
		organization.setSlug(slug);
		organization.setBusinessType(PartnerBusinessType.HOTEL);
		organization.setContactEmail(slug + "@example.com");
		organization.setStatus(PartnerOrganizationStatus.ACTIVE);
		return partnerOrganizationRepository.saveAndFlush(organization);
	}

	@Test
	void postmanCollectionIsValidJson() throws Exception {
		var collection = objectMapper.readTree(Files.readString(Path.of(
				"postman", "Travixa Auth API.postman_collection.json")));

		org.junit.jupiter.api.Assertions.assertTrue(collection.isObject());
		org.junit.jupiter.api.Assertions.assertEquals(11, collection.get("item").size());
	}

	private String registrationJson(String username, String email) {
		return """
				{"username":"%s","email":"%s","password":"StrongPass123","firstName":"Test","lastName":"Traveler"}
				""".formatted(username, email);
	}

}
