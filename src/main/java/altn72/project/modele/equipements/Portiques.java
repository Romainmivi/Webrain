package altn72.project.modele.equipements;

import altn72.project.modele.Station;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PORTIQUE")
public class Portiques extends Equipement {
    protected Portiques() {
    }

    public Portiques(String description, TypeEquipement typeEquipement, Station station, Etat etat) {
        super(description, typeEquipement, station, etat);
    }
}
