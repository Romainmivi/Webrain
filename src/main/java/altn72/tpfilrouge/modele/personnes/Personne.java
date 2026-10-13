package altn72.tpfilrouge.modele.personnes;

public abstract class Personne {

    private final int id;
    private String login;
    private String motDePasse;//à chiffrer, revoir son implémentation
    private String prenom;
    private String nom;
    private String pseudo;

    protected Personne(int id, String login, String motDePasse, String prenom, String nom, String pseudo) {
        this.id = id;
        this.login = login;
        this.motDePasse = motDePasse;
        this.prenom = prenom;
        this.nom = nom;
        this.pseudo = pseudo;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
}
