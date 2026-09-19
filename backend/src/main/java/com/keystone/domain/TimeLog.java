package com.keystone.domain;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="time_logs")
public class TimeLog {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="work_order_id",nullable=false) private WorkOrder workOrder;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="technician_id",nullable=false) private AppUser technician;
 @Column(nullable=false) private int minutes; private String note;
 @Column(name="created_at",nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 public TimeLog(){} public TimeLog(WorkOrder w,AppUser u,int m,String n){workOrder=w;technician=u;minutes=m;note=n;}
 public int getMinutes(){return minutes;} public String getNote(){return note;}
}
