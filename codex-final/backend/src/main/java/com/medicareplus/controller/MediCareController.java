package com.medicareplus.controller;

import com.medicareplus.dto.*;
import com.medicareplus.model.*;
import com.medicareplus.service.MediCareService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MediCareController {
  private final MediCareService service;

  public MediCareController(MediCareService service) {
    this.service = service;
  }

  @GetMapping("/health")
  Map<String, String> health() {
    return Map.of("status", "UP", "app", "MediCare+");
  }

  @GetMapping("/doctors")
  List<Doctor> doctors(@RequestParam(required = false) String specialty, @RequestParam(required = false) String city) {
    return service.doctors(specialty, city);
  }

  @GetMapping("/appointments")
  List<Appointment> appointments() {
    return service.appointments();
  }

  @PostMapping("/appointments")
  Appointment book(@Valid @RequestBody AppointmentRequest request) {
    return service.book(request);
  }

  @PatchMapping("/appointments/{id}/cancel")
  Appointment cancel(@PathVariable String id) {
    return service.cancelAppointment(id);
  }

  @PatchMapping("/appointments/{id}/reschedule")
  Appointment reschedule(@PathVariable String id, @RequestParam(required = false) String time) {
    return service.rescheduleAppointment(id, time);
  }

  @PostMapping("/consultations")
  Consultation startConsultation(@Valid @RequestBody ConsultationRequest request) {
    return service.startConsultation(request);
  }

  @PostMapping("/consultations/{id}/messages")
  Consultation message(@PathVariable String id, @Valid @RequestBody MessageRequest request) {
    return service.addMessage(id, request);
  }

  @PostMapping("/consultations/{id}/report")
  Consultation report(@PathVariable String id, @Valid @RequestBody ReportRequest request) {
    return service.saveReport(id, request);
  }

  @GetMapping("/prescriptions")
  List<Prescription> prescriptions() {
    return service.prescriptions();
  }

  @PostMapping("/prescriptions")
  Prescription prescription(@Valid @RequestBody PrescriptionRequest request) {
    return service.createPrescription(request);
  }

  @GetMapping("/pharmacies")
  List<Pharmacy> pharmacies() {
    return service.pharmacies();
  }

  @GetMapping("/stocks")
  List<DrugStock> stocks(@RequestParam(required = false) String query) {
    return service.stocks(query);
  }

  @GetMapping("/orders")
  List<Order> orders() {
    return service.orders();
  }

  @PostMapping("/orders")
  Order order(@Valid @RequestBody OrderRequest request) {
    return service.order(request);
  }

  @PostMapping("/assistant/analyze")
  AiAdvice analyze(@Valid @RequestBody AiRequest request) {
    return service.analyze(request);
  }

  @PostMapping("/sos")
  SosAlert sos(@Valid @RequestBody SosRequest request) {
    return service.triggerSos(request);
  }

  @GetMapping("/deliverables/{type}")
  Map<String, String> deliverable(@PathVariable String type) {
    return Map.of("type", type, "content", service.deliverable(type));
  }
}
