package altn72.tpfilrouge.modele;

import java.util.Objects;

public class Prestataire {

    private final int id;
    private String nom;
    private String numTelephone;
    private String email;

    private Prestataire(int id, String nom, String numTelephone, String email) {
        this.id = id;
        this.nom = Objects.requireNonNull(nom, "nom obligatoire");
        if (numTelephone == null && email == null) {
            throw new IllegalArgumentException("Au moins un moyen de contact est requis");
        }
        this.numTelephone = numTelephone;
        this.email = email;
    }

    public static Prestataire avecTelephoneEtEmail(int id, String nom, String tel, String email) {
        return new Prestataire(id, nom, Objects.requireNonNull(tel), Objects.requireNonNull(email));
    }

    public static Prestataire avecEmail(int id, String nom, String email) {
        return new Prestataire(id, nom, null, Objects.requireNonNull(email));
    }

    public static Prestataire avecTelephone(int id, String nom, String tel) {
        return new Prestataire(id, nom, Objects.requireNonNull(tel), null);
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

    public int getId() {
        return id;
    }
}
