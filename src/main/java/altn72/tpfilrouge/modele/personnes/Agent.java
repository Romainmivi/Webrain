package altn72.tpfilrouge.modele.personnes;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("AGENT")
public class Agent extends Personne{

    protected Agent() {
    }

    protected Agent(int id, String login, String motDePasse, String prenom, String nom) {
        super(id, login, motDePasse, prenom, nom);
    }
}
