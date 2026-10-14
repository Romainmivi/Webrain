package altn72.tpfilrouge.modele.equipements;

import altn72.tpfilrouge.modele.Station;

public class Equipement implements EquipementRepository {

    private final int id;
    private String description;
    private TypeEquipement typeEquipement;
    private Station station;
    private Etat enFonctionnement = Etat.ETEINT;

    public Equipement(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public enum Etat{ //nom à revoir
        ETEINT, EN_MARCHE, HS
    }

    public void setStation(Station station) {
    }

    public Etat getEtat() {
        return enFonctionnement;
    }

    public void setEtat(Etat enFonctionnement) {
        this.enFonctionnement = enFonctionnement;
    }
}
