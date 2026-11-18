package com.travixa.repository.transport;

import com.travixa.entity.transport.VehicleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleAssignmentRepository extends JpaRepository<VehicleAssignment, Long> {
}
