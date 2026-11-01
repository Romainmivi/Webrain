package altn72.project.modele.equipements;

import altn72.project.modele.Station;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ASCENSEUR")
public class Ascenseur extends Equipement {
    protected Ascenseur() {
    }

    public Ascenseur(String description, TypeEquipement typeEquipement, Station station, Etat etat) {
        super(description, typeEquipement, station, etat);
    }
}
