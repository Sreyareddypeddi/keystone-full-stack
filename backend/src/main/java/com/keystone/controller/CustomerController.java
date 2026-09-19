package com.keystone.controller;
import com.keystone.dto.Dtos; import com.keystone.service.CustomerService; import jakarta.validation.Valid; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/customers") public class CustomerController{
 private final CustomerService s; public CustomerController(CustomerService s){this.s=s;}
 @GetMapping @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER')") public List<Dtos.CustomerResponse> all(){return s.all();}
 @PostMapping @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER')") public Dtos.CustomerResponse create(@Valid @RequestBody Dtos.CustomerRequest r){return s.create(r);}
 @GetMapping("/{id}/sites") public List<Dtos.SiteResponse> sites(@PathVariable Long id){return s.sites(id);}
 @PostMapping("/{id}/sites") @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER')") public Dtos.SiteResponse site(@PathVariable Long id,@Valid @RequestBody Dtos.SiteRequest r){return s.createSite(id,r);}
}
