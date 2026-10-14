package altn72.tpfilrouge.modele;

import altn72.tpfilrouge.modele.equipements.Equipement;
import altn72.tpfilrouge.modele.personnes.Agent;

import java.sql.Date;

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

    private final int id;
    private Equipement equipement;
    private String description;
    private Date dateOuverture;
    private Status status;
    private Prestataire prestataire;
    private Date dateCloture;
    private final Agent agent;

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
