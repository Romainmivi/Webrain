package altn72.tpfilrouge.modele.equipements;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ESCALIER")
public class EscaliersMecanique extends Equipement {
    protected EscaliersMecanique() {
    }

    public EscaliersMecanique(int id) {
        super();
    }
}
