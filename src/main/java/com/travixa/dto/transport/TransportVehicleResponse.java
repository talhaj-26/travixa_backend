package com.travixa.dto.transport;

import com.travixa.enums.VehicleStatus;
import com.travixa.enums.VehicleType;

public record TransportVehicleResponse(Long id, Long organizationId, String organizationName,
                                        VehicleType vehicleType, String registrationNumber,
                                        String make, String modelName, int seatCapacity,
                                        VehicleStatus status) { }
