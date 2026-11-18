package com.travixa.services.restaurant;

import com.travixa.dto.restaurant.*;
import com.travixa.entity.identity_access.User;
import com.travixa.entity.restaurant.*;
import com.travixa.enums.RestaurantReservationStatus;
import com.travixa.enums.MenuItemStatus;
import com.travixa.enums.ListingStatus;
import com.travixa.mapper.MenuItemMapper;
import com.travixa.mapper.RestaurantReservationMapper;
import com.travixa.repository.identity.UserRepository;
import com.travixa.repository.restaurant.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.LocalTime;

@Service
@Transactional(readOnly = true)
public class RestaurantOperationService {

    private final RestaurantReservationRepository restaurantReservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final DiningTableRepository diningTableRepository;
    private final TableAvailabilityRepository tableAvailabilityRepository;
    private final RestaurantImageRepository restaurantImageRepository;
    private final BuffetScheduleRepository buffetScheduleRepository;
    private final UserRepository userRepository;

    public RestaurantOperationService(
            RestaurantReservationRepository restaurantReservationRepository,
            RestaurantRepository restaurantRepository,
            MenuCategoryRepository menuCategoryRepository,
            MenuItemRepository menuItemRepository,
            DiningTableRepository diningTableRepository,
            TableAvailabilityRepository tableAvailabilityRepository,
            RestaurantImageRepository restaurantImageRepository,
            BuffetScheduleRepository buffetScheduleRepository,
            UserRepository userRepository
    ) {
        this.restaurantReservationRepository = restaurantReservationRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuCategoryRepository = menuCategoryRepository;
        this.menuItemRepository = menuItemRepository;
        this.diningTableRepository = diningTableRepository;
        this.tableAvailabilityRepository = tableAvailabilityRepository;
        this.restaurantImageRepository = restaurantImageRepository;
        this.buffetScheduleRepository = buffetScheduleRepository;
        this.userRepository = userRepository;
    }

    public List<RestaurantReservation> findAll() {
        return restaurantReservationRepository.findAll();
    }

    public Optional<RestaurantReservation> findById(Long id) {
        return restaurantReservationRepository.findById(id);
    }

    public List<RestaurantReservation> findForCustomer(String username) {
        return restaurantReservationRepository
                .findAllByCustomer_UserNameOrderByStartsAtDesc(username);
    }

    public List<MenuCategory> findCategories(Long restaurantId) {
        requireRestaurant(restaurantId);
        return menuCategoryRepository
                .findAllByRestaurant_IdOrderByDisplayOrderAscNameAsc(restaurantId);
    }

    public List<MenuCategory> findPublicCategories(Long restaurantId) {
        Restaurant restaurant = requireRestaurant(restaurantId);
        if (restaurant.getListingStatus() != ListingStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Published restaurant not found");
        }
        return findCategories(restaurantId).stream()
                .filter(MenuCategory::isActive)
                .toList();
    }

    public List<MenuItem> findMenuItems(Long restaurantId) {
        requireRestaurant(restaurantId);
        return menuItemRepository
                .findAllByCategory_Restaurant_IdOrderByNameAsc(restaurantId);
    }

    public List<MenuItem> findPublicMenuItems(Long restaurantId) {
        requireRestaurant(restaurantId);
        findPublicCategories(restaurantId);
        return findMenuItems(restaurantId).stream()
                .filter(item -> item.getCategory().isActive())
                .filter(item -> item.getStatus() == MenuItemStatus.AVAILABLE)
                .toList();
    }

    public List<DiningTable> findTables(Long restaurantId) {
        requireRestaurant(restaurantId);
        return diningTableRepository
                .findAllByRestaurant_IdOrderByTableNumberAsc(restaurantId);
    }

    public List<DiningTable> findPublicTables(Long restaurantId) {
        Restaurant restaurant = requireRestaurant(restaurantId);
        if (restaurant.getListingStatus() != ListingStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Published restaurant not found");
        }
        return findTables(restaurantId).stream()
                .filter(table -> table.getStatus() == com.travixa.enums.RestaurantTableStatus.AVAILABLE)
                .toList();
    }

    public List<TableAvailability> findTableAvailability(Long restaurantId) {
        requireRestaurant(restaurantId);

        List<TableAvailability> nl = new ArrayList<>();
        nl = tableAvailabilityRepository.findAllByTable_Restaurant_IdOrderByAvailableOnAscStartsAtAsc(restaurantId);
        return nl;
    }

