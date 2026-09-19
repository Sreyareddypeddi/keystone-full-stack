package com.keystone.domain;

import jakarta.persistence.*;

@Entity @Table(name="users")
public class AppUser {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id") private Customer customer;
    @Column(nullable=false) private String name;
    @Column(nullable=false,unique=true) private String email;
    @Column(name="password_hash",nullable=false) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role;
    @Column(nullable=false) private boolean enabled=true;
    public AppUser(){}
    public Long getId(){return id;} public Customer getCustomer(){return customer;} public String getName(){return name;}
    public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public Role getRole(){return role;}
    public boolean isEnabled(){return enabled;}
}
