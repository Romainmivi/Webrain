package altn72.project.modele.personnes;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("RESPONSABLE")
public class Responsable extends Personne{

    protected Responsable() {
    }

    protected Responsable(int id, String login, String motDePasse, String prenom, String nom) {
        super(id, login, motDePasse, prenom, nom);
    }

    public Responsable(String login, String motDePasse, String prenom, String nom) {
        super(null, login, motDePasse, prenom, nom);
    }
}
