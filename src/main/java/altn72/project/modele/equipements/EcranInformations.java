package altn72.project.modele.equipements;

import altn72.project.modele.Station;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ECRAN_INFORMATION")
public class EcranInformations extends Equipement {
    protected EcranInformations() {
    }

    public EcranInformations(String description, TypeEquipement typeEquipement, Station station, Etat etat) {
        super(description, typeEquipement, station, etat);
    }
}
