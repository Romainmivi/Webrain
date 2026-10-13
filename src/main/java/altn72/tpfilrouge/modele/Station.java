package altn72.tpfilrouge.modele;

import altn72.tpfilrouge.modele.equipements.Equipement;

import java.util.ArrayList;
import java.util.List;

public class Station {
    private final int id;
    private final String code;
    private String nom;
    private final double latitude;
    private final double longitude;
    private String adresse;
    private String ville;
    private String zone;

    private List<Equipement> Equipements = new ArrayList<>();

    public Station(int id, String code, String nom, double latitude, double longitude, String adresse) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Station(int id, String code, String nom, double latitude, double longitude, String adresse, String ville, String zone) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adresse = adresse;
        this.ville = ville;
        this.zone = zone;
    }

    public void ajouterEquipement(Equipement e) {
        Equipements.add(e);
        e.setStation(this);
    }

    public long nbEquipementsHorsService() {
        return Equipements.stream()
                .filter(e -> e.getEtat() == Equipement.fonctionnement.HS)
                .count();
    }

    // getters / setters
    public int getId() { return id; }
    public String getCode() { return code; }
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

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public void addEquipement(Equipement equipement){
        Equipements.add(equipement);
    }

    public void addEquipements(List<Equipement> equipements){
        Equipements.addAll(equipements);
    }

    public void removeEquipementById(int id){
        //TODO
    }
}
