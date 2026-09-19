package com.keystone.controller;
import com.keystone.domain.*; import com.keystone.repository.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/technicians") public class TechnicianController{
 private final AppUserRepository users; public TechnicianController(AppUserRepository u){users=u;}
 @GetMapping @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER')") public List<Map<String,Object>> all(){return users.findByRole(Role.TECHNICIAN).stream().map(u->Map.<String,Object>of("id",u.getId(),"name",u.getName(),"email",u.getEmail())).toList();}
}
