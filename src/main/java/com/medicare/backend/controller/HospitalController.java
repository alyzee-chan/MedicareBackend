package com.medicare.backend.controller;

import com.medicare.backend.dto.AssistantResponse;
import com.medicare.backend.service.MedicareService;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HospitalController {

    private final JdbcTemplate jdbcTemplate;
    private final MedicareService service;

    public HospitalController(JdbcTemplate jdbcTemplate, MedicareService service) {
        this.jdbcTemplate = jdbcTemplate;
        this.service = service;
    }

    @PostMapping("/auth/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> body) {
        long userId = nextId();
        long patientId = nextId();
        String fullName = text(body, "fullName", "Patient MediCare");
        String email = text(body, "email", "patient@medicare.test").toLowerCase(Locale.ROOT);
        String phone = text(body, "phone", "237600000000");
        String password = text(body, "password", "123456");

        jdbcTemplate.update("""
                insert into users (id, full_name, email, phone, password, role, created_at)
                values (?, ?, ?, ?, ?, ?, ?)
                """,
                userId, fullName, email, phone, password, "PATIENT", LocalDate.now().toString());
        jdbcTemplate.update("""
                insert into patients (id, user_id, age, gender, city, blood_group, allergies, emergency_contact, insurance_number)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                patientId,
                userId,
                text(body, "age", "18"),
                text(body, "gender", "Non precise"),
                text(body, "city", "Douala"),
                text(body, "bloodGroup", "O+"),
                text(body, "allergies", "Aucune"),
                text(body, "emergencyContact", phone),
                text(body, "insuranceNumber", "Non renseigne"));

        return authResponse(userId, patientId, fullName, email, phone);
    }

    @PostMapping("/auth/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> body) {
        String email = text(body, "email", "").toLowerCase(Locale.ROOT);
        String password = text(body, "password", "");
        return jdbcTemplate.queryForObject("""
                select u.id user_id, coalesce(p.id, 0) patient_id, u.full_name, u.email, u.phone, u.role
                from users u
                left join patients p on p.user_id = u.id
                where lower(u.email) = ? and u.password = ?
                limit 1
                """,
                (rs, rowNum) -> authResponse(
                        rs.getLong("user_id"),
                        rs.getLong("patient_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("role")),
                email, password);
    }

    @GetMapping("/admin/dashboard")
    public Map<String, Object> adminDashboard() {
        return Map.of(
                "stats", Map.of(
                        "patients", count("patients"),
                        "medecins", count("doctors"),
                        "hopitaux", count("hospitals"),
                        "partenariats", count("partnerships"),
                        "rendezVous", count("appointments"),
                        "ordonnances", count("prescriptions"),
                        "commandes", count("orders"),
                        "alertesSos", count("sos_alerts")),
                "users", jdbcTemplate.queryForList("select id, full_name fullName, email, phone, role, created_at createdAt from users order by id desc"),
                "doctors", jdbcTemplate.queryForList("select id, name, specialty, city, clinic, rating, next_slot nextSlot, available from doctors order by rating desc"),
                "hospitals", jdbcTemplate.queryForList("select id, name, city, type, beds, status from hospitals order by id desc"),
                "partnerships", jdbcTemplate.queryForList("select id, partner_name partnerName, category, city, contract_status contractStatus, started_at startedAt from partnerships order by id desc"),
                "appointments", jdbcTemplate.queryForList("select id, patient_name patientName, doctor_name doctorName, specialty, clinic, date, time, status from appointments order by id desc limit 10"),
                "orders", jdbcTemplate.queryForList("select id, order_number orderNumber, customer_name customerName, medicine_name medicineName, status, payment_method paymentMethod, pharmacy_name pharmacyName from orders order by id desc limit 10"));
    }

    @GetMapping("/doctors")
    public List<Map<String, Object>> doctors(
            @RequestParam(required = false, defaultValue = "") String specialty,
            @RequestParam(required = false, defaultValue = "") String city) {
        String specialtyNeedle = "%" + specialty.toLowerCase(Locale.ROOT) + "%";
        String cityNeedle = "%" + city.toLowerCase(Locale.ROOT) + "%";
        return jdbcTemplate.queryForList("""
                select id, name, specialty, city, clinic, rating, next_slot nextSlot, available
                from doctors
                where lower(specialty) like ? and lower(city) like ?
                order by available desc, rating desc
                """, specialtyNeedle, cityNeedle);
    }

    @PatchMapping("/appointments/{id}/cancel")
    public Map<String, Object> cancelAppointment(@PathVariable long id) {
        jdbcTemplate.update("update appointments set status = ? where id = ?", "Annule", id);
        return Map.of("id", id, "status", "Annule");
    }

    @PatchMapping("/appointments/{id}/reschedule")
    public Map<String, Object> rescheduleAppointment(@PathVariable long id, @RequestParam String time) {
        jdbcTemplate.update("update appointments set status = ?, time = ? where id = ?", "Replanifie", time, id);
        return Map.of("id", id, "status", "Replanifie", "time", time);
    }

    @GetMapping("/stocks")
    public List<Map<String, Object>> stocks(@RequestParam(required = false, defaultValue = "") String query) {
        String needle = "%" + query.toLowerCase(Locale.ROOT) + "%";
        return jdbcTemplate.queryForList("""
                select m.id, m.name, m.form, m.price, m.stock, m.note,
                       p.name pharmacyName, p.city, p.distance
                from medicines m
                cross join pharmacies p
                where lower(m.name) like ? or lower(m.searchable) like ?
                order by p.rating desc, m.id
                limit 12
                """, needle, needle);
    }

    @PostMapping("/assistant/analyze")
    public Map<String, Object> assistantAnalyze(@RequestBody Map<String, Object> body) {
        String symptoms = text(body, "symptoms", "");
        AssistantResponse triage = service.triage(symptoms);
        String specialty = triage.urgencyLevel().equals("critique")
                ? "Urgences / Medecin de garde"
                : symptoms.toLowerCase(Locale.ROOT).contains("enfant") ? "Pediatrie" : "Medecine generale";
        return Map.of(
                "advice", triage.recommendation(),
                "recommendedSpecialty", specialty,
                "urgent", triage.urgencyLevel().equals("critique"),
                "recommendations", triage.advice());
    }

    @PostMapping("/sos")
    public Map<String, Object> sos(@RequestBody Map<String, Object> body) {
        long id = nextId();
        String patient = text(body, "patient", text(body, "patientName", "Patient"));
        String latitude = String.valueOf(body.getOrDefault("latitude", "3.866"));
        String longitude = String.valueOf(body.getOrDefault("longitude", "11.5167"));
        jdbcTemplate.update("""
                insert into sos_alerts (id, patient_name, age_group, symptom, latitude, longitude, note, created_at)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id, patient, text(body, "ageGroup", "Adulte"), text(body, "symptom", "Urgence"),
                latitude, longitude, text(body, "note", "Alerte mobile"), LocalDate.now().toString());
        return Map.of(
                "id", id,
                "status", "declenche",
                "message", "SOS enregistre, contacts d'urgence et medecin de garde alertes.",
                "doctor", "Dr. Alain MBALLA",
                "eta", "3 min");
    }

    @GetMapping("/prescriptions")
    public List<Map<String, Object>> prescriptions() {
        return jdbcTemplate.queryForList("""
                select id, patient_name patient, doctor_name doctor, medicine_name medicine, dosage,
                       signature, pharmacy_name pharmacy, qr_code qrCode, status, created_at createdAt
                from prescriptions
                order by id desc
                """);
    }

    @PostMapping("/prescriptions")
    public Map<String, Object> createPrescription(@RequestBody Map<String, Object> body) {
        long id = nextId();
        String qr = "MC-RX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        jdbcTemplate.update("""
                insert into prescriptions (id, patient_name, doctor_name, medicine_name, dosage, signature, pharmacy_name, qr_code, status, created_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                text(body, "patient", "Sophie"),
                text(body, "doctor", "Dr. Jeanne EVOUNA"),
                text(body, "medicine", "OBH Combi"),
                text(body, "dosage", "1 dose matin et soir"),
                text(body, "signature", "Signature electronique"),
                text(body, "pharmacy", "Pharmacie du soleil"),
                qr,
                "Transmise",
                LocalDate.now().toString());
        return Map.of(
                "id", id,
                "patient", text(body, "patient", "Sophie"),
                "doctor", text(body, "doctor", "Dr. Jeanne EVOUNA"),
                "medicine", text(body, "medicine", "OBH Combi"),
                "dosage", text(body, "dosage", "1 dose matin et soir"),
                "signature", text(body, "signature", "Signature electronique"),
                "pharmacy", text(body, "pharmacy", "Pharmacie du soleil"),
                "qrCode", qr,
                "status", "Transmise",
                "createdAt", LocalDate.now().toString());
    }

    @PostMapping("/consultations")
    public Map<String, Object> startConsultation(@RequestBody Map<String, Object> body) {
        long id = nextId();
        String room = "ROOM-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase(Locale.ROOT);
        jdbcTemplate.update("""
                insert into consultations (id, patient_name, doctor_name, status, room_code, report, created_at)
                values (?, ?, ?, ?, ?, ?, ?)
                """,
                id, text(body, "patient", "Sophie"), text(body, "doctor", "Dr. Alain MBALLA"),
                "Salle attente", room, "", LocalDate.now().toString());
        return Map.of("id", id, "status", "Salle attente", "roomCode", room,
                "patient", text(body, "patient", "Sophie"), "doctor", text(body, "doctor", "Dr. Alain MBALLA"));
    }

    @PostMapping("/consultations/{id}/messages")
    public Map<String, Object> consultationMessage(@PathVariable long id, @RequestBody Map<String, Object> body) {
        jdbcTemplate.update("""
                insert into consultation_messages (id, consultation_id, sender, message, created_at)
                values (?, ?, ?, ?, ?)
                """,
                nextId(), id, "Patient", text(body, "message", ""), LocalDate.now().toString());
        return Map.of("id", id, "saved", true, "message", text(body, "message", ""));
    }

    @PostMapping("/consultations/{id}/report")
    public Map<String, Object> consultationReport(@PathVariable long id, @RequestBody Map<String, Object> body) {
        String report = text(body, "report", "");
        jdbcTemplate.update("update consultations set status = ?, report = ? where id = ?", "Compte-rendu enregistre", report, id);
        return Map.of("id", id, "status", "Compte-rendu enregistre", "report", report);
    }

    private Map<String, Object> authResponse(long userId, long patientId, String fullName, String email, String phone) {
        return authResponse(userId, patientId, fullName, email, phone, "PATIENT");
    }

    private Map<String, Object> authResponse(long userId, long patientId, String fullName, String email, String phone, String role) {
        return Map.of(
                "token", "demo-token-" + userId,
                "user", Map.of("id", userId, "fullName", fullName, "email", email, "phone", phone, "role", role),
                "patient", Map.of("id", patientId, "name", fullName));
    }

    private long nextId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }

    private String text(Map<String, Object> body, String key, String fallback) {
        Object value = body.get(key);
        if (value == null || String.valueOf(value).isBlank()) {
            return fallback;
        }
        return String.valueOf(value).trim();
    }

    private int count(String table) {
        Integer value = jdbcTemplate.queryForObject("select count(*) from " + table, Integer.class);
        return value == null ? 0 : value;
    }
}
