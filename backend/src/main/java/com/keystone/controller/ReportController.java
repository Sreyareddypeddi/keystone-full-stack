package com.keystone.controller;
import com.keystone.dto.Dtos; import com.keystone.service.ReportService; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/reports") public class ReportController{private final ReportService s;public ReportController(ReportService s){this.s=s;}@GetMapping("/summary")@PreAuthorize("hasRole('MANAGER')")public Dtos.Summary summary(){return s.summary();}}
