package com.medicare.backend.init;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements ApplicationRunner {

        private final JdbcTemplate jdbcTemplate;
        private final PasswordEncoder passwordEncoder;

        public DatabaseInitializer(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
                this.jdbcTemplate = jdbcTemplate;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        public void run(ApplicationArguments args) {
                createTables();
                seedData();
        }

        private void createTables() {
                // ─── Users ───
                jdbcTemplate.execute("""
                                create table if not exists users (
                                  id integer primary key,
                                  email text not null unique,
                                  password_hash text not null,
                                  full_name text not null,
                                  phone text not null,
                                  date_of_birth text,
                                  blood_group text,
                                  role text not null default 'PATIENT',
                                  avatar_url text,
                                  created_at text not null
                                )
                                """);

                // ─── Family profiles (linked to a user) ───
                jdbcTemplate.execute("""
                                create table if not exists user_profiles (
                                  id integer primary key,
                                  user_id integer not null,
                                  name text not null,
                                  role text not null,
                                  age text,
                                  blood_group text,
                                  avatar_url text,
                                  foreign key (user_id) references users(id)
                                )
                                """);

                // ─── Existing tables ───
                jdbcTemplate.execute("""
                                create table if not exists appointments (
                                  id integer primary key,
                                  user_id integer not null,
                                  patient_name text not null,
                                  doctor_name text not null,
                                  specialty text not null,
                                  clinic text not null,
                                  date text not null,
                                  time text not null,
                                  status text not null,
                                  reason text,
                                  foreign key (user_id) references users(id)
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
                                  city text not null,
                                  lat real,
                                  lng real
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
                                  user_id integer not null,
                                  order_number text not null,
                                  customer_name text not null,
                                  medicine_name text not null,
                                  quantity integer not null,
                                  status text not null,
                                  payment_method text not null,
                                  eta text not null,
                                  pharmacy_name text not null,
                                  fulfillment_mode text not null,
                                  foreign key (user_id) references users(id)
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
                // ─── Demo user ───
                if (count("users") == 0) {
                        String hashedPassword = passwordEncoder.encode("Medicare2026!");
                        jdbcTemplate.update(
                                        """
                                                        insert into users (id, email, password_hash, full_name, phone, date_of_birth, blood_group, role, avatar_url, created_at)
                                                        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                                        """,
                                        1L, "demo@medicare.cm", hashedPassword, "Sophie Mbarga",
                                        "+237 691 234 567", "15/03/2002", "O+", "PATIENT", null, "2026-01-15");
                }

                // ─── Demo family profiles ───
                if (count("user_profiles") == 0) {
                        jdbcTemplate.update(
                                        "insert into user_profiles (id, user_id, name, role, age, blood_group, avatar_url) values (?, ?, ?, ?, ?, ?, ?)",
                                        1L, 1L, "Maman", "Aidant", "52 ans", "A+", null);
                        jdbcTemplate.update(
                                        "insert into user_profiles (id, user_id, name, role, age, blood_group, avatar_url) values (?, ?, ?, ?, ?, ?, ?)",
                                        2L, 1L, "Papa", "Senior", "58 ans", "O+", null);
                        jdbcTemplate.update(
                                        "insert into user_profiles (id, user_id, name, role, age, blood_group, avatar_url) values (?, ?, ?, ?, ?, ?, ?)",
                                        3L, 1L, "Ava", "Enfant", "6 ans", "O+", null);
                }

                // ─── Existing seed data ───
                if (count("appointments") == 0) {
                        jdbcTemplate.update(
                                        """
                                                        insert into appointments (id, user_id, patient_name, doctor_name, specialty, clinic, date, time, status, reason)
                                                        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                                        """,
                                        101L, 1L, "Sophie", "Dr. Jeanne EVOUNA", "Pediatrie", "Hopital de la Casse",
                                        "23 fevrier 2026",
                                        "10h00", "Confirmé", "Consultation de suivi");
                        jdbcTemplate.update(
                                        """
                                                        insert into appointments (id, user_id, patient_name, doctor_name, specialty, clinic, date, time, status, reason)
                                                        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                                        """,
                                        102L, 1L, "Sophie", "Dr. Marc NGO", "Cardiologie", "Teleconsultation",
                                        "24 fevrier 2026", "14h30",
                                        "En attente", "Bilans et conseils");
                        jdbcTemplate.update(
                                        """
                                                        insert into appointments (id, user_id, patient_name, doctor_name, specialty, clinic, date, time, status, reason)
                                                        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                                        """,
                                        103L, 1L, "Ava", "Dr. Aissatou MBEA", "Vaccination", "Centre medical Douala",
                                        "27 fevrier 2026",
                                        "09h15", "Confirmé", "Vaccin de rappel");
                }

                if (count("medicines") == 0) {
                        jdbcTemplate.update(
                                        """
                                                        insert into medicines (id, name, form, price, stock, alternative, note, searchable)
                                                        values (?, ?, ?, ?, ?, ?, ?, ?)
                                                        """,
                                        201L, "Aspirine UPSA", "500 mg", "XAF 1,200", "En stock", "Aspegic",
                                        "Douleurs et fièvre, adulte",
                                        "aspirine aspegic douleur fievre");
                        jdbcTemplate.update(
                                        """
                                                        insert into medicines (id, name, form, price, stock, alternative, note, searchable)
                                                        values (?, ?, ?, ?, ?, ?, ?, ?)
                                                        """,
                                        202L, "Doliprane", "1000 mg", "XAF 1,500", "En stock", "Paracétamol",
                                        "Antalgique puissant",
                                        "doliprane paracetamol douleur");
                        jdbcTemplate.update(
                                        """
                                                        insert into medicines (id, name, form, price, stock, alternative, note, searchable)
                                                        values (?, ?, ?, ?, ?, ?, ?, ?)
                                                        """,
                                        203L, "Ventoline", "Seringue/Spray", "XAF 4,500", "Stock faible", "Salbutamol",
                                        "Asthme et difficultés respiratoires",
                                        "ventoline asthme respiratoire");
                        jdbcTemplate.update(
                                        """
                                                        insert into medicines (id, name, form, price, stock, alternative, note, searchable)
                                                        values (?, ?, ?, ?, ?, ?, ?, ?)
                                                        """,
                                        204L, "Gaviscon", "250 ml", "XAF 3,200", "En stock", "Maalox",
                                        "Brulures d'estomac, reflux",
                                        "gaviscon maalox estomac");
                        jdbcTemplate.update(
                                        """
                                                        insert into medicines (id, name, form, price, stock, alternative, note, searchable)
                                                        values (?, ?, ?, ?, ?, ?, ?, ?)
                                                        """,
                                        205L, "Spasfon", "80 mg", "XAF 2,800", "En stock", "Antispasmodique",
                                        "Douleurs abdominales",
                                        "spasfon ventre douleur");
                        jdbcTemplate.update(
                                        """
                                                        insert into medicines (id, name, form, price, stock, alternative, note, searchable)
                                                        values (?, ?, ?, ?, ?, ?, ?, ?)
                                                        """,
                                        206L, "Amoxicilline", "500 mg", "XAF 2,500", "En stock", "Clamoxyl",
                                        "Antibiotique large spectre",
                                        "amoxicilline infection");
                        jdbcTemplate.update(
                                        """
                                                        insert into medicines (id, name, form, price, stock, alternative, note, searchable)
                                                        values (?, ?, ?, ?, ?, ?, ?, ?)
                                                        """,
                                        207L, "Betadine", "125 ml", "XAF 1,800", "En stock", "Antiseptique",
                                        "Désinfectant cutané",
                                        "betadine plaie");
                        jdbcTemplate.update(
                                        """
                                                        insert into medicines (id, name, form, price, stock, alternative, note, searchable)
                                                        values (?, ?, ?, ?, ?, ?, ?, ?)
                                                        """,
                                        208L, "Efferalgan Vitamine C", "Tab", "XAF 2,100", "En stock", "Paracétamol C",
                                        "Fatigue et état grippal",
                                        "efferalgan grippe fatigue");
                }

                if (count("pharmacies") == 0) {
                        jdbcTemplate.update("""
                                        insert into pharmacies (id, name, rating, distance, label, open, city, lat, lng)
                                        values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                                        """,
                                        301L, "Pharmacie de la Paix", 4.9, "450 m", "Recommandé par 98%",
                                        "Ouvert 24h/24", "Douala", 4.0511, 9.7679);
                        jdbcTemplate.update("""
                                        insert into pharmacies (id, name, rating, distance, label, open, city, lat, lng)
                                        values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                                        """,
                                        302L, "Pharmacie du Centre", 4.7, "900 m", "Service client réactif",
                                        "Garde active", "Douala", 4.0485, 9.7042);
                        jdbcTemplate.update("""
                                        insert into pharmacies (id, name, rating, distance, label, open, city, lat, lng)
                                        values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                                        """,
                                        303L, "Pharmacie de l'Avenir", 4.6, "1.2 km", "Stock complet", "Ferme à 22h",
                                        "Yaoundé", 3.8480, 11.5021);
                        jdbcTemplate.update("""
                                        insert into pharmacies (id, name, rating, distance, label, open, city, lat, lng)
                                        values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                                        """,
                                        304L, "Pharmacie du Soleil", 4.8, "2.5 km", "Spécialités rares", "Ouvert",
                                        "Yaoundé", 3.8667, 11.5167);
                        jdbcTemplate.update("""
                                        insert into pharmacies (id, name, rating, distance, label, open, city, lat, lng)
                                        values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                                        """,
                                        305L, "Pharmacie de Bonanjo", 4.5, "500 m", "Accès facile", "Ouvert 24h/24",
                                        "Douala", 4.0435, 9.6845);
                }

                if (count("profiles") == 0) {
                        jdbcTemplate.update("insert into profiles (id, name, role, tone) values (?, ?, ?, ?)", 401L,
                                        "Sophie",
                                        "Patient principal", "#2F63FF");
                        jdbcTemplate.update("insert into profiles (id, name, role, tone) values (?, ?, ?, ?)", 402L,
                                        "Ava",
                                        "Enfant", "#1BC5BD");
                        jdbcTemplate.update("insert into profiles (id, name, role, tone) values (?, ?, ?, ?)", 403L,
                                        "Maman",
                                        "Aidant", "#FF7A45");
                        jdbcTemplate.update("insert into profiles (id, name, role, tone) values (?, ?, ?, ?)", 404L,
                                        "Papa",
                                        "Senior", "#8A5CFF");
                }

                if (count("orders") == 0) {
                        jdbcTemplate.update(
                                        """
                                                        insert into orders (id, user_id, order_number, customer_name, medicine_name, quantity, status, payment_method, eta, pharmacy_name, fulfillment_mode)
                                                        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                                        """,
                                        501L, 1L, "MC-260223-1", "Sophie", "OBH Combi", 1, "En préparation",
                                        "Orange Money", "19 min",
                                        "Pharmacie du soleil", "Livraison");
                        jdbcTemplate.update(
                                        """
                                                        insert into orders (id, user_id, order_number, customer_name, medicine_name, quantity, status, payment_method, eta, pharmacy_name, fulfillment_mode)
                                                        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                                        """,
                                        502L, 1L, "MC-260224-2", "Ava", "Paracetamol", 2, "En route", "MTN MoMo",
                                        "32 min",
                                        "Pharmacie Centrale", "Retrait");
                }

                if (count("medical_records") == 0) {
                        jdbcTemplate.update(
                                        """
                                                        insert into medical_records (id, patient_name, allergies, treatments, vaccines, notes)
                                                        values (?, ?, ?, ?, ?, ?)
                                                        """,
                                        601L, "Sophie", "Penicilline, arachides", "Vitamine D, antihistaminique",
                                        "Calendrier a jour",
                                        "Acces temporaire actif et partage securise");
                }
        }

        private int count(String table) {
                Integer value = jdbcTemplate.queryForObject("select count(*) from " + table, Integer.class);
                return value == null ? 0 : value;
        }
}
