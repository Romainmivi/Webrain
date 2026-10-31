package altn72.tpfilrouge.modele;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "prestataire")
public class Prestataire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "num_telephone", nullable = false, length = 16)
    private String numTelephone;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    protected Prestataire() {
    }

    private Prestataire(String nom, String numTelephone, String email) {
        this.nom = Objects.requireNonNull(nom, "nom obligatoire");
        this.numTelephone = Objects.requireNonNull(numTelephone, "numéro de téléphone obligatoire");
        this.email = Objects.requireNonNull(email, "email obligatoire");
    }

    public static Prestataire creer(String nom, String numTelephone, String email) {
        return new Prestataire(nom, numTelephone, email);
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNumTelephone() {
        return numTelephone;
    }

    public void setNumTelephone(String numTelephone) {
        this.numTelephone = numTelephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }
}
