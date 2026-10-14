package altn72.tpfilrouge.modele.personnes;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("RESPONSABLE")
public class Responsable extends Personne{

    protected Responsable() {
    }

    protected Responsable(int id, String login, String motDePasse, String prenom, String nom, String pseudo) {
        super(id, login, motDePasse, prenom, nom, pseudo);
    }
}
