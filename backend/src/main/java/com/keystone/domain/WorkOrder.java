package com.keystone.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="work_orders")
public class WorkOrder {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true) private String code;
    @Column(nullable=false) private String title;
    private String description;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Priority priority;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private WorkOrderStatus status;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id",nullable=false) private Customer customer;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="site_id",nullable=false) private Site site;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="technician_id") private AppUser technician;
    @Column(name="sla_due_at",nullable=false) private LocalDateTime slaDueAt;
    @Column(name="created_at",nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    @Column(name="updated_at",nullable=false) private LocalDateTime updatedAt=LocalDateTime.now();
    public WorkOrder(){}
    public Long getId(){return id;} public String getCode(){return code;} public void setCode(String v){code=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public Priority getPriority(){return priority;} public void setPriority(Priority v){priority=v;}
    public WorkOrderStatus getStatus(){return status;} public void setStatus(WorkOrderStatus v){status=v;}
    public Customer getCustomer(){return customer;} public void setCustomer(Customer v){customer=v;}
    public Site getSite(){return site;} public void setSite(Site v){site=v;}
    public AppUser getTechnician(){return technician;} public void setTechnician(AppUser v){technician=v;}
    public LocalDateTime getSlaDueAt(){return slaDueAt;} public void setSlaDueAt(LocalDateTime v){slaDueAt=v;}
}
