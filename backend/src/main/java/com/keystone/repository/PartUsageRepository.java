package com.keystone.repository;
import com.keystone.domain.PartUsage; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface PartUsageRepository extends JpaRepository<PartUsage,Long>{List<PartUsage> findByWorkOrderId(Long id);}
