package altn72.project.evenement;

import altn72.project.modele.Incident;
import altn72.project.modele.IncidentEvent;
import altn72.project.modele.IncidentEventRepository;
import altn72.project.modele.IncidentRepository;
import altn72.project.modele.personnes.Agent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class IncidentChangeListener {

    private final IncidentEventRepository incidentEventRepository;
    private final IncidentRepository incidentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public IncidentChangeListener(IncidentEventRepository incidentEventRepository, IncidentRepository incidentRepository) {
        this.incidentEventRepository = incidentEventRepository;
        this.incidentRepository = incidentRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    public void onIncidentChange(IncidentChangeEvent event) {
        Incident incident = incidentRepository.findById(event.getIncidentId())
                .orElse(null);

        if (incident == null && event.getChangeType() != IncidentChangeEvent.ChangeType.INCIDENT_SUPPRIME) {
            return;
        }

        IncidentEvent.EventType eventType = mapChangeType(event.getChangeType());

        IncidentEvent incidentEvent;
        if (incident != null) {
            incidentEvent = new IncidentEvent(
                    incident,
                    eventType,
                    event.getDescription(),
                    event.getDateEvenement(),
                    event.getAgent()
            );
        } else {
            // Pour la suppression, on référence l'incident par son ID via un proxy JPA
            Incident ghostIncident = entityManager.getReference(Incident.class, event.getIncidentId());
            incidentEvent = new IncidentEvent(
                    ghostIncident,
                    eventType,
                    event.getDescription(),
                    event.getDateEvenement(),
                    event.getAgent()
            );
        }

        incidentEventRepository.save(incidentEvent);
    }

    private IncidentEvent.EventType mapChangeType(IncidentChangeEvent.ChangeType changeType) {
        return switch (changeType) {
            case INCIDENT_CREE -> IncidentEvent.EventType.INCIDENT_CREE;
            case STATUT_MODIFIE -> IncidentEvent.EventType.STATUT_MODIFIE;
            case DESCRIPTION_MODIFIEE -> IncidentEvent.EventType.DESCRIPTION_MODIFIEE;
            case PRESTATAIRE_AFFECTE -> IncidentEvent.EventType.PRESTATAIRE_AFFECTE;
            case PRESTATAIRE_DESACTIVER -> IncidentEvent.EventType.PRESTATAIRE_DESACTIVER;
            case INCIDENT_SUPPRIME -> IncidentEvent.EventType.INCIDENT_SUPPRIME;
        };
    }
}
