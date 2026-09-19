package com.keystone.service;
import com.keystone.domain.*; import com.keystone.dto.Dtos; import com.keystone.repository.*; import org.springframework.stereotype.Service; import java.util.*;
@Service public class CustomerService{
 private final CustomerRepository customers; private final SiteRepository sites;
 public CustomerService(CustomerRepository c,SiteRepository s){customers=c;sites=s;}
 public List<Dtos.CustomerResponse> all(){return customers.findAll().stream().map(c->new Dtos.CustomerResponse(c.getId(),c.getName(),c.getEmail(),c.getPhone())).toList();}
 public Dtos.CustomerResponse create(Dtos.CustomerRequest r){Customer c=new Customer();c.setName(r.name());c.setEmail(r.email());c.setPhone(r.phone());customers.save(c);return new Dtos.CustomerResponse(c.getId(),c.getName(),c.getEmail(),c.getPhone());}
 public List<Dtos.SiteResponse> sites(Long customerId){return sites.findByCustomerId(customerId).stream().map(s->new Dtos.SiteResponse(s.getId(),s.getCustomer().getId(),s.getName(),s.getAddress(),s.getCity(),s.getContactName(),s.getContactPhone())).toList();}
 public Dtos.SiteResponse createSite(Long customerId,Dtos.SiteRequest r){Customer c=customers.findById(customerId).orElseThrow(()->new RuntimeException("Customer not found"));Site s=new Site();s.setCustomer(c);s.setName(r.name());s.setAddress(r.address());s.setCity(r.city());s.setContactName(r.contactName());s.setContactPhone(r.contactPhone());sites.save(s);return new Dtos.SiteResponse(s.getId(),c.getId(),s.getName(),s.getAddress(),s.getCity(),s.getContactName(),s.getContactPhone());}
}
