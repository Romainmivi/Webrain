package altn72.tpfilrouge.modele;

import altn72.tpfilrouge.modele.equipements.Equipement;
import altn72.tpfilrouge.modele.personnes.Agent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.sql.Date;

@Entity
@Table(name = "incident")
public class Incident {

    public int getId() {
        return id;
    }

    public Agent getAgent() {
        return agent;
    }

    public enum Status {
        OUVERT, EN_COURS, CLOTURE
    }

    @Id
    @Column(name = "id_incident", nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_equipement", nullable = false)
    private Equipement equipement;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "date_ouverture", nullable = false)
    private Date dateOuverture;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prestataire")
    private Prestataire prestataire;

    @Column(name = "date_cloture")
    private Date dateCloture;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agent", nullable = false)
    private Agent agent;

    protected Incident() {
    }

    public Incident(int id, Equipement equipement, String description, Date dateOuverture, Prestataire prestataire, Agent agent) {
        this.id = id;
        this.equipement = equipement;
        this.description = description;
        this.dateOuverture = dateOuverture;
        this.agent = agent;
        this.status = Status.OUVERT;
        this.prestataire = prestataire;
    }

    public Incident(int id, Equipement equipement, String description, Date dateOuverture, Agent agent) {
        this.id = id;
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

    public Date getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(Date dateOuverture) {
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

    public Date getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(Date dateCloture) {
        this.dateCloture = dateCloture;
    }


}
