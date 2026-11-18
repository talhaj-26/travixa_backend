package com.travixa.services.booking;

import com.travixa.entity.hotel_stays.RoomAvailability;
import com.travixa.repository.hotel.RoomAvailabilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AvailabilityService {

    private final RoomAvailabilityRepository roomAvailabilityRepository;

    public AvailabilityService(RoomAvailabilityRepository roomAvailabilityRepository) {
        this.roomAvailabilityRepository = roomAvailabilityRepository;
    }

    public List<RoomAvailability> findAllAvailability() {
        return roomAvailabilityRepository.findAll();
    }

    @Transactional
    public RoomAvailability save(RoomAvailability roomAvailability) {
        return roomAvailabilityRepository.save(roomAvailability);
    }
}
