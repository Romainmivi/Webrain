package altn72.project.modele.equipements;

import altn72.project.modele.Station;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ESCALIER")
public class EscaliersMecanique extends Equipement {
    protected EscaliersMecanique() {
    }

    public EscaliersMecanique(String description, TypeEquipement typeEquipement, Station station, Etat etat) {
        super(description, typeEquipement, station, etat);
    }
}
