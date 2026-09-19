package com.keystone.repository;
import com.keystone.domain.WorkOrderStatusHistory; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface HistoryRepository extends JpaRepository<WorkOrderStatusHistory,Long>{List<WorkOrderStatusHistory> findByWorkOrderIdOrderByChangedAtAsc(Long id);}
