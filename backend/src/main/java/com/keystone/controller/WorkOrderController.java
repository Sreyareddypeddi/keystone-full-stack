package com.keystone.controller;

import com.keystone.domain.AppUser;
import com.keystone.domain.Role;
import com.keystone.dto.Dtos;
import com.keystone.repository.AppUserRepository;
import com.keystone.service.WorkOrderService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderService service;
    private final AppUserRepository users;

    public WorkOrderController(
            WorkOrderService service,
            AppUserRepository users
    ) {
        this.service = service;
        this.users = users;
    }

    private AppUser currentUser(Authentication authentication) {
        return users.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"
                        )
                );
    }

    // =========================================================
    // GET ALL WORK ORDERS
    // =========================================================

    @GetMapping
    public List<Dtos.WorkOrderResponse> all(
            Authentication authentication
    ) {
        return service.all(
                currentUser(authentication)
        );
    }

    // =========================================================
    // GET ALL TECHNICIANS
    // =========================================================

    @GetMapping("/technicians")
    @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER')")
    public List<Map<String, Object>> technicians() {

        return users.findByRole(Role.TECHNICIAN)
                .stream()
                .map(user -> Map.of(
                        "id", (Object) user.getId(),
                        "name", (Object) user.getName(),
                        "email", (Object) user.getEmail()
                ))
                .toList();
    }

    // =========================================================
    // GET ONE WORK ORDER
    // =========================================================

    @GetMapping("/{id:[0-9]+}")
    public Dtos.WorkOrderResponse one(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return service.one(
                id,
                currentUser(authentication)
        );
    }

    // =========================================================
    // CREATE WORK ORDER
    // =========================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER','CUSTOMER')")
    public Dtos.WorkOrderResponse create(
            @Valid @RequestBody Dtos.WorkOrderRequest request,
            Authentication authentication
    ) {
        return service.create(
                request,
                currentUser(authentication)
        );
    }

    // =========================================================
    // ASSIGN TECHNICIAN
    // =========================================================

    @PostMapping("/{id:[0-9]+}/assign")
@PreAuthorize("hasAnyRole('MANAGER','DISPATCHER')")
public Dtos.WorkOrderResponse assign(
        @PathVariable Long id,
        @Valid @RequestBody Dtos.AssignRequest request,
        Authentication authentication
) {
    return service.assign(
            id,
            request,
            currentUser(authentication)
    );
}

    // =========================================================
    // UPDATE STATUS
    // =========================================================

    @PostMapping("/{id:[0-9]+}/status")
    public Dtos.WorkOrderResponse status(
            @PathVariable Long id,
            @Valid @RequestBody Dtos.StatusRequest request,
            Authentication authentication
    ) {
        return service.status(
                id,
                request,
                currentUser(authentication)
        );
    }

    // =========================================================
    // ADD PART
    // =========================================================

    @PostMapping("/{id:[0-9]+}/parts")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public Map<String, String> parts(
            @PathVariable Long id,
            @Valid @RequestBody Dtos.PartRequest request,
            Authentication authentication
    ) {
        service.addPart(
                id,
                request,
                currentUser(authentication)
        );

        return Map.of(
                "message",
                "Part logged successfully"
        );
    }

    // =========================================================
    // ADD TIME
    // =========================================================

    @PostMapping("/{id:[0-9]+}/time")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public Map<String, String> time(
            @PathVariable Long id,
            @Valid @RequestBody Dtos.TimeRequest request,
            Authentication authentication
    ) {
        service.addTime(
                id,
                request,
                currentUser(authentication)
        );

        return Map.of(
                "message",
                "Time logged successfully"
        );
    }
}