package altn72.project.evenement;

import altn72.project.modele.personnes.Agent;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

public class IncidentChangeEvent extends ApplicationEvent {

    public enum ChangeType {
        INCIDENT_CREE,
        STATUT_MODIFIE,
        DESCRIPTION_MODIFIEE,
        PRESTATAIRE_AFFECTE,
        PRESTATAIRE_DESACTIVER,
        INCIDENT_SUPPRIME
    }

    private final Integer incidentId;
    private final String incidentDescription;
    private final ChangeType changeType;
    private final String description;
    private final LocalDateTime dateEvenement;
    private final Agent agent;

    public IncidentChangeEvent(Object source, Integer incidentId, String incidentDescription, ChangeType changeType, String description, LocalDateTime dateEvenement, Agent agent) {
        super(source);
        this.incidentId = incidentId;
        this.incidentDescription = incidentDescription;
        this.changeType = changeType;
        this.description = description;
        this.dateEvenement = dateEvenement;
        this.agent = agent;
    }

    public Integer getIncidentId() {
        return incidentId;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateEvenement() {
        return dateEvenement;
    }

    public Agent getAgent() {
        return agent;
    }
}
