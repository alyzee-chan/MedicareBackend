package com.medicare.backend.repository;

import com.medicare.backend.dto.AppointmentDto;
import com.medicare.backend.dto.AppointmentRequest;
import com.medicare.backend.dto.FamilyProfileDto;
import com.medicare.backend.dto.FamilyProfileRequest;
import com.medicare.backend.dto.MedicalRecordDto;
import com.medicare.backend.dto.MedicineDto;
import com.medicare.backend.dto.OrderDto;
import com.medicare.backend.dto.OrderRequest;
import com.medicare.backend.dto.PharmacyDto;
import com.medicare.backend.dto.ProfileDto;
import com.medicare.backend.dto.SosAlertRequest;
import com.medicare.backend.dto.UserDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MedicareRepository {

    private final JdbcTemplate jdbcTemplate;

    public MedicareRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ═══════════════════════════════════════════
    // ─── USERS ───
    // ═══════════════════════════════════════════

    public UserDto findUserByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject("""
                    select id, full_name, email, phone, date_of_birth, blood_group, role, avatar_url, created_at
                    from users where email = ?
                    """,
                    (rs, rowNum) -> new UserDto(
                            rs.getLong("id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("date_of_birth"),
                            rs.getString("blood_group"),
                            rs.getString("role"),
                            rs.getString("avatar_url"),
                            rs.getString("created_at")),
                    email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UserDto findUserById(long id) {
        try {
            return jdbcTemplate.queryForObject("""
                    select id, full_name, email, phone, date_of_birth, blood_group, role, avatar_url, created_at
                    from users where id = ?
                    """,
                    (rs, rowNum) -> new UserDto(
                            rs.getLong("id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("date_of_birth"),
                            rs.getString("blood_group"),
                            rs.getString("role"),
                            rs.getString("avatar_url"),
                            rs.getString("created_at")),
                    id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String findPasswordHashByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(
                    "select password_hash from users where email = ?",
                    String.class,
                    email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public long createUser(String email, String passwordHash, String fullName, String phone,
                           String dateOfBirth, String bloodGroup, String role) {
        long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        jdbcTemplate.update("""
                insert into users (id, email, password_hash, full_name, phone, date_of_birth, blood_group, role, avatar_url, created_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id, email, passwordHash, fullName, phone, dateOfBirth, bloodGroup,
                role != null ? role : "PATIENT", null, LocalDate.now().toString());
        return id;
    }

    public void updateUser(long userId, String fullName, String phone, String dateOfBirth, String bloodGroup) {
        jdbcTemplate.update("""
                update users set full_name = ?, phone = ?, date_of_birth = ?, blood_group = ?
                where id = ?
                """,
                fullName, phone, dateOfBirth, bloodGroup, userId);
    }

    // ═══════════════════════════════════════════
    // ─── FAMILY PROFILES ───
    // ═══════════════════════════════════════════

    public List<FamilyProfileDto> findFamilyProfiles(long userId) {
        return jdbcTemplate.query("""
                select id, user_id, name, role, age, blood_group, avatar_url
                from user_profiles where user_id = ? order by id
                """,
                (rs, rowNum) -> new FamilyProfileDto(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("age"),
                        rs.getString("blood_group"),
                        rs.getString("avatar_url")),
                userId);
    }

    public FamilyProfileDto createFamilyProfile(long userId, FamilyProfileRequest request) {
        long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        jdbcTemplate.update("""
                insert into user_profiles (id, user_id, name, role, age, blood_group, avatar_url)
                values (?, ?, ?, ?, ?, ?, ?)
                """,
                id, userId, request.name(), request.role(), request.age(), request.bloodGroup(), null);
        return new FamilyProfileDto(id, userId, request.name(), request.role(), request.age(), request.bloodGroup(), null);
    }

    public void deleteFamilyProfile(long profileId, long userId) {
        jdbcTemplate.update("delete from user_profiles where id = ? and user_id = ?", profileId, userId);
    }

    // ═══════════════════════════════════════════
    // ─── APPOINTMENTS ───
    // ═══════════════════════════════════════════

    public List<AppointmentDto> findAppointments(long userId) {
        return jdbcTemplate.query("""
                select id, patient_name, doctor_name, specialty, clinic, date, time, status, reason
                from appointments
                where user_id = ?
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
                        rs.getString("reason")),
                userId);
    }

    public AppointmentDto createAppointment(long userId, AppointmentRequest request) {
        long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        String status = "Confirmé";
        jdbcTemplate.update("""
                insert into appointments (id, user_id, patient_name, doctor_name, specialty, clinic, date, time, status, reason)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id, userId, request.patientName(), request.doctorName(), request.specialty(), request.clinic(),
                request.date(), request.time(), status, request.reason());
        return new AppointmentDto(id, request.patientName(), request.doctorName(), request.specialty(),
                request.clinic(), request.date(), request.time(), status, request.reason());
    }

    // ═══════════════════════════════════════════
    // ─── MEDICINES ───
    // ═══════════════════════════════════════════

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
        return jdbcTemplate.query(sql, (rs, rowNum) -> new MedicineDto(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("form"),
                rs.getString("price"),
                rs.getString("stock"),
                rs.getString("alternative"),
                rs.getString("note")), args);
    }

    // ═══════════════════════════════════════════
    // ─── PHARMACIES ───
    // ═══════════════════════════════════════════

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
                        rs.getString("city")));
    }

    // ═══════════════════════════════════════════
    // ─── PROFILES (legacy) ───
    // ═══════════════════════════════════════════

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
                        rs.getString("tone")));
    }

    // ═══════════════════════════════════════════
    // ─── ORDERS ───
    // ═══════════════════════════════════════════

    public List<OrderDto> findOrders(long userId) {
        return jdbcTemplate.query(
                """
                        select id, order_number, customer_name, medicine_name, quantity, status, payment_method, eta, pharmacy_name, fulfillment_mode
                        from orders
                        where user_id = ?
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
                        rs.getString("fulfillment_mode")),
                userId);
    }

    public OrderDto createOrder(long userId, OrderRequest request) {
        long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        String orderNumber = "MC-" + LocalDate.now().toString().replace("-", "") + "-"
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase(Locale.ROOT);
        String status = "En préparation";
        String eta = "25 min";
        jdbcTemplate.update(
                """
                        insert into orders (id, user_id, order_number, customer_name, medicine_name, quantity, status, payment_method, eta, pharmacy_name, fulfillment_mode)
                        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                id, userId, orderNumber, request.customerName(), request.medicineName(), request.quantity(), status,
                request.paymentMethod(), eta, request.pharmacyName(), request.fulfillmentMode());
        return new OrderDto(id, orderNumber, request.customerName(), request.medicineName(), request.quantity(), status,
                request.paymentMethod(), eta, request.pharmacyName(), request.fulfillmentMode());
    }

    // ═══════════════════════════════════════════
    // ─── MEDICAL RECORDS ───
    // ═══════════════════════════════════════════

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
                        splitValues(rs.getString("notes"))));
    }

    // ═══════════════════════════════════════════
    // ─── SOS ───
    // ═══════════════════════════════════════════

    public void saveSosAlert(SosAlertRequest request) {
        long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        jdbcTemplate.update("""
                insert into sos_alerts (id, patient_name, age_group, symptom, latitude, longitude, note, created_at)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id, request.patientName(), request.ageGroup(), request.symptom(), request.latitude(),
                request.longitude(), request.note(), LocalDate.now().toString());
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
