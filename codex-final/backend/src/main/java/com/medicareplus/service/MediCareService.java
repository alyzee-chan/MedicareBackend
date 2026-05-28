package com.medicareplus.service;

import com.medicareplus.dto.*;
import com.medicareplus.model.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;

@Service
public class MediCareService {
  private final List<Doctor> doctors = new CopyOnWriteArrayList<>();
  private final List<Appointment> appointments = new CopyOnWriteArrayList<>();
  private final List<Consultation> consultations = new CopyOnWriteArrayList<>();
  private final List<Prescription> prescriptions = new CopyOnWriteArrayList<>();
  private final List<Pharmacy> pharmacies = new CopyOnWriteArrayList<>();
  private final List<DrugStock> stocks = new CopyOnWriteArrayList<>();
  private final List<Order> orders = new CopyOnWriteArrayList<>();
  private final List<SosAlert> sosAlerts = new CopyOnWriteArrayList<>();

  public MediCareService() {
    doctors.addAll(List.of(
        new Doctor("doc-1", "Dr. Kamdem", "Generaliste", "Douala", "08:30", "Disponible", false),
        new Doctor("doc-2", "Dr. Mballa", "Cardiologue", "Douala", "10:00", "Urgence", true),
        new Doctor("doc-3", "Dr. Talla", "Pediatre", "Yaounde", "13:30", "Disponible", false),
        new Doctor("doc-4", "Dr. Mbarga", "Dermatologue", "Bafoussam", "16:00", "Occupe", false),
        new Doctor("doc-5", "Dr. Abena", "Gynecologue", "Garoua", "10:00", "Disponible", false)
    ));
    pharmacies.addAll(List.of(
        new Pharmacy("pha-1", "Pharmacie Centrale Douala", "Douala", 4.0524, 9.7066, "34 min"),
        new Pharmacy("pha-2", "Pharmacie de la Paix", "Yaounde", 3.8667, 11.5167, "45 min"),
        new Pharmacy("pha-3", "PharmaPlus Bonamoussadi", "Douala", 4.0841, 9.7434, "28 min")
    ));
    stocks.addAll(List.of(
        new DrugStock("stk-1", "Paracetamol 500mg", "pha-1", "Pharmacie Centrale Douala", "Douala", 42, 1200),
        new DrugStock("stk-2", "Amoxicilline 1g", "pha-2", "Pharmacie de la Paix", "Yaounde", 18, 3500),
        new DrugStock("stk-3", "Ibuprofene 400mg", "pha-3", "PharmaPlus Bonamoussadi", "Douala", 25, 1800),
        new DrugStock("stk-4", "Vitamine C", "pha-1", "Pharmacie Centrale Douala", "Douala", 60, 900),
        new DrugStock("stk-5", "Serum oral", "pha-2", "Pharmacie de la Paix", "Yaounde", 14, 700)
    ));
  }

  public List<Doctor> doctors(String specialty, String city) {
    return doctors.stream()
        .filter(d -> specialty == null || d.specialty().equalsIgnoreCase(specialty))
        .filter(d -> city == null || d.city().equalsIgnoreCase(city))
        .toList();
  }

  public List<Appointment> appointments() {
    return appointments;
  }

  public Appointment book(AppointmentRequest request) {
    Appointment appointment = new Appointment(
        UUID.randomUUID().toString(),
        request.patient(),
        request.specialty(),
        request.city(),
        request.date(),
        request.time(),
        request.reminder(),
        "CONFIRME"
    );
    appointments.add(0, appointment);
    return appointment;
  }

  public Appointment cancelAppointment(String id) {
    return replaceAppointment(id, "ANNULE", null);
  }

  public Appointment rescheduleAppointment(String id, String time) {
    return replaceAppointment(id, "RE_PLANIFIE", time == null ? "16:00" : time);
  }

  private Appointment replaceAppointment(String id, String status, String newTime) {
    for (int i = 0; i < appointments.size(); i++) {
      Appointment current = appointments.get(i);
      if (current.id().equals(id)) {
        Appointment updated = new Appointment(
            current.id(), current.patient(), current.specialty(), current.city(),
            current.date(), newTime == null ? current.time() : newTime,
            current.reminder(), status
        );
        appointments.set(i, updated);
        return updated;
      }
    }
    throw new IllegalArgumentException("Rendez-vous introuvable");
  }

  public Consultation startConsultation(ConsultationRequest request) {
    Consultation consultation = new Consultation(
        UUID.randomUUID().toString(),
        request.patient(),
        request.doctor(),
        "SALLE_ATTENTE",
        "webrtc://secure-room/" + UUID.randomUUID(),
        new ArrayList<>(List.of("Medecin: Bonjour, decrivez vos symptomes.")),
        new ArrayList<>(),
        "",
        Instant.now()
    );
    consultations.add(0, consultation);
    return consultation;
  }

