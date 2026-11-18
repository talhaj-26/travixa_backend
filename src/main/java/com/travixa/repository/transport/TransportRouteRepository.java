package com.travixa.repository.transport;

import com.travixa.entity.transport.TransportRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;

public interface TransportRouteRepository extends JpaRepository<TransportRoute, Long> {
    long countByOrganization_IdIn(Collection<Long> organizationIds);
}
