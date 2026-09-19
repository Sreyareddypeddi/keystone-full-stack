package com.keystone.service;

import com.keystone.domain.AppUser;
import com.keystone.domain.Customer;
import com.keystone.domain.Part;
import com.keystone.domain.Priority;
import com.keystone.domain.Role;
import com.keystone.domain.Site;
import com.keystone.domain.TimeLog;
import com.keystone.domain.WorkOrder;
import com.keystone.domain.WorkOrderStatus;
import com.keystone.domain.WorkOrderStatusHistory;
import com.keystone.domain.PartUsage;
import com.keystone.dto.Dtos;
import com.keystone.repository.AppUserRepository;
import com.keystone.repository.CustomerRepository;
import com.keystone.repository.HistoryRepository;
import com.keystone.repository.PartRepository;
import com.keystone.repository.PartUsageRepository;
import com.keystone.repository.SiteRepository;
import com.keystone.repository.TimeLogRepository;
import com.keystone.repository.WorkOrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class WorkOrderService {

    private final WorkOrderRepository repo;
    private final CustomerRepository customers;
    private final SiteRepository sites;
    private final AppUserRepository users;
    private final HistoryRepository history;
    private final PartRepository parts;
    private final PartUsageRepository usage;
    private final TimeLogRepository time;

    public WorkOrderService(
            WorkOrderRepository repo,
            CustomerRepository customers,
            SiteRepository sites,
            AppUserRepository users,
            HistoryRepository history,
            PartRepository parts,
            PartUsageRepository usage,
            TimeLogRepository time
    ) {
        this.repo = repo;
        this.customers = customers;
        this.sites = sites;
        this.users = users;
        this.history = history;
        this.parts = parts;
        this.usage = usage;
        this.time = time;
    }

    private LocalDateTime calculateSla(Priority priority) {
        return LocalDateTime.now().plusHours(
                switch (priority) {
                    case CRITICAL -> 2;
                    case HIGH -> 4;
                    case MEDIUM -> 12;
                    case LOW -> 24;
                }
        );
    }

    private Dtos.WorkOrderResponse toResponse(WorkOrder workOrder) {

        List<Dtos.HistoryResponse> historyResponses =
                history.findByWorkOrderIdOrderByChangedAtAsc(workOrder.getId())
                        .stream()
                        .map(item -> new Dtos.HistoryResponse(
                                item.getFromStatus() == null
                                        ? null
                                        : item.getFromStatus().name(),
                                item.getToStatus().name(),
                                item.getChangedBy().getName(),
                                item.getChangedAt(),
                                item.getNote()
                        ))
                        .toList();

        boolean overdue =
                workOrder.getSlaDueAt().isBefore(LocalDateTime.now())
                        && !Set.of(
                                WorkOrderStatus.CLOSED,
                                WorkOrderStatus.CANCELLED
                        ).contains(workOrder.getStatus());

        return new Dtos.WorkOrderResponse(
                workOrder.getId(),
                workOrder.getCode(),
                workOrder.getTitle(),
                workOrder.getDescription(),
                workOrder.getPriority().name(),
                workOrder.getStatus().name(),
                workOrder.getCustomer().getId(),
                workOrder.getCustomer().getName(),
                workOrder.getSite().getId(),
                workOrder.getSite().getName(),
                workOrder.getTechnician() == null
                        ? null
                        : workOrder.getTechnician().getId(),
                workOrder.getTechnician() == null
                        ? null
                        : workOrder.getTechnician().getName(),
                workOrder.getSlaDueAt(),
                overdue,
                historyResponses
        );
    }

    @Transactional(readOnly = true)
    public List<Dtos.WorkOrderResponse> all(AppUser user) {

        List<WorkOrder> workOrders = switch (user.getRole()) {

            case CUSTOMER ->
                    repo.findByCustomerId(user.getCustomer().getId());

            case TECHNICIAN ->
                    repo.findByTechnicianId(user.getId());

            default ->
                    repo.findAll();
        };

        return workOrders
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Dtos.WorkOrderResponse one(Long id, AppUser user) {

        WorkOrder workOrder = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Work order not found"));

        authorize(workOrder, user);

        return toResponse(workOrder);
    }

    @Transactional
    public Dtos.WorkOrderResponse create(
            Dtos.WorkOrderRequest request,
            AppUser actor
    ) {

        if (actor.getRole() == Role.CUSTOMER) {

            if (actor.getCustomer() == null
                    || !actor.getCustomer()
                    .getId()
                    .equals(request.customerId())) {

                throw new RuntimeException("Forbidden");
            }
        }

        Customer customer = customers.findById(request.customerId())
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        Site site = sites.findById(request.siteId())
                .orElseThrow(() ->
                        new RuntimeException("Site not found"));

        if (!site.getCustomer().getId().equals(customer.getId())) {

            throw new RuntimeException(
                    "Site does not belong to the selected customer");
        }

        WorkOrder workOrder = new WorkOrder();

        workOrder.setCode(
                "WO-" + (10000 + new Random().nextInt(89999))
        );

        workOrder.setTitle(request.title());
        workOrder.setDescription(request.description());
        workOrder.setPriority(request.priority());
        workOrder.setStatus(WorkOrderStatus.NEW);
        workOrder.setCustomer(customer);
        workOrder.setSite(site);
        workOrder.setSlaDueAt(calculateSla(request.priority()));

        repo.save(workOrder);

        history.save(
                new WorkOrderStatusHistory(
                        workOrder,
                        null,
                        WorkOrderStatus.NEW,
                        actor,
                        "Request created"
                )
        );

        return toResponse(workOrder);
    }

    @Transactional
    public Dtos.WorkOrderResponse assign(
            Long id,
            Dtos.AssignRequest request,
            AppUser actor
    ) {

        WorkOrder workOrder = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Work order not found"));

        if (actor.getRole() != Role.MANAGER
                && actor.getRole() != Role.DISPATCHER) {

            throw new RuntimeException("Forbidden");
        }

        if (workOrder.getStatus() != WorkOrderStatus.NEW) {

            throw new IllegalStateException(
                    "Only NEW work orders can be assigned");
        }

        AppUser technician = users.findById(request.technicianId())
                .orElseThrow(() ->
                        new RuntimeException("Technician not found"));

        if (technician.getRole() != Role.TECHNICIAN) {

            throw new RuntimeException(
                    "Selected user is not a technician");
        }

        transition(
                workOrder,
                WorkOrderStatus.ASSIGNED,
                actor,
                "Assigned to " + technician.getName()
        );

        workOrder.setTechnician(technician);

        repo.save(workOrder);

        return toResponse(workOrder);
    }

    @Transactional
    public Dtos.WorkOrderResponse status(
            Long id,
            Dtos.StatusRequest request,
            AppUser actor
    ) {

        WorkOrder workOrder = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Work order not found"));

        authorize(workOrder, actor);

        WorkOrderStatus targetStatus = request.status();

        /*
         * Technician-specific restrictions
         */
        if (actor.getRole() == Role.TECHNICIAN) {

            if (workOrder.getTechnician() == null
                    || !workOrder.getTechnician()
                    .getId()
                    .equals(actor.getId())) {

                throw new RuntimeException("Forbidden");
            }

            /*
             * A technician cannot assign a work order.
             */
            if (targetStatus == WorkOrderStatus.ASSIGNED) {

                throw new RuntimeException(
                        "Technicians cannot assign work orders");
            }

            /*
             * A technician cannot directly complete a job
             * without a completion note and time log.
             */
            if (targetStatus == WorkOrderStatus.COMPLETED) {

                String completionNote = request.note();

                if (completionNote == null
                        || completionNote.trim().isEmpty()) {

                    throw new IllegalStateException(
                            "Work completion note is required before completing the job"
                    );
                }

                long numberOfTimeLogs =
                        time.countByWorkOrderId(workOrder.getId());

                if (numberOfTimeLogs <= 0) {

                    throw new IllegalStateException(
                            "Please log time spent before completing the job"
                    );
                }
            }
        }

        /*
         * Only the manager can close a completed work order.
         */
        if (targetStatus == WorkOrderStatus.CLOSED) {

            if (actor.getRole() != Role.MANAGER) {

                throw new RuntimeException(
                        "Only a manager can close a work order");
            }

            if (workOrder.getStatus() != WorkOrderStatus.COMPLETED) {

                throw new IllegalStateException(
                        "Only COMPLETED work orders can be closed");
            }
        }

        /*
         * Validate the status transition.
         */
        if (!isAllowedTransition(
                workOrder.getStatus(),
                targetStatus
        )) {

            throw new IllegalStateException(
                    "Illegal transition from "
                            + workOrder.getStatus()
                            + " to "
                            + targetStatus
            );
        }

        transition(
                workOrder,
                targetStatus,
                actor,
                request.note()
        );

        repo.save(workOrder);

        return toResponse(workOrder);
    }

    private void transition(
            WorkOrder workOrder,
            WorkOrderStatus newStatus,
            AppUser actor,
            String note
    ) {

        history.save(
                new WorkOrderStatusHistory(
                        workOrder,
                        workOrder.getStatus(),
                        newStatus,
                        actor,
                        note
                )
        );

        workOrder.setStatus(newStatus);
    }

    private boolean isAllowedTransition(
            WorkOrderStatus currentStatus,
            WorkOrderStatus targetStatus
    ) {

        return switch (currentStatus) {

            case NEW ->
                    Set.of(
                            WorkOrderStatus.ASSIGNED,
                            WorkOrderStatus.CANCELLED
                    ).contains(targetStatus);

            case ASSIGNED ->
                    Set.of(
                            WorkOrderStatus.IN_PROGRESS,
                            WorkOrderStatus.CANCELLED
                    ).contains(targetStatus);

            case IN_PROGRESS ->
                    Set.of(
                            WorkOrderStatus.ON_HOLD,
                            WorkOrderStatus.COMPLETED,
                            WorkOrderStatus.CANCELLED
                    ).contains(targetStatus);

            case ON_HOLD ->
                    Set.of(
                            WorkOrderStatus.IN_PROGRESS,
                            WorkOrderStatus.CANCELLED
                    ).contains(targetStatus);

            case COMPLETED ->
                    targetStatus == WorkOrderStatus.CLOSED;

            default ->
                    false;
        };
    }

    @Transactional
    public void addPart(
            Long id,
            Dtos.PartRequest request,
            AppUser actor
    ) {

        WorkOrder workOrder = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Work order not found"));

        validateTechnicianAccess(workOrder, actor);

        if (workOrder.getStatus() != WorkOrderStatus.IN_PROGRESS) {

            throw new IllegalStateException(
                    "Parts can only be added while the job is IN_PROGRESS");
        }

        Part part = parts.findById(request.partId())
                .orElseThrow(() ->
                        new RuntimeException("Part not found"));

        if (part.getStockQuantity() < request.quantity()) {

            throw new IllegalStateException(
                    "Insufficient stock for the selected part");
        }

        part.setStockQuantity(
                part.getStockQuantity() - request.quantity()
        );

        parts.save(part);

        usage.save(
                new PartUsage(
                        workOrder,
                        part,
                        request.quantity()
                )
        );
    }

    @Transactional
    public void addTime(
            Long id,
            Dtos.TimeRequest request,
            AppUser actor
    ) {

        WorkOrder workOrder = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Work order not found"));

        validateTechnicianAccess(workOrder, actor);

        if (workOrder.getStatus() != WorkOrderStatus.IN_PROGRESS) {

            throw new IllegalStateException(
                    "Time can only be logged while the job is IN_PROGRESS");
        }

        time.save(
                new TimeLog(
                        workOrder,
                        actor,
                        request.minutes(),
                        request.note()
                )
        );
    }

    private void validateTechnicianAccess(
            WorkOrder workOrder,
            AppUser actor
    ) {

        if (actor.getRole() != Role.TECHNICIAN
                || workOrder.getTechnician() == null
                || !workOrder.getTechnician()
                .getId()
                .equals(actor.getId())) {

            throw new RuntimeException("Forbidden");
        }
    }

    private void authorize(
            WorkOrder workOrder,
            AppUser user
    ) {

        if (user.getRole() == Role.CUSTOMER) {

            if (user.getCustomer() == null
                    || !workOrder.getCustomer()
                    .getId()
                    .equals(user.getCustomer().getId())) {

                throw new RuntimeException("Forbidden");
            }
        }

        if (user.getRole() == Role.TECHNICIAN) {

            if (workOrder.getTechnician() == null
                    || !workOrder.getTechnician()
                    .getId()
                    .equals(user.getId())) {

                throw new RuntimeException("Forbidden");
            }
        }
    }
}