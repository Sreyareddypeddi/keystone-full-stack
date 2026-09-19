package com.keystone.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="work_order_status_history")
public class WorkOrderStatusHistory {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="work_order_id",nullable=false) private WorkOrder workOrder;
    @Enumerated(EnumType.STRING) @Column(name="from_status") private WorkOrderStatus fromStatus;
    @Enumerated(EnumType.STRING) @Column(name="to_status",nullable=false) private WorkOrderStatus toStatus;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="changed_by",nullable=false) private AppUser changedBy;
    @Column(name="changed_at",nullable=false) private LocalDateTime changedAt=LocalDateTime.now();
    private String note;
    public WorkOrderStatusHistory(){}
    public WorkOrderStatusHistory(WorkOrder w,WorkOrderStatus from,WorkOrderStatus to,AppUser u,String note){this.workOrder=w;this.fromStatus=from;this.toStatus=to;this.changedBy=u;this.note=note;}
    public WorkOrderStatus getFromStatus(){return fromStatus;} public WorkOrderStatus getToStatus(){return toStatus;}
    public LocalDateTime getChangedAt(){return changedAt;} public String getNote(){return note;} public AppUser getChangedBy(){return changedBy;}
}
