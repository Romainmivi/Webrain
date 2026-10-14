package altn72.tpfilrouge.modele.personnes;

public abstract class Personne {

    private final int id;
    private String email;
    private String motDePasse;//à chiffrer, revoir son implémentation
    private String prenom;
    private String nom;
    private RolePersonne rolePersonne;

    protected Personne(int id, String email, String motDePasse, String prenom, String nom) {
        this.id = id;
        this.email = email;
        this.motDePasse = motDePasse;
        this.prenom = prenom;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return email;
    }

    public void setLogin(String login) {
        this.email = login;
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

    public RolePersonne getRole() {
        return rolePersonne;
    }

    public void setRole(RolePersonne rolePersonne) {
        this.rolePersonne = rolePersonne;
    }
}
