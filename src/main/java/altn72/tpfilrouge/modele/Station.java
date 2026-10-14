package altn72.tpfilrouge.modele;

import altn72.tpfilrouge.modele.equipements.Equipement;

import java.util.ArrayList;
import java.util.List;

public class Station {
    private final int id;
    private String nom;
    private double latitude;
    private double longitude;
    private final String adresse;
    private String ville;

    private final List<Equipement> Equipements = new ArrayList<>();


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
