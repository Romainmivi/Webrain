package altn72.tpfilrouge.modele.equipements;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ASCENSEUR")
public class Ascenseur extends Equipement {
    protected Ascenseur() {
    }

    public Ascenseur(int id) {
        super(id);
    }
}
