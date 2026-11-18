package com.travixa.repository.transport;

import com.travixa.entity.transport.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
    boolean existsByUser_UserName(String userName);
}
