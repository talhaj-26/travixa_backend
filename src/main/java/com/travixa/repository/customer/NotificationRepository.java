package com.travixa.repository.customer;

import com.travixa.entity.customer_engagement.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
