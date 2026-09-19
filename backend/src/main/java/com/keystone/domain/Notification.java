package com.keystone.domain;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="notifications")
public class Notification {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false) private AppUser user;
 @Column(nullable=false) private String message; @Column(name="read_flag",nullable=false) private boolean readFlag;
 @Column(name="created_at",nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 public Notification(){} public Notification(AppUser u,String m){user=u;message=m;}
 public String getMessage(){return message;} public boolean isReadFlag(){return readFlag;}
}
