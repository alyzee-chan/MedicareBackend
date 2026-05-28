package com.medicare.backend.controller;

import com.medicare.backend.dto.AppointmentDto;
import com.medicare.backend.dto.AppointmentRequest;
import com.medicare.backend.dto.AssistantResponse;
import com.medicare.backend.dto.DashboardResponse;
import com.medicare.backend.dto.MedicalRecordDto;
import com.medicare.backend.dto.MedicineDto;
import com.medicare.backend.dto.OrderDto;
import com.medicare.backend.dto.OrderRequest;
import com.medicare.backend.dto.PharmacyDto;
import com.medicare.backend.dto.ProfileDto;
import com.medicare.backend.dto.SosAlertRequest;
import com.medicare.backend.dto.SosAlertResponse;
import com.medicare.backend.service.MedicareService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final MedicareService service;

    public ApiController(MedicareService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public String health() {
        return "MediCare+ backend is running";
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return service.getDashboard();
    }

    @GetMapping("/appointments")
    public List<AppointmentDto> appointments() {
        return service.getAppointments();
    }

    @PostMapping("/appointments")
    public AppointmentDto createAppointment(@Valid @RequestBody AppointmentRequest request) {
        return service.createAppointment(request);
    }

    @GetMapping("/medicines/search")
    public List<MedicineDto> searchMedicines(@RequestParam(required = false, defaultValue = "") String query) {
        return service.searchMedicines(query);
    }

    @GetMapping("/pharmacies")
    public List<PharmacyDto> pharmacies() {
        return service.getPharmacies();
    }

    @GetMapping("/profiles")
    public List<ProfileDto> profiles() {
        return service.getProfiles();
    }

    @GetMapping("/medical-record")
    public MedicalRecordDto medicalRecord() {
        return service.getMedicalRecord();
    }

    @GetMapping("/orders")
    public List<OrderDto> orders() {
        return service.getOrders();
    }

    @PostMapping("/orders")
    public OrderDto createOrder(@Valid @RequestBody OrderRequest request) {
        return service.createOrder(request);
    }

    @GetMapping("/assistant/triage")
    public AssistantResponse triage(@RequestParam(required = false, defaultValue = "") String input) {
        return service.triage(input);
    }

    @PostMapping("/sos/alerts")
    public SosAlertResponse sos(@Valid @RequestBody SosAlertRequest request) {
        return service.raiseSos(request);
    }
}
