package com.keystone.repository;
import com.keystone.domain.Notification; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface NotificationRepository extends JpaRepository<Notification,Long>{List<Notification> findByUserIdOrderByCreatedAtDesc(Long id);}
