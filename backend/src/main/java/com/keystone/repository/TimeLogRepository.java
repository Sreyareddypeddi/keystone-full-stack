package com.keystone.repository;

import com.keystone.domain.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {

    long countByWorkOrderId(Long workOrderId);
}