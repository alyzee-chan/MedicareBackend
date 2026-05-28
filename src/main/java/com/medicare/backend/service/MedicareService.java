package com.medicare.backend.service;

import com.medicare.backend.dto.AppointmentDto;
import com.medicare.backend.dto.AppointmentRequest;
import com.medicare.backend.dto.AssistantResponse;
import com.medicare.backend.dto.DashboardResponse;
import com.medicare.backend.dto.MedicalRecordDto;
import com.medicare.backend.dto.MedicineDto;
import com.medicare.backend.dto.MetricDto;
import com.medicare.backend.dto.OrderDto;
import com.medicare.backend.dto.OrderRequest;
import com.medicare.backend.dto.PharmacyDto;
import com.medicare.backend.dto.ProfileDto;
import com.medicare.backend.dto.SosAlertRequest;
import com.medicare.backend.dto.SosAlertResponse;
import com.medicare.backend.repository.MedicareRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class MedicareService {

    private final MedicareRepository repository;

    public MedicareService(MedicareRepository repository) {
        this.repository = repository;
    }

    public DashboardResponse getDashboard() {
        List<AppointmentDto> appointments = repository.findAppointments().stream().limit(3).toList();
        List<MedicineDto> medicines = repository.findMedicines("").stream().limit(3).toList();
        List<PharmacyDto> pharmacies = repository.findPharmacies().stream().limit(3).toList();
        List<ProfileDto> profiles = repository.findProfiles();
        MedicalRecordDto medicalRecord = repository.findMedicalRecord();
        List<MetricDto> metrics = List.of(
                new MetricDto("RDV actifs", String.valueOf(appointments.size())),
                new MetricDto("Medicaments", String.valueOf(medicines.size())),
                new MetricDto("Pharmacies", String.valueOf(pharmacies.size())),
                new MetricDto("Profils", String.valueOf(profiles.size()))
        );
        return new DashboardResponse("MediCare+", metrics, appointments, medicines, pharmacies, profiles, medicalRecord);
    }

    public List<AppointmentDto> getAppointments() {
        return repository.findAppointments();
    }

    public AppointmentDto createAppointment(AppointmentRequest request) {
        return repository.createAppointment(request);
    }

    public List<MedicineDto> searchMedicines(String query) {
        return repository.findMedicines(query);
    }

    public List<PharmacyDto> getPharmacies() {
        return repository.findPharmacies();
    }

    public List<ProfileDto> getProfiles() {
        return repository.findProfiles();
    }

    public List<OrderDto> getOrders() {
        return repository.findOrders();
    }

    public OrderDto createOrder(OrderRequest request) {
        return repository.createOrder(request);
    }

    public MedicalRecordDto getMedicalRecord() {
        return repository.findMedicalRecord();
    }

    public AssistantResponse triage(String input) {
        String normalized = input == null ? "" : input.toLowerCase(Locale.ROOT);
        if (containsAny(normalized, "poitrine", "thoracique", "respirer", "allergie severe", "quincke")) {
            return new AssistantResponse(
                    input,
                    "Urgence immediate : appelez les secours et declenchez le mode SOS.",
                    "critique",
                    List.of("Ne laissez pas la personne seule.", "Activez la geolocalisation.", "Ouvrez la fiche de premiers secours.")
            );
        }
        if (containsAny(normalized, "fievre", "toux", "douleur", "maux", "rhume")) {
            return new AssistantResponse(
                    input,
                    "Consultez un medecin ou un pharmacien, puis surveillez l'evolution.",
                    "moderee",
                    List.of("Buvez de l'eau.", "Verifiez la dose.", "Demandez un avis si les symptomes persistent.")
            );
        }
        return new AssistantResponse(
                input,
                "L'app recommande un avis medical ou un questionnaire complementaire.",
                "info",
                List.of("Precisez les symptomes.", "Indiquez l'age du patient.", "Partagez une photo si besoin.")
        );
    }

    public SosAlertResponse raiseSos(SosAlertRequest request) {
        repository.saveSosAlert(request);
        List<PharmacyDto> nearby = repository.findPharmacies().stream().limit(3).toList();
        return new SosAlertResponse(
                "declenche",
                "Le SOS a ete enregistre. Les pharmacies proches sont notifiees.",
                nearby
        );
    }

    private boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
