package com.keystone.controller;
import com.keystone.domain.Part; import com.keystone.repository.PartRepository; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/parts") public class PartController{
 private final PartRepository r; public PartController(PartRepository r){this.r=r;}
 @GetMapping @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER','TECHNICIAN')") public List<Part> all(){return r.findAll();}
}
