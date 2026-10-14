package altn72.tpfilrouge.modele.equipements;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PORTIQUE")
public class Portiques extends Equipement {
    protected Portiques() {
    }

    public Portiques(int id) {
        super(id);
    }
}
