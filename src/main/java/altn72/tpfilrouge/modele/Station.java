package altn72.tpfilrouge.modele;

import altn72.tpfilrouge.modele.equipements.Equipement;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "station")
public class Station {
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "adresse", nullable = false)
    private String adresse;

    @Column(name = "ville", nullable = false, length = 100)
    private String ville;

    @OneToMany(mappedBy = "station")
    private final List<Equipement> Equipements = new ArrayList<>();

    protected Station() {
    }

    public Station(int id, String nom, String adresse, String ville) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
    }

    public Station(int id, String nom, double latitude, double longitude, String adresse, String ville) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void ajouterEquipement(Equipement e) {
        Equipements.add(e);
        e.setStation(this);
    }

    public long nbEquipementsHorsService() {
        return Equipements.stream()
                .filter(e -> e.getEtat() == Equipement.Etat.HS)
                .count();
    }

    // getters / setters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public List<Equipement> getEquipements() { return Equipements; }


    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setNom(String nom){
        this.nom = nom;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public void addEquipements(List<Equipement> equipements){
        equipements.forEach(equipement -> {
            equipement.setStation(this);
            Equipements.add(equipement);
        });
    }

    public void removeEquipementById(int id){
        //TODO
    }
}
