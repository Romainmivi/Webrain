package altn72.tpfilrouge.modele.equipements;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DISTRIBUTEUR")
public class DistributeurTitreTransport extends Equipement {
    protected DistributeurTitreTransport() {
    }

    public DistributeurTitreTransport(int id) {
        super();
    }
}
