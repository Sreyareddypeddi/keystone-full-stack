package com.keystone.service;
import com.keystone.domain.*; import com.keystone.dto.Dtos; import com.keystone.repository.WorkOrderRepository; import org.springframework.stereotype.Service; import java.time.*; import java.util.*;
@Service public class ReportService{
 private final WorkOrderRepository r; public ReportService(WorkOrderRepository r){this.r=r;}
 public Dtos.Summary summary(){long n=r.countByStatus(WorkOrderStatus.NEW),a=r.countByStatus(WorkOrderStatus.ASSIGNED),i=r.countByStatus(WorkOrderStatus.IN_PROGRESS),h=r.countByStatus(WorkOrderStatus.ON_HOLD),c=r.countByStatus(WorkOrderStatus.COMPLETED),cl=r.countByStatus(WorkOrderStatus.CLOSED),ca=r.countByStatus(WorkOrderStatus.CANCELLED);long overdue=r.countBySlaDueAtBeforeAndStatusNotIn(LocalDateTime.now(),List.of(WorkOrderStatus.CLOSED,WorkOrderStatus.CANCELLED));long total=n+a+i+h+c+cl+ca;double compliance=total==0?100.0:Math.max(0,100.0-(overdue*100.0/total));return new Dtos.Summary(n,a,i,h,c,cl,ca,overdue,Math.round(compliance*10)/10.0);}
}
