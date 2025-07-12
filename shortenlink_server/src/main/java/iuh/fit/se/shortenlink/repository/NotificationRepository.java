package iuh.fit.se.shortenlink.repository;

import iuh.fit.se.shortenlink.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}