    public List<TableAvailability> findPublicTableAvailability(Long restaurantId, java.time.LocalDate date) {
        Restaurant restaurant = requireRestaurant(restaurantId);
        if (restaurant.getListingStatus() != ListingStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Published restaurant not found");
        }
        return tableAvailabilityRepository
                .findAllByTable_Restaurant_IdAndAvailableOnAndAvailableSeatsGreaterThanOrderByStartsAtAsc(
                        restaurantId, date, 0)
                .stream()
                .filter(slot -> slot.getTable().getStatus() == com.travixa.enums.RestaurantTableStatus.AVAILABLE)
                .filter(slot -> date.isAfter(java.time.LocalDate.now(ZoneId.of("Asia/Dhaka")))
                        || slot.getStartsAt().isAfter(LocalTime.now(ZoneId.of("Asia/Dhaka"))))
                .toList();
    }

    @Transactional
    public TableAvailability createTableAvailability(TableAvailabilityRequest request) {
        DiningTable table = requireTableForRestaurant(request.tableId(), request.restaurantId());
        validateTableSlot(table, request);
        if (tableAvailabilityRepository.existsByTable_IdAndAvailableOnAndStartsAt(
                table.getId(), request.availableOn(), request.startsAt())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A slot already exists for this table and start time");
        }
        TableAvailability slot = new TableAvailability();
        slot.setTable(table);
        applyTableSlot(slot, request);
        return tableAvailabilityRepository.save(slot);
    }

    @Transactional
    public TableAvailability updateTableAvailability(Long id, TableAvailabilityRequest request) {
        TableAvailability slot = tableAvailabilityRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Table availability slot not found"));
        DiningTable table = requireTableForRestaurant(request.tableId(), request.restaurantId());
        validateTableSlot(table, request);
        if (tableAvailabilityRepository.existsByTable_IdAndAvailableOnAndStartsAtAndIdNot(
                table.getId(), request.availableOn(), request.startsAt(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A slot already exists for this table and start time");
        }
        slot.setTable(table);
        applyTableSlot(slot, request);
        return tableAvailabilityRepository.save(slot);
    }

    @Transactional
    public void deleteTableAvailability(Long id) {
        delete(tableAvailabilityRepository, id, "Table availability slot");
    }

    public List<RestaurantImage> findImages(Long restaurantId) {
        requireRestaurant(restaurantId);
        return restaurantImageRepository
                .findAllByRestaurant_IdOrderByDisplayOrderAscIdAsc(restaurantId);
    }

    public List<RestaurantImage> findPublicImages(Long restaurantId) {
        Restaurant restaurant = requireRestaurant(restaurantId);
        if (restaurant.getListingStatus() != ListingStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Published restaurant not found");
        }
        return findImages(restaurantId);
    }

    public List<BuffetSchedule> findBuffetSchedules(Long restaurantId) {
        requireRestaurant(restaurantId);
        return buffetScheduleRepository
                .findAllByRestaurant_IdOrderByBuffetDateAscStartsAtAsc(restaurantId);
    }

    public List<BuffetSchedule> findPublicBuffetSchedules(Long restaurantId) {
        Restaurant restaurant = requireRestaurant(restaurantId);
        if (restaurant.getListingStatus() != ListingStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Published restaurant not found");
        }
        return buffetScheduleRepository.findAllByRestaurant_IdOrderByBuffetDateAscStartsAtAsc(restaurantId)
                .stream()
                .filter(schedule -> schedule.getListingStatus() == ListingStatus.PUBLISHED)
                .filter(schedule -> !schedule.getBuffetDate().isBefore(java.time.LocalDate.now(ZoneId.of("Asia/Dhaka"))))
                .toList();
    }

    @Transactional
    public MenuCategory createCategory(MenuCategoryRequest request) {
        Restaurant restaurant = requireRestaurant(request.restaurantId());
        String name = request.name().trim();
        if (menuCategoryRepository.existsByRestaurant_IdAndNameIgnoreCase(
                restaurant.getId(), name
        )) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A menu category with this name already exists");
        }
        MenuCategory category = new MenuCategory();
        category.setRestaurant(restaurant);
        applyCategory(category, request);
        return menuCategoryRepository.save(category);
    }

    @Transactional
    public MenuCategory updateCategory(Long id, MenuCategoryRequest request) {
        MenuCategory category = menuCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Menu category not found"));
        Restaurant restaurant = requireRestaurant(request.restaurantId());
        String name = request.name().trim();
        if (menuCategoryRepository.existsByRestaurant_IdAndNameIgnoreCaseAndIdNot(
                restaurant.getId(), name, id
        )) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A menu category with this name already exists");
        }
        category.setRestaurant(restaurant);
        applyCategory(category, request);
        return menuCategoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        delete(menuCategoryRepository, id, "Menu category");
    }

