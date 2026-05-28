package com.medicare.backend.repository;

import com.medicare.backend.dto.AppointmentDto;
import com.medicare.backend.dto.AppointmentRequest;
import com.medicare.backend.dto.MedicalRecordDto;
import com.medicare.backend.dto.MedicineDto;
import com.medicare.backend.dto.OrderDto;
import com.medicare.backend.dto.OrderRequest;
import com.medicare.backend.dto.PharmacyDto;
import com.medicare.backend.dto.ProfileDto;
import com.medicare.backend.dto.SosAlertRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MedicareRepository {

    private final JdbcTemplate jdbcTemplate;

    public MedicareRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AppointmentDto> findAppointments() {
        return jdbcTemplate.query("""
                select id, patient_name, doctor_name, specialty, clinic, date, time, status, reason
                from appointments
                order by id desc
                """,
                (rs, rowNum) -> new AppointmentDto(
                        rs.getLong("id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("specialty"),
                        rs.getString("clinic"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("status"),
                        rs.getString("reason")
                ));
    }

    public AppointmentDto createAppointment(AppointmentRequest request) {
        long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        String status = "Confirme";
        jdbcTemplate.update("""
                insert into appointments (id, patient_name, doctor_name, specialty, clinic, date, time, status, reason)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id, request.patientName(), request.doctorName(), request.specialty(), request.clinic(),
                request.date(), request.time(), status, request.reason());
        return new AppointmentDto(id, request.patientName(), request.doctorName(), request.specialty(), request.clinic(), request.date(), request.time(), status, request.reason());
    }

    public List<MedicineDto> findMedicines(String query) {
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (needle.isBlank()) {
            return queryMedicines("select id, name, form, price, stock, alternative, note from medicines order by id");
        }
        return queryMedicines("""
                select id, name, form, price, stock, alternative, note
                from medicines
                where lower(name) like ? or lower(searchable) like ?
                order by id
                """, "%" + needle + "%", "%" + needle + "%");
    }

    private List<MedicineDto> queryMedicines(String sql, Object... args) {
        return jdbcTemplate.query(sql, args, (rs, rowNum) -> new MedicineDto(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("form"),
                rs.getString("price"),
                rs.getString("stock"),
                rs.getString("alternative"),
                rs.getString("note")
        ));
    }

    public List<PharmacyDto> findPharmacies() {
        return jdbcTemplate.query("""
                select id, name, rating, distance, label, open, city
                from pharmacies
                order by rating desc, id asc
                """,
                (rs, rowNum) -> new PharmacyDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getDouble("rating"),
                        rs.getString("distance"),
                        rs.getString("label"),
                        rs.getString("open"),
                        rs.getString("city")
                ));
    }

    public List<ProfileDto> findProfiles() {
        return jdbcTemplate.query("""
                select id, name, role, tone
                from profiles
                order by id
                """,
                (rs, rowNum) -> new ProfileDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("tone")
                ));
    }

    public List<OrderDto> findOrders() {
        return jdbcTemplate.query("""
                select id, order_number, customer_name, medicine_name, quantity, status, payment_method, eta, pharmacy_name, fulfillment_mode
                from orders
                order by id desc
                """,
                (rs, rowNum) -> new OrderDto(
                        rs.getLong("id"),
                        rs.getString("order_number"),
                        rs.getString("customer_name"),
                        rs.getString("medicine_name"),
                        rs.getInt("quantity"),
                        rs.getString("status"),
                        rs.getString("payment_method"),
                        rs.getString("eta"),
                        rs.getString("pharmacy_name"),
                        rs.getString("fulfillment_mode")
                ));
    }

    public OrderDto createOrder(OrderRequest request) {
        long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        String orderNumber = "MC-" + LocalDate.now().toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase(Locale.ROOT);
        String status = "Preparation";
        String eta = "25 min";
        jdbcTemplate.update("""
                insert into orders (id, order_number, customer_name, medicine_name, quantity, status, payment_method, eta, pharmacy_name, fulfillment_mode)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id, orderNumber, request.customerName(), request.medicineName(), request.quantity(), status,
                request.paymentMethod(), eta, request.pharmacyName(), request.fulfillmentMode());
        return new OrderDto(id, orderNumber, request.customerName(), request.medicineName(), request.quantity(), status, request.paymentMethod(), eta, request.pharmacyName(), request.fulfillmentMode());
    }

    public MedicalRecordDto findMedicalRecord() {
        return jdbcTemplate.queryForObject("""
                select patient_name, allergies, treatments, vaccines, notes
                from medical_records
                order by id
                limit 1
                """,
                (rs, rowNum) -> new MedicalRecordDto(
                        rs.getString("patient_name"),
                        splitValues(rs.getString("allergies")),
                        splitValues(rs.getString("treatments")),
                        splitValues(rs.getString("vaccines")),
                        splitValues(rs.getString("notes"))
                ));
    }

    public void saveSosAlert(SosAlertRequest request) {
        long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        jdbcTemplate.update("""
                insert into sos_alerts (id, patient_name, age_group, symptom, latitude, longitude, note, created_at)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id, request.patientName(), request.ageGroup(), request.symptom(), request.latitude(), request.longitude(), request.note(), LocalDate.now().toString());
    }

    private List<String> splitValues(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        String[] parts = value.split(",");
        List<String> values = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isBlank()) {
                values.add(trimmed);
            }
        }
        return values;
    }
}
