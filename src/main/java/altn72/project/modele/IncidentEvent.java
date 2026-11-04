package altn72.project.modele;

import altn72.project.modele.personnes.Agent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "incident_event")
public class IncidentEvent {

    public enum EventType {
        INCIDENT_CREE,
        STATUT_MODIFIE,
        DESCRIPTION_MODIFIEE,
        PRESTATAIRE_AFFECTE,
        PRESTATAIRE_DESACTIVER,
        INCIDENT_SUPPRIME
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_evenement", nullable = false, length = 50)
    private EventType typeEvenement;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "date_evenement", nullable = false)
    private LocalDateTime dateEvenement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    protected IncidentEvent() {
    }

    public IncidentEvent(Incident incident, EventType typeEvenement, String description, LocalDateTime dateEvenement, Agent agent) {
        this.incident = incident;
        this.typeEvenement = typeEvenement;
        this.description = description;
        this.dateEvenement = dateEvenement;
        this.agent = agent;
    }

    public Integer getId() {
        return id;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public EventType getTypeEvenement() {
        return typeEvenement;
    }

    public void setTypeEvenement(EventType typeEvenement) {
        this.typeEvenement = typeEvenement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateEvenement() {
        return dateEvenement;
    }

    public void setDateEvenement(LocalDateTime dateEvenement) {
        this.dateEvenement = dateEvenement;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}
