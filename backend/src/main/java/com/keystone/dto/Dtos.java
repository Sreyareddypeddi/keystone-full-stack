package com.keystone.dto;

import com.keystone.domain.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public final class Dtos {

    private Dtos() {
    }

    // =========================================================
    // LOGIN
    // =========================================================

    public record LoginRequest(
            @Email
            @NotBlank
            String email,

            @NotBlank
            String password
    ) {
    }

    public record LoginResponse(
            String token,
            String role,
            String name,
            Long customerId
    ) {
    }

    // =========================================================
    // USER
    // =========================================================

    public record UserResponse(
            Long id,
            String name,
            String email,
            String role
    ) {
    }

    // =========================================================
    // CUSTOMER
    // =========================================================

    public record CustomerRequest(
            @NotBlank
            @Size(max = 150)
            String name,

            @Email
            String email,

            @Size(max = 40)
            String phone
    ) {
    }

    public record CustomerResponse(
            Long id,
            String name,
            String email,
            String phone
    ) {
    }

    // =========================================================
    // SITE
    // =========================================================

    public record SiteRequest(
            @NotBlank
            String name,

            @NotBlank
            String address,

            @NotBlank
            String city,

            String contactName,

            String contactPhone
    ) {
    }

    public record SiteResponse(
            Long id,
            Long customerId,
            String name,
            String address,
            String city,
            String contactName,
            String contactPhone
    ) {
    }

    // =========================================================
    // WORK ORDER REQUEST
    // =========================================================

    public record WorkOrderRequest(
            @NotBlank
            @Size(max = 200)
            String title,

            @Size(max = 2000)
            String description,

            @NotNull
            Priority priority,

            @NotNull
            Long customerId,

            @NotNull
            Long siteId
    ) {
    }

    // =========================================================
    // ASSIGN TECHNICIAN
    // =========================================================

    /*
     * Existing DTO name.
     * Use this if WorkOrderController.java contains:
     *
     * Dtos.AssignRequest
     */
    public record AssignRequest(
            @NotNull
            Long technicianId
    ) {
    }

    /*
     * Compatibility DTO.
     *
     * Use this if WorkOrderController.java currently contains:
     *
     * Dtos.AssignTechnicianRequest
     *
     * This prevents the compilation error:
     * "cannot find symbol: class AssignTechnicianRequest"
     */
    public record AssignTechnicianRequest(
            @NotNull
            Long technicianId
    ) {
    }

    // =========================================================
    // WORK ORDER STATUS
    // =========================================================

    public record StatusRequest(
            @NotNull
            WorkOrderStatus status,

            @Size(max = 2000)
            String note
    ) {
    }

    // =========================================================
    // PARTS
    // =========================================================

    public record PartRequest(
            @NotNull
            Long partId,

            @Min(1)
            int quantity
    ) {
    }

    // =========================================================
    // TIME
    // =========================================================

    public record TimeRequest(
            @Min(1)
            int minutes,

            @Size(max = 1000)
            String note
    ) {
    }

    // =========================================================
    // WORK ORDER HISTORY
    // =========================================================

    public record HistoryResponse(
            String from,
            String to,
            String by,
            LocalDateTime at,
            String note
    ) {
    }

    // =========================================================
    // WORK ORDER RESPONSE
    // =========================================================

    public record WorkOrderResponse(
            Long id,
            String code,
            String title,
            String description,
            String priority,
            String status,
            Long customerId,
            String customerName,
            Long siteId,
            String siteName,
            Long technicianId,
            String technicianName,
            LocalDateTime slaDueAt,
            boolean overdue,
            List<HistoryResponse> history
    ) {
    }

    // =========================================================
    // DASHBOARD SUMMARY
    // =========================================================

    public record Summary(
            long newCount,
            long assigned,
            long inProgress,
            long onHold,
            long completed,
            long closed,
            long cancelled,
            long overdue,
            double slaCompliance
    ) {
    }

    // =========================================================
    // ERROR RESPONSE
    // =========================================================

    public record ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String message,
            java.util.Map<String, String> fields
    ) {
    }
}