  public Consultation addMessage(String id, MessageRequest request) {
    Consultation current = findConsultation(id);
    List<String> messages = new ArrayList<>(current.messages());
    messages.add("Patient: " + request.message());
    return replaceConsultation(current, messages, current.documents(), current.report(), "EN_COURS");
  }

  public Consultation saveReport(String id, ReportRequest request) {
    Consultation current = findConsultation(id);
    return replaceConsultation(current, current.messages(), current.documents(), request.report(), "COMPTE_RENDU");
  }

  private Consultation findConsultation(String id) {
    return consultations.stream()
        .filter(c -> c.id().equals(id))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Consultation introuvable"));
  }

  private Consultation replaceConsultation(
      Consultation current,
      List<String> messages,
      List<String> documents,
      String report,
      String status
  ) {
    Consultation updated = new Consultation(
        current.id(), current.patient(), current.doctor(), status, current.roomUrl(),
        messages, documents, report, current.startedAt()
    );
    consultations.replaceAll(c -> c.id().equals(current.id()) ? updated : c);
    return updated;
  }

  public List<Prescription> prescriptions() {
    return prescriptions;
  }

  public Prescription createPrescription(PrescriptionRequest request) {
    String id = UUID.randomUUID().toString();
    Prescription prescription = new Prescription(
        id,
        request.patient(),
        request.doctor(),
        request.drug(),
        request.dosage(),
        request.pharmacy(),
        request.signature(),
        "QR-RX-" + id.substring(0, 8).toUpperCase(Locale.ROOT),
        Instant.now()
    );
    prescriptions.add(0, prescription);
    return prescription;
  }

  public List<Pharmacy> pharmacies() {
    return pharmacies;
  }

  public List<DrugStock> stocks(String query) {
    if (query == null || query.isBlank()) return stocks;
    String needle = query.toLowerCase(Locale.ROOT);
    return stocks.stream()
        .filter(s -> (s.drug() + " " + s.pharmacyName() + " " + s.city()).toLowerCase(Locale.ROOT).contains(needle))
        .toList();
  }

  public Order order(OrderRequest request) {
    Order order = new Order(
        UUID.randomUUID().toString(),
        request.drug(),
        request.pharmacyName(),
        request.price(),
        request.mobileMoneyPhone(),
        "PAIEMENT_VALIDE",
        "Commande -> Preparation -> Livreur GPS -> Livree",
        Instant.now()
    );
    orders.add(0, order);
    return order;
  }

  public List<Order> orders() {
    return orders;
  }

  public AiAdvice analyze(AiRequest request) {
    String text = request.symptoms().toLowerCase(Locale.ROOT);
    if (text.contains("thoracique") || text.contains("respire") || text.contains("essouffle")) {
      return new AiAdvice(request.symptoms(), "Activez le SOS ou contactez un medecin de garde.", "Urgences / cardiologue", true);
    }
    if (text.contains("fievre") && text.contains("toux")) {
      return new AiAdvice(request.symptoms(), "Hydratez-vous, surveillez la temperature et prenez RDV.", "Generaliste", false);
    }
    if (text.contains("enfant") || text.contains("bebe")) {
      return new AiAdvice(request.symptoms(), "Consultez rapidement si la fievre persiste.", "Pediatre", false);
    }
    if (text.contains("peau") || text.contains("eruption")) {
      return new AiAdvice(request.symptoms(), "Evitez l'automedication et montrez les lesions en consultation.", "Dermatologue", false);
    }
    return new AiAdvice(request.symptoms(), "Un medecin generaliste pourra clarifier les symptomes.", "Generaliste", false);
  }

  public SosAlert triggerSos(SosRequest request) {
    List<String> contacts = request.contacts() == null || request.contacts().isEmpty()
        ? List.of("Contact famille", "Contact ami", "Ambulance")
        : request.contacts();
    SosAlert alert = new SosAlert(
        UUID.randomUUID().toString(),
        request.patient(),
        request.latitude(),
        request.longitude(),
        "ALERTE_ACTIVE",
        contacts,
        "Dr. Mballa",
        Instant.now()
    );
    sosAlerts.add(0, alert);
    return alert;
  }

  public String deliverable(String type) {
    return switch (type) {
      case "uml" -> "Patient 1..* RendezVous; Medecin 1..* RendezVous; RendezVous 0..1 ConsultationVideo; ConsultationVideo 0..1 Ordonnance; Ordonnance 0..1 Commande; Pharmacie 1..* StockMedicament; Patient 0..* AlerteSOS.";
      case "sequence" -> "Patient choisit RDV -> Systeme programme rappel -> Salle attente -> WebRTC chiffre -> Chat/Documents -> Compte-rendu -> Ordonnance -> Pharmacie -> Paiement -> Livraison GPS.";
      default -> "MediCare+ couvre le parcours complet: rendez-vous, teleconsultation, ordonnance numerique, pharmacie connectee, assistant IA et SOS urgence.";
    };
  }
}
