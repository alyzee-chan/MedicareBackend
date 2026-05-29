package com.medicare.backend.controller;

import com.medicare.backend.dto.AppointmentDto;
import com.medicare.backend.dto.AppointmentRequest;
import com.medicare.backend.dto.AssistantResponse;
import com.medicare.backend.dto.DashboardResponse;
import com.medicare.backend.dto.FamilyProfileDto;
import com.medicare.backend.dto.FamilyProfileRequest;
import com.medicare.backend.dto.MedicalRecordDto;
import com.medicare.backend.dto.MedicineDto;
import com.medicare.backend.dto.OrderDto;
import com.medicare.backend.dto.OrderRequest;
import com.medicare.backend.dto.PharmacyDto;
import com.medicare.backend.dto.ProfileDto;
import com.medicare.backend.dto.SosAlertRequest;
import com.medicare.backend.dto.SosAlertResponse;
import com.medicare.backend.dto.UpdateProfileRequest;
import com.medicare.backend.dto.UserDto;
import com.medicare.backend.repository.MedicareRepository;
import com.medicare.backend.service.MedicareService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final MedicareService service;
    private final MedicareRepository repository;

    public ApiController(MedicareService service, MedicareRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @GetMapping("/health")
    public String health() {
        return "MediCare+ backend is running";
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard(Authentication auth) {
        long userId = (long) auth.getPrincipal();
        return service.getDashboard(userId);
    }

    @GetMapping("/appointments")
    public List<AppointmentDto> appointments(Authentication auth) {
        long userId = (long) auth.getPrincipal();
        return service.getAppointments(userId);
    }

    @PostMapping("/appointments")
    public AppointmentDto createAppointment(Authentication auth, @Valid @RequestBody AppointmentRequest request) {
        long userId = (long) auth.getPrincipal();
        return service.createAppointment(userId, request);
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
    public List<OrderDto> orders(Authentication auth) {
        long userId = (long) auth.getPrincipal();
        return service.getOrders(userId);
    }

    @PostMapping("/orders")
    public OrderDto createOrder(Authentication auth, @Valid @RequestBody OrderRequest request) {
        long userId = (long) auth.getPrincipal();
        return service.createOrder(userId, request);
    }

    @GetMapping("/assistant/triage")
    public AssistantResponse triage(@RequestParam(required = false, defaultValue = "") String input) {
        return service.triage(input);
    }

    @PostMapping("/sos/alerts")
    public SosAlertResponse sos(@Valid @RequestBody SosAlertRequest request) {
        return service.raiseSos(request);
    }

    // ═══════════════════════════════════════════
    // ─── USER PROFILE MANAGEMENT ───
    // ═══════════════════════════════════════════

    @PutMapping("/user/profile")
    public ResponseEntity<?> updateProfile(Authentication auth, @RequestBody UpdateProfileRequest request) {
        long userId = (long) auth.getPrincipal();
        repository.updateUser(userId, request.fullName(), request.phone(), request.dateOfBirth(), request.bloodGroup());
        UserDto updated = repository.findUserById(userId);
        return ResponseEntity.ok(updated);
    }

    // ─── Family profiles ───

    @GetMapping("/user/profiles")
    public List<FamilyProfileDto> getFamilyProfiles(Authentication auth) {
        long userId = (long) auth.getPrincipal();
        return repository.findFamilyProfiles(userId);
    }

    @PostMapping("/user/profiles")
    public FamilyProfileDto createFamilyProfile(Authentication auth, @Valid @RequestBody FamilyProfileRequest request) {
        long userId = (long) auth.getPrincipal();
        return repository.createFamilyProfile(userId, request);
    }

    @DeleteMapping("/user/profiles/{profileId}")
    public ResponseEntity<?> deleteFamilyProfile(Authentication auth, @PathVariable long profileId) {
        long userId = (long) auth.getPrincipal();
        repository.deleteFamilyProfile(profileId, userId);
        return ResponseEntity.ok(Map.of("message", "Profil supprimé"));
    }
}
