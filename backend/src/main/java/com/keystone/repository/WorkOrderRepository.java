package com.keystone.repository;
import com.keystone.domain.*; import org.springframework.data.jpa.repository.*; import java.util.*;
public interface WorkOrderRepository extends JpaRepository<WorkOrder,Long>{
 List<WorkOrder> findByTechnicianId(Long id);
 List<WorkOrder> findByCustomerId(Long id);
 List<WorkOrder> findByStatus(WorkOrderStatus s);
 long countByStatus(WorkOrderStatus s);
 long countBySlaDueAtBeforeAndStatusNotIn(java.time.LocalDateTime t, Collection<WorkOrderStatus> s);
}
