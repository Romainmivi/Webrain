package altn72.tpfilrouge.modele.equipements;

import altn72.tpfilrouge.modele.Station;

public class Equipement {

    private final int id;
    private String description;
    private Type type;//peut-être un string qui vient d'une class fille d'équipement
    private Station station;
    private fonctionnement enFonctionnement = fonctionnement.ETEINT;

    public Equipement(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public enum fonctionnement{ //nom à revoir
        ETEINT, EN_MARCHE, HS
    }

    public void setStation(Station station) {
    }

    public fonctionnement getEtat() {
        return enFonctionnement;
    }

    public void setEtat(fonctionnement enFonctionnement) {
        this.enFonctionnement = enFonctionnement;
    }
}
