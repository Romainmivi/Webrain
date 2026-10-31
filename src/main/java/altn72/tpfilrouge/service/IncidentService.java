package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.IncidentRepository;
import altn72.tpfilrouge.modele.Prestataire;
import altn72.tpfilrouge.modele.PrestataireRepository;
import altn72.tpfilrouge.modele.equipements.Equipement;
import altn72.tpfilrouge.modele.equipements.EquipementRepository;
import altn72.tpfilrouge.modele.personnes.Agent;
import altn72.tpfilrouge.modele.personnes.AgentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final EquipementRepository equipementRepository;
    private final AgentRepository agentRepository;
    private final PrestataireRepository prestataireRepository;

    public IncidentService(
            IncidentRepository incidentRepository,
            EquipementRepository equipementRepository,
            AgentRepository agentRepository,
            PrestataireRepository prestataireRepository
    ) {
        this.incidentRepository = incidentRepository;
        this.equipementRepository = equipementRepository;
        this.agentRepository = agentRepository;
        this.prestataireRepository = prestataireRepository;
    }

    @Transactional(readOnly = true)
    public List<Incident> getIncidents() {
        return incidentRepository.findAll();
    }

    @Transactional
    public Incident ajouterIncident(
            Integer equipementId,
            Integer agentId,
            Integer prestataireId,
            String description
    ) {
        Equipement equipement = equipementRepository.findById(equipementId)
                .orElseThrow(() -> new IllegalArgumentException("Équipement introuvable"));
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent introuvable"));
        Prestataire prestataire = prestataireId == null
                ? null
                : prestataireRepository.findById(prestataireId)
                        .orElseThrow(() -> new IllegalArgumentException("Prestataire introuvable"));

        Incident incident = new Incident(
                equipement,
                description,
                LocalDateTime.now(),
                prestataire,
                agent
        );
        return incidentRepository.save(incident);
    }

    @Transactional(readOnly = true)
    public Optional<Incident> getIncident(Integer id) {
        return incidentRepository.findWithDetailsById(id);
    }

    @Transactional
    public Optional<Incident> modifierIncident(
            Integer id,
            String description,
            Incident.Status status,
            Prestataire prestataire
    ) {
        return incidentRepository.findById(id).map(incident -> {
                Incident.Status ancienStatut = incident.getStatus();
                incident.setDescription(description);
                incident.setStatus(status);
                incident.setPrestataire(prestataire);
                if (status == Incident.Status.CLOTURE) {
                    if (ancienStatut != Incident.Status.CLOTURE || incident.getDateCloture() == null) {
                        incident.setDateCloture(LocalDateTime.now());
                    }
                } else {
                    incident.setDateCloture(null);
                }
                return incident;
            });
    }

    @Transactional
    public boolean supprimerIncident(Integer id) {
        if (!incidentRepository.existsById(id)) {
            return false;
        }
        incidentRepository.deleteById(id);
        return true;
    }
}
