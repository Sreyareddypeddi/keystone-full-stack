package com.keystone.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="parts")
public class Part {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true) private String sku;
    @Column(nullable=false) private String name;
    @Column(name="unit_cost",nullable=false) private BigDecimal unitCost;
    @Column(name="stock_quantity",nullable=false) private int stockQuantity;
    public Part(){}
    public Long getId(){return id;} public String getSku(){return sku;} public String getName(){return name;}
    public BigDecimal getUnitCost(){return unitCost;} public int getStockQuantity(){return stockQuantity;}
    public void setStockQuantity(int v){stockQuantity=v;}
}