    @Transactional
    public MenuItem createMenuItem(MenuItemRequest request) {
        MenuCategory category = requireCategory(request.categoryId());
        return menuItemRepository.save(MenuItemMapper.toEntity(request, category));
    }

    @Transactional
    public MenuItem updateMenuItem(Long id, MenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Menu item not found"));
        item.setCategory(requireCategory(request.categoryId()));
        MenuItemMapper.updateEntity(item, request);
        return menuItemRepository.save(item);
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        delete(menuItemRepository, id, "Menu item");
    }

    @Transactional
    public DiningTable createTable(RestaurantTableRequest request) {
        Restaurant restaurant = requireRestaurant(request.restaurantId());
        String tableNumber = request.tableNumber().trim();
        if (diningTableRepository.existsByRestaurant_IdAndTableNumberIgnoreCase(
                restaurant.getId(), tableNumber
        )) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A table with this number already exists");
        }
        DiningTable table = new DiningTable();
        table.setRestaurant(restaurant);
        applyTable(table, request);
        return diningTableRepository.save(table);
    }

    @Transactional
    public DiningTable updateTable(Long id, RestaurantTableRequest request) {
        DiningTable table = diningTableRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Restaurant table not found"));
        Restaurant restaurant = requireRestaurant(request.restaurantId());
        String tableNumber = request.tableNumber().trim();
        if (diningTableRepository.existsByRestaurant_IdAndTableNumberIgnoreCaseAndIdNot(
                restaurant.getId(), tableNumber, id
        )) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A table with this number already exists");
        }
        table.setRestaurant(restaurant);
        applyTable(table, request);
        return diningTableRepository.save(table);
    }

    @Transactional
    public void deleteTable(Long id) {
        delete(diningTableRepository, id, "Restaurant table");
    }

    @Transactional
    public RestaurantImage createImage(RestaurantImageRequest request) {
        RestaurantImage image = new RestaurantImage();
        image.setRestaurant(requireRestaurant(request.restaurantId()));
        applyImage(image, request);
        return restaurantImageRepository.save(image);
    }

    @Transactional
    public RestaurantImage updateImage(Long id, RestaurantImageRequest request) {
        RestaurantImage image = restaurantImageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Restaurant image not found"));
        image.setRestaurant(requireRestaurant(request.restaurantId()));
        applyImage(image, request);
        return restaurantImageRepository.save(image);
    }

    @Transactional
    public void deleteImage(Long id) {
        delete(restaurantImageRepository, id, "Restaurant image");
    }

    @Transactional
    public BuffetSchedule createBuffetSchedule(BuffetScheduleRequest request) {
        BuffetSchedule schedule = new BuffetSchedule();
        schedule.setRestaurant(requireRestaurant(request.restaurantId()));
        applyBuffetSchedule(schedule, request);
        return buffetScheduleRepository.save(schedule);
    }

    @Transactional
    public BuffetSchedule updateBuffetSchedule(Long id, BuffetScheduleRequest request) {
        BuffetSchedule schedule = buffetScheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Buffet schedule not found"));
        schedule.setRestaurant(requireRestaurant(request.restaurantId()));
        applyBuffetSchedule(schedule, request);
        return buffetScheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteBuffetSchedule(Long id) {
        delete(buffetScheduleRepository, id, "Buffet schedule");
    }

    @Transactional
    public RestaurantReservation createReservation(
            RestaurantReservationRequest request,
            String username
    ) {
        Restaurant restaurant = requireRestaurant(request.restaurantId());
        User customer = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"));
        DiningTable table = request.tableId() == null ? null : diningTableRepository
                .findById(request.tableId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Restaurant table not found"));
        if (table != null && !table.getRestaurant().getId().equals(restaurant.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The table does not belong to the selected restaurant");
        }
        if (table != null && table.getCapacity() < request.guestCount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The table does not have enough seats");
        }
        if (table != null) {
            LocalDateTime localStart = LocalDateTime.ofInstant(request.startsAt(), ZoneId.of("Asia/Dhaka"));
            TableAvailability slot = tableAvailabilityRepository.lockByTableAndDateAndStart(
                    table.getId(), localStart.toLocalDate(), localStart.toLocalTime().withSecond(0).withNano(0))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                            "The selected table has no availability for this date and time"));
            if (slot.getAvailableSeats() < request.guestCount()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The selected table slot has no remaining seats");
            }
            slot.setAvailableSeats(slot.getAvailableSeats() - request.guestCount());
        }
        RestaurantReservation reservation = RestaurantReservationMapper.toEntity(
                request, restaurant, table, customer
        );
        reservation.setStatus(RestaurantReservationStatus.PENDING);
        return restaurantReservationRepository.save(reservation);
    }

    @Transactional
    public RestaurantReservation updateReservationStatus(
            Long id,
            RestaurantReservationStatus status
    ) {
        RestaurantReservation reservation = restaurantReservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Reservation not found"));
        if (status == RestaurantReservationStatus.CANCELLED
                && reservation.getStatus() != RestaurantReservationStatus.CANCELLED
                && reservation.getTable() != null) {
            LocalDateTime localStart = LocalDateTime.ofInstant(reservation.getStartsAt(), ZoneId.of("Asia/Dhaka"));
            tableAvailabilityRepository.lockByTableAndDateAndStart(
                    reservation.getTable().getId(), localStart.toLocalDate(),
                    localStart.toLocalTime().withSecond(0).withNano(0))
                    .ifPresent(slot -> slot.setAvailableSeats(Math.min(
                            reservation.getTable().getCapacity(), slot.getAvailableSeats() + reservation.getGuestCount())));
        }
        reservation.setStatus(status);
        return restaurantReservationRepository.save(reservation);
    }

    @Transactional
    public RestaurantReservation save(RestaurantReservation reservation) {
        return restaurantReservationRepository.save(reservation);
    }

    private Restaurant requireRestaurant(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Restaurant not found"));
    }

    private MenuCategory requireCategory(Long id) {
        return menuCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Menu category not found"));
    }

    private void applyCategory(MenuCategory category, MenuCategoryRequest request) {
        category.setName(request.name().trim());
        category.setDescription(request.description() == null ? null : request.description().trim());
        category.setDisplayOrder(request.displayOrder());
        category.setActive(request.active());
    }

    private void applyTable(DiningTable table, RestaurantTableRequest request) {
        table.setTableNumber(request.tableNumber().trim());
        table.setCapacity(request.capacity());
        table.setStatus(request.status());
    }

    private DiningTable requireTableForRestaurant(Long tableId, Long restaurantId) {
        DiningTable table = diningTableRepository.findById(tableId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant table not found"));
        if (!table.getRestaurant().getId().equals(restaurantId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The table does not belong to the selected restaurant");
        }
        return table;
    }

    private void validateTableSlot(DiningTable table, TableAvailabilityRequest request) {
        if (!request.endsAt().isAfter(request.startsAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot end time must be after its start time");
        }
        if (request.availableSeats() > table.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Available seats cannot exceed the table capacity");
        }
    }

    private void applyTableSlot(TableAvailability slot, TableAvailabilityRequest request) {
        slot.setAvailableOn(request.availableOn());
        slot.setStartsAt(request.startsAt());
        slot.setEndsAt(request.endsAt());
        slot.setAvailableSeats(request.availableSeats());
    }

    private void applyImage(RestaurantImage image, RestaurantImageRequest request) {
        image.setStorageKey(request.storageKey().trim());
        image.setAltText(request.altText() == null ? null : request.altText().trim());
        image.setDisplayOrder(request.displayOrder());
        image.setCoverImage(request.coverImage());
    }

    private void applyBuffetSchedule(BuffetSchedule schedule, BuffetScheduleRequest request) {
        if (!request.endsAt().isAfter(request.startsAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Buffet end time must be after start time");
        }
        schedule.setBuffetDate(request.buffetDate());
        schedule.setStartsAt(request.startsAt());
        schedule.setEndsAt(request.endsAt());
        schedule.setAdultPrice(request.adultPrice());
        schedule.setChildPrice(request.childPrice());
        schedule.setCurrency(request.currency());
        schedule.setCapacity(request.capacity());
        schedule.setMenuSummary(request.menuSummary() == null ? null : request.menuSummary().trim());
        schedule.setListingStatus(request.listingStatus());
    }

    private <T> void delete(org.springframework.data.jpa.repository.JpaRepository<T, Long> repository,
                            Long id,
                            String label) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, label + " not found");
        }
        try {
            repository.deleteById(id);
            repository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete " + label.toLowerCase() + " because it is linked to other records", ex);
        }
    }
}
