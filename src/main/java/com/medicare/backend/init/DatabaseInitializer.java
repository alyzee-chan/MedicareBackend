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
    }

    private int count(String table) {
        Integer value = jdbcTemplate.queryForObject("select count(*) from " + table, Integer.class);
        return value == null ? 0 : value;
    }
}
