package altn72.tpfilrouge.modele.equipements;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ECRAN_INFORMATION")
public class EcranInformations extends Equipement {
    protected EcranInformations() {
    }

    public EcranInformations(int id) {
        super();
    }
}
