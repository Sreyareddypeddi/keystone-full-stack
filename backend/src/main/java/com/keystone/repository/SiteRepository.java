package com.keystone.repository;
import com.keystone.domain.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SiteRepository extends JpaRepository<Site,Long>{List<Site> findByCustomerId(Long customerId);}
