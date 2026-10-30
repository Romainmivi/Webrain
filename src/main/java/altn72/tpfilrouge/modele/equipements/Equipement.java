package altn72.tpfilrouge.modele.equipements;

import altn72.tpfilrouge.modele.Station;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "equipement")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "categorie_equipement")
@DiscriminatorValue("EQUIPEMENT")
public class Equipement {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "type_equipement" , length = 40)
    private TypeEquipement typeEquipement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false, length = 20)
    private Etat etat;


    public Equipement(int id, String description, TypeEquipement typeEquipement, Station station, Etat etat) {
        this.id = id;
        this.description = description;
        this.station = station;
        this.typeEquipement = typeEquipement;
        this.etat = etat;
    }

    public Equipement() {

    }

    public int getId() {
        return id;
    }

    public enum Etat{
        ETEINT, EN_MARCHE, HS
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Station getStation() {
        return station;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat enFonctionnement) {
        this.etat = enFonctionnement;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public TypeEquipement getType() {
        return typeEquipement;
    }

    public void setType(TypeEquipement typeEquipement) {
        this.typeEquipement = typeEquipement;
    }

}
