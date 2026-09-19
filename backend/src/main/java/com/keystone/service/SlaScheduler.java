package com.keystone.service;
import com.keystone.domain.*; import com.keystone.repository.*; import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Component; import java.time.*; import java.util.*;
@Component public class SlaScheduler{
 private final WorkOrderRepository work; private final AppUserRepository users; private final NotificationRepository notifications;
 public SlaScheduler(WorkOrderRepository w,AppUserRepository u,NotificationRepository n){work=w;users=u;notifications=n;}
 @Scheduled(fixedDelay=60000) public void check(){var open=work.findAll().stream().filter(x->!Set.of(WorkOrderStatus.CLOSED,WorkOrderStatus.CANCELLED).contains(x.getStatus())&&x.getSlaDueAt().isBefore(LocalDateTime.now())).toList();var managers=users.findByRole(Role.MANAGER);for(var w:open)for(var m:managers)notifications.save(new Notification(m,"SLA breach: "+w.getCode()+" - "+w.getTitle()));}
}
