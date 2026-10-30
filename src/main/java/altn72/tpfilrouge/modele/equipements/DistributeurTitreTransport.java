package altn72.tpfilrouge.modele.equipements;

import altn72.tpfilrouge.modele.Station;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DISTRIBUTEUR")
public class DistributeurTitreTransport extends Equipement {
    protected DistributeurTitreTransport() {
    }

    public DistributeurTitreTransport(String description, TypeEquipement typeEquipement, Station station, Etat etat) {
        super(description, typeEquipement, station, etat);
    }
}
