package com.travixa.repository.tour;

import com.travixa.entity.tour_guide.CustomTripRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomTripRequestRepository extends JpaRepository<CustomTripRequest, Long> {

    java.util.List<CustomTripRequest> findAllByUser_UserNameOrderByCreatedAtDesc(String userName);

    java.util.Optional<CustomTripRequest> findByIdAndUser_UserName(Long id, String userName);
}
