package com.keystone.domain;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.LocalDateTime;
@Entity @Table(name="part_usage")
public class PartUsage {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="work_order_id",nullable=false) private WorkOrder workOrder;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="part_id",nullable=false) private Part part;
 @Column(nullable=false) private int quantity; @Column(name="unit_cost",nullable=false) private BigDecimal unitCost;
 @Column(name="created_at",nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 public PartUsage(){} public PartUsage(WorkOrder w,Part p,int q){workOrder=w;part=p;quantity=q;unitCost=p.getUnitCost();}
 public int getQuantity(){return quantity;} public Part getPart(){return part;} public BigDecimal getUnitCost(){return unitCost;}
}
