package com.keystone.controller;
import com.keystone.domain.*; import com.keystone.repository.*; import org.springframework.web.bind.annotation.*; import org.springframework.security.core.Authentication; import java.util.*;
@RestController @RequestMapping("/api/notifications") public class NotificationController{
 private final NotificationRepository n; private final AppUserRepository u; public NotificationController(NotificationRepository n,AppUserRepository u){this.n=n;this.u=u;}
 @GetMapping public List<Notification> all(Authentication a){return n.findByUserIdOrderByCreatedAtDesc(u.findByEmail(a.getName()).orElseThrow().getId());}
}
