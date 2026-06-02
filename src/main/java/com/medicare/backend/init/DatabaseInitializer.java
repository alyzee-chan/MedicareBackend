package com.medicare.backend.init;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        createTables();
        seedData();
    }

    private void createTables() {
        jdbcTemplate.execute("""
            create table if not exists appointments (
              id integer primary key,
              patient_name text not null,
              doctor_name text not null,
              specialty text not null,
              clinic text not null,
              date text not null,
              time text not null,
              status text not null,
              reason text
            )
            """);

        jdbcTemplate.execute("""
            create table if not exists medicines (
              id integer primary key,
              name text not null,
              form text not null,
              price text not null,
              stock text not null,
              alternative text not null,
              note text not null,
              searchable text not null
            )
            """);

        jdbcTemplate.execute("""
            create table if not exists pharmacies (
              id integer primary key,
              name text not null,
              rating real not null,
              distance text not null,
              label text not null,
              open text not null,
              city text not null
            )
            """);

        jdbcTemplate.execute("""
            create table if not exists profiles (
              id integer primary key,
              name text not null,
              role text not null,
              tone text not null
            )
            """);

        jdbcTemplate.execute("""
            create table if not exists orders (
              id integer primary key,
              order_number text not null,
              customer_name text not null,
              medicine_name text not null,
              quantity integer not null,
              status text not null,
              payment_method text not null,
              eta text not null,
              pharmacy_name text not null,
              fulfillment_mode text not null
            )
            """);

        jdbcTemplate.execute("""
            create table if not exists sos_alerts (
              id integer primary key,
              patient_name text not null,
              age_group text not null,
              symptom text not null,
              latitude text not null,
              longitude text not null,
              note text not null,
              created_at text not null
            )
            """);

        jdbcTemplate.execute("""
            create table if not exists medical_records (
              id integer primary key,
              patient_name text not null,
              allergies text not null,
              treatments text not null,
              vaccines text not null,
              notes text not null
            )
            """);

        jdbcTemplate.execute("""
            create table if not exists users (
              id integer primary key,
              full_name text not null,
              email text not null unique,
              phone text not null,
              password text not null,
              role text not null,
              created_at text not null
            )
            """);

        jdbcTemplate.execute("""
            create table if not exists patients (
              id integer primary key,
              user_id integer not null,
              age text not null,
              gender text not null,
              city text not null,
              blood_group text not null,
              allergies text not null,
              emergency_contact text not null,
              insurance_number text not null
            )
            """);

        jdbcTemplate.execute("""
            create table if not exists doctors (
              id integer primary key,
              name text not null,
              specialty text not null,
              city text not null,
              clinic text not null,
              rating real not null,
              next_slot text not null,
              available text not null
            )
            """);

        jdbcTemplate.execute("""
            create table if not exists prescriptions (
              id integer primary key,
              patient_name text not null,
              doctor_name text not null,
              medicine_name text not null,
              dosage text not null,
              signature text not null,
              pharmacy_name text not null,
              qr_code text not null,
              status text not null,
              created_at text not null
            )
            """);

        jdbcTemplate.execute("""
            create table if not exists consultations (
              id integer primary key,
              patient_name text not null,
              doctor_name text not null,
              status text not null,
              room_code text not null,
              report text not null,
              created_at text not null
            )
            """);

        jdbcTemplate.execute("""
            create table if not exists consultation_messages (
              id integer primary key,
              consultation_id integer not null,
              sender text not null,
              message text not null,
              created_at text not null
            )
            """);

        jdbcTemplate.execute("""
            create table if not exists hospitals (
              id integer primary key,
              name text not null,
              city text not null,
              type text not null,
              beds integer not null,
              status text not null
            )
            """);

        jdbcTemplate.execute("""
            create table if not exists partnerships (
              id integer primary key,
              partner_name text not null,
              category text not null,
              city text not null,
              contract_status text not null,
              started_at text not null
            )
            """);
    }

    private void seedData() {
        if (count("appointments") == 0) {
            jdbcTemplate.update("""
                insert into appointments (id, patient_name, doctor_name, specialty, clinic, date, time, status, reason)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                101L, "Sophie", "Dr. Jeanne EVOUNA", "Pediatrie", "Hopital de la Casse", "23 fevrier 2026", "10h00", "Confirme", "Consultation de suivi");
            jdbcTemplate.update("""
                insert into appointments (id, patient_name, doctor_name, specialty, clinic, date, time, status, reason)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                102L, "Sophie", "Dr. Marc NGO", "Cardiologie", "Teleconsultation", "24 fevrier 2026", "14h30", "En attente", "Bilans et conseils");
            jdbcTemplate.update("""
                insert into appointments (id, patient_name, doctor_name, specialty, clinic, date, time, status, reason)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                103L, "Ava", "Dr. Aissatou MBEA", "Vaccination", "Centre medical Douala", "27 fevrier 2026", "09h15", "Confirme", "Vaccin de rappel");
        }

        if (count("medicines") == 0) {
            jdbcTemplate.update("""
                insert into medicines (id, name, form, price, stock, alternative, note, searchable)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                201L, "OBH Combi", "75 ml", "XAF 5,750", "En stock", "Gen. disponible", "Antitussif et fluidifiant", "obh combi toux");
            jdbcTemplate.update("""
                insert into medicines (id, name, form, price, stock, alternative, note, searchable)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                202L, "Bodetoline", "10 ml", "XAF 2,900", "Stock faible", "Equivalent propose", "Solution de soin local", "bodetoline");
            jdbcTemplate.update("""
                insert into medicines (id, name, form, price, stock, alternative, note, searchable)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                203L, "Badrexin", "300 mg", "XAF 1,500", "En stock", "Prix compare", "Antalgique et anti-inflammatoire", "badrexin douleur");
            jdbcTemplate.update("""
                insert into medicines (id, name, form, price, stock, alternative, note, searchable)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                204L, "Paracetamol", "500 mg", "XAF 600", "En stock", "Generique", "Utilisation courante", "paracetamol douleur fievre");
        }

        if (count("pharmacies") == 0) {
            jdbcTemplate.update("""
                insert into pharmacies (id, name, rating, distance, label, open, city)
                values (?, ?, ?, ?, ?, ?, ?)
                """,
                301L, "Pharmacie du soleil", 4.8, "800 m", "Confiance", "Ouverte jusqu'a 22h", "Douala");
            jdbcTemplate.update("""
                insert into pharmacies (id, name, rating, distance, label, open, city)
                values (?, ?, ?, ?, ?, ?, ?)
                """,
                302L, "Pharmacie de la lune", 4.7, "1.2 km", "SOS+", "Garde de nuit", "Douala");
            jdbcTemplate.update("""
                insert into pharmacies (id, name, rating, distance, label, open, city)
                values (?, ?, ?, ?, ?, ?, ?)
                """,
                303L, "Pharmacie Mvogo Enyegue", 4.9, "2.0 km", "Stock fort", "Livraison active", "Douala");
            jdbcTemplate.update("""
                insert into pharmacies (id, name, rating, distance, label, open, city)
                values (?, ?, ?, ?, ?, ?, ?)
                """,
                304L, "Pharmacie Centrale", 4.6, "3.4 km", "Confiance", "Dimanche ouvert", "Douala");
        }

        if (count("profiles") == 0) {
            jdbcTemplate.update("insert into profiles (id, name, role, tone) values (?, ?, ?, ?)", 401L, "Sophie", "Patient principal", "#2F63FF");
            jdbcTemplate.update("insert into profiles (id, name, role, tone) values (?, ?, ?, ?)", 402L, "Ava", "Enfant", "#1BC5BD");
            jdbcTemplate.update("insert into profiles (id, name, role, tone) values (?, ?, ?, ?)", 403L, "Maman", "Aidant", "#FF7A45");
            jdbcTemplate.update("insert into profiles (id, name, role, tone) values (?, ?, ?, ?)", 404L, "Papa", "Senior", "#8A5CFF");
        }

        if (count("orders") == 0) {
            jdbcTemplate.update("""
                insert into orders (id, order_number, customer_name, medicine_name, quantity, status, payment_method, eta, pharmacy_name, fulfillment_mode)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                501L, "MC-260223-1", "Sophie", "OBH Combi", 1, "Preparation", "Orange Money", "19 min", "Pharmacie du soleil", "Livraison");
            jdbcTemplate.update("""
                insert into orders (id, order_number, customer_name, medicine_name, quantity, status, payment_method, eta, pharmacy_name, fulfillment_mode)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                502L, "MC-260224-2", "Ava", "Paracetamol", 2, "Depart", "MTN MoMo", "32 min", "Pharmacie Centrale", "Retrait");
        }

        if (count("medical_records") == 0) {
            jdbcTemplate.update("""
                insert into medical_records (id, patient_name, allergies, treatments, vaccines, notes)
                values (?, ?, ?, ?, ?, ?)
                """,
                601L, "Sophie", "Penicilline, arachides", "Vitamine D, antihistaminique", "Calendrier a jour", "Acces temporaire actif et partage securise");
        }

        if (count("users") == 0) {
            jdbcTemplate.update("""
                insert into users (id, full_name, email, phone, password, role, created_at)
                values (?, ?, ?, ?, ?, ?, ?)
                """,
                701L, "Sophie Ngono", "sophie@medicare.test", "237600000000", "123456", "PATIENT", "2026-06-02");
            jdbcTemplate.update("""
                insert into users (id, full_name, email, phone, password, role, created_at)
                values (?, ?, ?, ?, ?, ?, ?)
                """,
                703L, "Admin MediCare+", "admin@medicare.test", "237655000000", "admin123", "ADMIN", "2026-06-02");
            jdbcTemplate.update("""
                insert into patients (id, user_id, age, gender, city, blood_group, allergies, emergency_contact, insurance_number)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                702L, 701L, "24", "Femme", "Douala", "O+", "Penicilline", "237699000000", "MC-ASS-2026-001");
        }

        if (count("doctors") == 0) {
            jdbcTemplate.update("insert into doctors (id, name, specialty, city, clinic, rating, next_slot, available) values (?, ?, ?, ?, ?, ?, ?, ?)",
                    801L, "Dr. Jeanne EVOUNA", "Pediatrie", "Douala", "Hopital de la Caisse", 4.9, "10:00", "Oui");
            jdbcTemplate.update("insert into doctors (id, name, specialty, city, clinic, rating, next_slot, available) values (?, ?, ?, ?, ?, ?, ?, ?)",
                    802L, "Dr. Alain MBALLA", "Medecine generale", "Yaounde", "Teleconsultation", 4.8, "11:30", "Oui");
            jdbcTemplate.update("insert into doctors (id, name, specialty, city, clinic, rating, next_slot, available) values (?, ?, ?, ?, ?, ?, ?, ?)",
                    803L, "Dr. Aissatou MBEA", "Cardiologie", "Douala", "Clinique Bonapriso", 4.7, "14:00", "Oui");
            jdbcTemplate.update("insert into doctors (id, name, specialty, city, clinic, rating, next_slot, available) values (?, ?, ?, ?, ?, ?, ?, ?)",
                    804L, "Dr. Eric KAMDEM", "Dermatologie", "Bafoussam", "Centre medical Ouest", 4.6, "16:00", "Non");
        }

        if (count("prescriptions") == 0) {
            jdbcTemplate.update("""
                insert into prescriptions (id, patient_name, doctor_name, medicine_name, dosage, signature, pharmacy_name, qr_code, status, created_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                901L, "Sophie", "Dr. Jeanne EVOUNA", "OBH Combi", "1 dose matin et soir", "EVOUNA-J-2026", "Pharmacie du soleil", "MC-RX-901", "Transmise", "2026-06-02");
        }

        if (count("hospitals") == 0) {
            jdbcTemplate.update("insert into hospitals (id, name, city, type, beds, status) values (?, ?, ?, ?, ?, ?)",
                    1001L, "Hopital Laquintinie", "Douala", "Public", 320, "Partenaire actif");
            jdbcTemplate.update("insert into hospitals (id, name, city, type, beds, status) values (?, ?, ?, ?, ?, ?)",
                    1002L, "Clinique Bonapriso", "Douala", "Prive", 85, "Convention signee");
            jdbcTemplate.update("insert into hospitals (id, name, city, type, beds, status) values (?, ?, ?, ?, ?, ?)",
                    1003L, "Centre medical Mvog Ada", "Yaounde", "Centre medical", 60, "En integration");
        }

        if (count("partnerships") == 0) {
            jdbcTemplate.update("insert into partnerships (id, partner_name, category, city, contract_status, started_at) values (?, ?, ?, ?, ?, ?)",
                    1101L, "Orange Money Cameroun", "Paiement Mobile Money", "National", "Actif", "2026-01-20");
            jdbcTemplate.update("insert into partnerships (id, partner_name, category, city, contract_status, started_at) values (?, ?, ?, ?, ?, ?)",
                    1102L, "Reseau Pharmacies Confiance", "Pharmacies partenaires", "Douala", "Actif", "2026-02-14");
            jdbcTemplate.update("insert into partnerships (id, partner_name, category, city, contract_status, started_at) values (?, ?, ?, ?, ?, ?)",
                    1103L, "Medecins de garde Cameroun", "Urgence SOS", "Yaounde", "En validation", "2026-03-08");
        }
    }

    private int count(String table) {
        Integer value = jdbcTemplate.queryForObject("select count(*) from " + table, Integer.class);
        return value == null ? 0 : value;
    }
}
