package com.travixa.services.tour;

import com.travixa.entity.tour_guide.CustomTripRequest;
import com.travixa.dto.tour.CustomTripRequestDto;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.enums.CustomTripRequestStatus;
import com.travixa.repository.hotel.DestinationRepository;
import com.travixa.repository.identity.UserRepository;
import com.travixa.repository.tour.CustomTripRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CustomTripService {

    private final CustomTripRequestRepository customTripRequestRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;

    public CustomTripService(CustomTripRequestRepository customTripRequestRepository,
            UserRepository userRepository, DestinationRepository destinationRepository) {
        this.customTripRequestRepository = customTripRequestRepository;
        this.userRepository = userRepository;
        this.destinationRepository = destinationRepository;
    }

    public List<CustomTripRequest> findAll() {
        return customTripRequestRepository.findAll();
    }

    public Optional<CustomTripRequest> findById(Long id) {
        return customTripRequestRepository.findById(id);
    }

    public List<CustomTripRequest> findMine(String userName) {
        return customTripRequestRepository.findAllByUser_UserNameOrderByCreatedAtDesc(userName);
    }

    public Optional<CustomTripRequest> findMineById(Long id, String userName) {
        return customTripRequestRepository.findByIdAndUser_UserName(id, userName);
    }

    @Transactional
    public CustomTripRequest create(CustomTripRequestDto request, String userName) {
        if (!request.endDate().isAfter(request.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }
        Destination destination = destinationRepository.findByIdAndPublishedTrue(request.destinationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Published destination not found"));
        var user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
        CustomTripRequest trip = new CustomTripRequest();
        trip.setUser(user);
        trip.setDestination(destination);
        trip.setTravelerCount(request.travelerCount());
        trip.setStartDate(request.startDate());
        trip.setEndDate(request.endDate());
        trip.setTargetBudget(request.targetBudget());
        trip.setCurrency(request.currency());
        trip.setRequirements(request.requirements());
        trip.setStatus(CustomTripRequestStatus.SUBMITTED);
        return customTripRequestRepository.save(trip);
    }

    @Transactional
    public CustomTripRequest save(CustomTripRequest customTripRequest) {
        return customTripRequestRepository.save(customTripRequest);
    }
}
