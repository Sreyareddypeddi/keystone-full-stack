package com.keystone.controller;
import com.keystone.dto.Dtos; import com.keystone.service.AuthService; import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth") public class AuthController{private final AuthService s;public AuthController(AuthService s){this.s=s;}@PostMapping("/login")public Dtos.LoginResponse login(@Valid @RequestBody Dtos.LoginRequest r){return s.login(r);}}
