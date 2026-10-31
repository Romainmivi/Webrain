package altn72.tpfilrouge.modele;

import altn72.tpfilrouge.modele.equipements.Equipement;
import altn72.tpfilrouge.modele.personnes.Agent;
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
@Table(name = "incident")
public class Incident {

    public Integer getId() {
        return id;
    }

    public Agent getAgent() {
        return agent;
    }

    public enum Status {
        OUVERT, EN_COURS, CLOTURE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipement_id", nullable = false)
    private Equipement equipement;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Column(name = "date_ouverture", nullable = false)
    private LocalDateTime dateOuverture;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 100)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestataire_id")
    private Prestataire prestataire;

    @Column(name = "date_cloture")
    private LocalDateTime dateCloture;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    protected Incident() {
    }

    public Incident(Equipement equipement, String description, LocalDateTime dateOuverture, Prestataire prestataire, Agent agent) {
        this.equipement = equipement;
        this.description = description;
        this.dateOuverture = dateOuverture;
        this.agent = agent;
        this.status = Status.OUVERT;
        this.prestataire = prestataire;
    }

    public Incident(Equipement equipement, String description, LocalDateTime dateOuverture, Agent agent) {
        this.equipement = equipement;
        this.description = description;
        this.dateOuverture = dateOuverture;
        this.agent = agent;
        this.status = Status.OUVERT;
    }

    public Equipement getEquipement() {
        return equipement;
    }

    public void setEquipement(Equipement equipement) {
        this.equipement = equipement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(LocalDateTime dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Prestataire getPrestataire() {
        return prestataire;
    }

    public void setPrestataire(Prestataire prestataire) {
        this.prestataire = prestataire;
    }

    public LocalDateTime getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(LocalDateTime dateCloture) {
        this.dateCloture = dateCloture;
    }

}
