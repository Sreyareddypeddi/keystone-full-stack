package com.keystone.domain;

import jakarta.persistence.*;

@Entity @Table(name="sites")
public class Site {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id",nullable=false) private Customer customer;
    @Column(nullable=false) private String name;
    @Column(nullable=false) private String address;
    @Column(nullable=false) private String city;
    private String contactName, contactPhone;
    public Site() {}
    public Long getId(){return id;} public Customer getCustomer(){return customer;} public void setCustomer(Customer v){customer=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getCity(){return city;} public void setCity(String v){city=v;}
    public String getContactName(){return contactName;} public void setContactName(String v){contactName=v;}
    public String getContactPhone(){return contactPhone;} public void setContactPhone(String v){contactPhone=v;}
}
