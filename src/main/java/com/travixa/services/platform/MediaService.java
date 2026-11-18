package com.travixa.services.platform;

import com.travixa.dto.media.MediaUploadResponse;
import com.travixa.entity.hotel_stays.HotelImage;
import com.travixa.entity.hotel_stays.RoomTypeImage;
import com.travixa.entity.hotel_stays.RoomImage;
import com.travixa.entity.restaurant.RestaurantImage;
import com.travixa.entity.restaurant.MenuItem;
import com.travixa.entity.tour_guide.PackageImage;
import com.travixa.enums.ListingStatus;
import com.travixa.exceptions.InvalidRequestException;
import com.travixa.repository.hotel.HotelImageRepository;
import com.travixa.repository.hotel.HotelRepository;
import com.travixa.repository.hotel.RoomTypeRepository;
import com.travixa.repository.hotel.RoomTypeImageRepository;
import com.travixa.repository.hotel.RoomImageRepository;
import com.travixa.repository.hotel.RoomRepository;
import com.travixa.repository.restaurant.RestaurantImageRepository;
import com.travixa.repository.restaurant.RestaurantRepository;
import com.travixa.repository.restaurant.MenuItemRepository;
import com.travixa.repository.tour.PackageImageRepository;
import com.travixa.repository.tour.TourPackageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class MediaService {
    private static final int MAX_FILES = 10;
    private static final long MAX_REQUEST_SIZE = 20L * 1024 * 1024;
    private final FileStorageService storage;
    private final HotelRepository hotels;
    private final HotelImageRepository hotelImages;
    private final RoomTypeRepository roomTypes;
    private final RoomTypeImageRepository roomTypeImages;
    private final RoomRepository rooms;
    private final RoomImageRepository roomImages;
    private final RestaurantRepository restaurants;
    private final RestaurantImageRepository restaurantImages;
    private final MenuItemRepository menuItems;
    private final TourPackageRepository packages;
    private final PackageImageRepository packageImages;

    public MediaService(FileStorageService storage, HotelRepository hotels, HotelImageRepository hotelImages,
                        RoomTypeRepository roomTypes, RoomTypeImageRepository roomTypeImages,
                        RoomRepository rooms, RoomImageRepository roomImages,
                        RestaurantRepository restaurants, RestaurantImageRepository restaurantImages,
                        MenuItemRepository menuItems,
                        TourPackageRepository packages, PackageImageRepository packageImages) {
        this.storage = storage;
        this.hotels = hotels;
        this.hotelImages = hotelImages;
        this.roomTypes = roomTypes;
        this.roomTypeImages = roomTypeImages;
        this.rooms = rooms;
        this.roomImages = roomImages;
        this.restaurants = restaurants;
        this.restaurantImages = restaurantImages;
        this.menuItems = menuItems;
        this.packages = packages;
        this.packageImages = packageImages;
    }

    @Transactional
    public List<MediaUploadResponse> upload(String type, Long listingId, List<MultipartFile> files,
                                            List<String> altTexts, Integer coverIndex) {
        String kind = normalizeType(type);
        if (listingId == null || listingId < 1 || files == null || files.isEmpty() || files.size() > MAX_FILES) {
            throw new InvalidRequestException("Provide a listing ID and between 1 and 10 images");
        }
        if (kind.equals("MENU_ITEM") && files.size() != 1) {
            throw new InvalidRequestException("Upload one image per menu item");
        }
        long total = files.stream().filter(f -> f != null).mapToLong(MultipartFile::getSize).sum();
        if (total > MAX_REQUEST_SIZE) throw new InvalidRequestException("Combined image size must not exceed 20 MB");
        if (coverIndex != null && (coverIndex < 0 || coverIndex >= files.size())) {
            throw new InvalidRequestException("coverIndex must refer to one of the uploaded images");
        }
        requireListing(kind, listingId);
        if (kind.equals("MENU_ITEM") && menuItems.findById(listingId).orElseThrow().getImageStorageKey() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Delete the current menu item photo before uploading a replacement");
        }

        List<String> keys = new ArrayList<>();
        try {
            List<MediaUploadResponse> result = new ArrayList<>();
            int startOrder = existingCount(kind, listingId);
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                String key = storage.store(file);
                keys.add(key);
                boolean cover = coverIndex != null ? coverIndex == i : startOrder == 0 && i == 0;
                int order = cover && kind.equals("TOUR_PACKAGE") ? 0 : startOrder + i;
                String alt = altTexts != null && i < altTexts.size() ? trimToNull(altTexts.get(i)) : null;
                result.add(saveMetadata(kind, listingId, key, alt, order, cover));
            }
            if (coverIndex != null || startOrder == 0) selectCover(kind, listingId, result, coverIndex == null ? 0 : coverIndex);
            return result;
        } catch (RuntimeException ex) {
            keys.forEach(storage::delete);
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public StoredImage loadPublished(String key) {
        HotelImage hotelImage = hotelImages.findByStorageKey(key).orElse(null);
        if (hotelImage != null && hotelImage.getHotel().getListingStatus() == ListingStatus.PUBLISHED) return load(key);
        RoomTypeImage roomImage = roomTypeImages.findByStorageKey(key).orElse(null);
        if (roomImage != null && roomImage.getRoomType().getListingStatus() == ListingStatus.PUBLISHED
                && roomImage.getRoomType().getHotel().getListingStatus() == ListingStatus.PUBLISHED) return load(key);
        RoomImage physicalRoomImage = roomImages.findByStorageKey(key).orElse(null);
        if (physicalRoomImage != null && physicalRoomImage.getRoom().getRoomType().getListingStatus() == ListingStatus.PUBLISHED
                && physicalRoomImage.getRoom().getHotel().getListingStatus() == ListingStatus.PUBLISHED) return load(key);
        RestaurantImage restaurantImage = restaurantImages.findByStorageKey(key).orElse(null);
        if (restaurantImage != null && restaurantImage.getRestaurant().getListingStatus() == ListingStatus.PUBLISHED) return load(key);
        MenuItem menuItem = menuItems.findByImageStorageKey(key).orElse(null);
        if (menuItem != null && menuItem.getStatus() == com.travixa.enums.MenuItemStatus.AVAILABLE
                && menuItem.getCategory().isActive()
                && menuItem.getCategory().getRestaurant().getListingStatus() == ListingStatus.PUBLISHED) return load(key);
        PackageImage packageImage = packageImages.findByStorageKey(key).orElse(null);
        if (packageImage != null && packageImage.getTourPackage().getListingStatus() == ListingStatus.PUBLISHED) return load(key);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
    }

    @Transactional(readOnly = true)
    public StoredImage loadAnyListingImage(String key) {
        boolean exists = hotelImages.findByStorageKey(key).isPresent()
                || restaurantImages.findByStorageKey(key).isPresent()
                || roomTypeImages.findByStorageKey(key).isPresent()
                || roomImages.findByStorageKey(key).isPresent()
                || menuItems.findByImageStorageKey(key).isPresent()
                || packageImages.findByStorageKey(key).isPresent();
        if (!exists) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
        return load(key);
    }

    @Transactional
    public void delete(String type, Long imageId) {
        String kind = normalizeType(type);
        String key = switch (kind) {
            case "HOTEL" -> hotelImages.findById(imageId).map(image -> image.getStorageKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
            case "ROOM_TYPE" -> roomTypeImages.findById(imageId).map(RoomTypeImage::getStorageKey)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
            case "ROOM" -> roomImages.findById(imageId).map(RoomImage::getStorageKey)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
            case "RESTAURANT" -> restaurantImages.findById(imageId).map(image -> image.getStorageKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
            case "MENU_ITEM" -> menuItems.findById(imageId).map(MenuItem::getImageStorageKey)
                    .filter(java.util.Objects::nonNull)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
            case "TOUR_PACKAGE" -> packageImages.findById(imageId).map(image -> image.getStorageKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
            default -> throw new InvalidRequestException("Unsupported listing type");
        };
        switch (kind) {
            case "HOTEL" -> hotelImages.deleteById(imageId);
            case "ROOM_TYPE" -> roomTypeImages.deleteById(imageId);
            case "ROOM" -> roomImages.deleteById(imageId);
            case "RESTAURANT" -> restaurantImages.deleteById(imageId);
            case "MENU_ITEM" -> menuItems.findById(imageId).ifPresent(item -> {
                item.setImageStorageKey(null);
                menuItems.save(item);
            });
            case "TOUR_PACKAGE" -> packageImages.deleteById(imageId);
            default -> { }
        }
        deleteAfterCommit(key);
    }

    @Transactional
    public void deleteRoomTypeImages(Long roomTypeId) {
        List<RoomTypeImage> images = roomTypeImages.findAllByRoomType_IdOrderByDisplayOrderAscIdAsc(roomTypeId);
        roomTypeImages.deleteAll(images);
        images.forEach(image -> deleteAfterCommit(image.getStorageKey()));
    }

    @Transactional
    public void deleteRoomImages(Long roomId) {
        List<RoomImage> images = roomImages.findAllByRoom_IdOrderByDisplayOrderAscIdAsc(roomId);
        roomImages.deleteAll(images);
        images.forEach(image -> deleteAfterCommit(image.getStorageKey()));
    }

    private StoredImage load(String key) { return new StoredImage(storage.load(key), storage.contentType(key)); }

    private void requireListing(String kind, Long id) {
        boolean exists = switch (kind) {
            case "HOTEL" -> hotels.existsById(id);
            case "ROOM_TYPE" -> roomTypes.existsById(id);
            case "ROOM" -> rooms.existsById(id);
            case "RESTAURANT" -> restaurants.existsById(id);
            case "MENU_ITEM" -> menuItems.existsById(id);
            case "TOUR_PACKAGE" -> packages.existsById(id);
            default -> false;
        };
        if (!exists) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found");
    }

    private int existingCount(String kind, Long id) {
        return switch (kind) {
            case "HOTEL" -> hotelImages.findAllByHotel_IdOrderByDisplayOrderAscIdAsc(id).size();
            case "ROOM_TYPE" -> roomTypeImages.findAllByRoomType_IdOrderByDisplayOrderAscIdAsc(id).size();
            case "ROOM" -> roomImages.findAllByRoom_IdOrderByDisplayOrderAscIdAsc(id).size();
            case "RESTAURANT" -> restaurantImages.findAllByRestaurant_IdOrderByDisplayOrderAscIdAsc(id).size();
            case "MENU_ITEM" -> menuItems.findById(id).map(item -> item.getImageStorageKey() == null ? 0 : 1).orElse(0);
            case "TOUR_PACKAGE" -> packageImages.findAllByTourPackage_IdOrderByDisplayOrderAscIdAsc(id).size();
            default -> 0;
        };
    }

    private MediaUploadResponse saveMetadata(String kind, Long id, String key, String alt, int order, boolean cover) {
        return switch (kind) {
            case "HOTEL" -> {
                HotelImage item = new HotelImage();
                item.setHotel(hotels.findById(id).orElseThrow());
                item.setStorageKey(key); item.setAltText(alt); item.setDisplayOrder(order); item.setCoverImage(cover);
                item = hotelImages.save(item);
                yield response(item.getId(), kind, id, key, alt, order, cover);
            }
            case "ROOM_TYPE" -> {
                RoomTypeImage item = new RoomTypeImage();
                item.setRoomType(roomTypes.findById(id).orElseThrow());
                item.setStorageKey(key); item.setAltText(alt); item.setDisplayOrder(order); item.setCoverImage(cover);
                item = roomTypeImages.save(item);
                yield response(item.getId(), kind, id, key, alt, order, cover);
            }
            case "ROOM" -> {
                RoomImage item = new RoomImage();
                item.setRoom(rooms.findById(id).orElseThrow());
                item.setStorageKey(key); item.setAltText(alt); item.setDisplayOrder(order); item.setCoverImage(cover);
                item = roomImages.save(item);
                yield response(item.getId(), kind, id, key, alt, order, cover);
            }
            case "RESTAURANT" -> {
                RestaurantImage item = new RestaurantImage();
                item.setRestaurant(restaurants.findById(id).orElseThrow());
                item.setStorageKey(key); item.setAltText(alt); item.setDisplayOrder(order); item.setCoverImage(cover);
                item = restaurantImages.save(item);
                yield response(item.getId(), kind, id, key, alt, order, cover);
            }
            case "MENU_ITEM" -> {
                MenuItem item = menuItems.findById(id).orElseThrow();
                item.setImageStorageKey(key);
                menuItems.save(item);
                yield response(item.getId(), kind, id, key, alt, 0, true);
            }
            case "TOUR_PACKAGE" -> {
                PackageImage item = new PackageImage();
                item.setTourPackage(packages.findById(id).orElseThrow());
                item.setStorageKey(key); item.setAltText(alt); item.setDisplayOrder(order);
                item = packageImages.save(item);
                yield response(item.getId(), kind, id, key, alt, order, cover);
            }
            default -> throw new InvalidRequestException("Unsupported listing type");
        };
    }

    private void selectCover(String kind, Long id, List<MediaUploadResponse> added, int selectedIndex) {
        String selectedKey = added.get(selectedIndex).storageKey();
        switch (kind) {
            case "HOTEL" -> hotelImages.findAllByHotel_IdOrderByDisplayOrderAscIdAsc(id).forEach(image -> {
                image.setCoverImage(image.getStorageKey().equals(selectedKey)); hotelImages.save(image);
            });
            case "ROOM_TYPE" -> roomTypeImages.findAllByRoomType_IdOrderByDisplayOrderAscIdAsc(id).forEach(image -> {
                image.setCoverImage(image.getStorageKey().equals(selectedKey)); roomTypeImages.save(image);
            });
            case "ROOM" -> roomImages.findAllByRoom_IdOrderByDisplayOrderAscIdAsc(id).forEach(image -> {
                image.setCoverImage(image.getStorageKey().equals(selectedKey)); roomImages.save(image);
            });
            case "RESTAURANT" -> restaurantImages.findAllByRestaurant_IdOrderByDisplayOrderAscIdAsc(id).forEach(image -> {
                image.setCoverImage(image.getStorageKey().equals(selectedKey)); restaurantImages.save(image);
            });
            case "TOUR_PACKAGE" -> packageImages.findAllByTourPackage_IdOrderByDisplayOrderAscIdAsc(id).forEach(image -> {
                image.setDisplayOrder(image.getStorageKey().equals(selectedKey) ? 0 : image.getDisplayOrder() + 1); packageImages.save(image);
            });
            default -> { }
        }
    }

    private MediaUploadResponse response(Long id, String kind, Long listingId, String key, String alt, int order, boolean cover) {
        return new MediaUploadResponse(id, kind, listingId, key, "/api/media/" + key, alt, order, cover);
    }

    private String normalizeType(String type) {
        String normalized = type == null ? "" : type.trim().replace('-', '_').toUpperCase();
        if (!List.of("HOTEL", "ROOM_TYPE", "ROOM", "RESTAURANT", "MENU_ITEM", "TOUR_PACKAGE").contains(normalized)) {
            throw new InvalidRequestException("Unsupported listing type");
        }
        return normalized;
    }

    @Transactional(readOnly = true)
    public List<MediaUploadResponse> list(String type, Long listingId) {
        String kind = normalizeType(type);
        if (listingId == null || listingId < 1) throw new InvalidRequestException("A valid listing ID is required");
        requireListing(kind, listingId);
        return switch (kind) {
            case "HOTEL" -> hotelImages.findAllByHotel_IdOrderByDisplayOrderAscIdAsc(listingId).stream()
                    .map(i -> response(i.getId(), kind, listingId, i.getStorageKey(), i.getAltText(), i.getDisplayOrder(), i.isCoverImage())).toList();
            case "ROOM_TYPE" -> roomTypeImages.findAllByRoomType_IdOrderByDisplayOrderAscIdAsc(listingId).stream()
                    .map(i -> response(i.getId(), kind, listingId, i.getStorageKey(), i.getAltText(), i.getDisplayOrder(), i.isCoverImage())).toList();
            case "ROOM" -> roomImages.findAllByRoom_IdOrderByDisplayOrderAscIdAsc(listingId).stream()
                    .map(i -> response(i.getId(), kind, listingId, i.getStorageKey(), i.getAltText(), i.getDisplayOrder(), i.isCoverImage())).toList();
            case "RESTAURANT" -> restaurantImages.findAllByRestaurant_IdOrderByDisplayOrderAscIdAsc(listingId).stream()
                    .map(i -> response(i.getId(), kind, listingId, i.getStorageKey(), i.getAltText(), i.getDisplayOrder(), i.isCoverImage())).toList();
            case "TOUR_PACKAGE" -> packageImages.findAllByTourPackage_IdOrderByDisplayOrderAscIdAsc(listingId).stream()
                    .map(i -> response(i.getId(), kind, listingId, i.getStorageKey(), i.getAltText(), i.getDisplayOrder(), false)).toList();
            case "MENU_ITEM" -> menuItems.findById(listingId).filter(i -> i.getImageStorageKey() != null)
                    .map(i -> List.of(response(i.getId(), kind, listingId, i.getImageStorageKey(), null, 0, true))).orElseGet(List::of);
            default -> List.of();
        };
    }

    private String trimToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }

    private void deleteAfterCommit(String key) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            storage.delete(key);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { storage.delete(key); }
        });
    }

    public record StoredImage(byte[] bytes, String contentType) { }
}
