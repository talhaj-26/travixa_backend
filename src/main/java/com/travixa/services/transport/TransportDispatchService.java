package com.travixa.services.transport;

import com.travixa.entity.transport.VehicleAssignment;
import com.travixa.repository.transport.VehicleAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TransportDispatchService {

    private final VehicleAssignmentRepository vehicleAssignmentRepository;

    public TransportDispatchService(VehicleAssignmentRepository vehicleAssignmentRepository) {
        this.vehicleAssignmentRepository = vehicleAssignmentRepository;
    }

    public List<VehicleAssignment> findAll() {
        return vehicleAssignmentRepository.findAll();
    }

    public Optional<VehicleAssignment> findById(Long id) {
        return vehicleAssignmentRepository.findById(id);
    }

    @Transactional
    public VehicleAssignment save(VehicleAssignment vehicleAssignment) {
        return vehicleAssignmentRepository.save(vehicleAssignment);
    }

    @Transactional
    public void delete(VehicleAssignment assignment) {
        vehicleAssignmentRepository.delete(assignment);
    }
}
