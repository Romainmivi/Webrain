package altn72.project.modele.equipements;

public enum TypeEquipement {
    ASCENSEUR("Ascenseur"),
    DISTRIBUTEUR("Distributeur de titres"),
    ECRAN_INFORMATION("Écran d'information"),
    ESCALIER("Escalier mécanique"),
    PORTIQUE("Portique d'accès");

    private final String libelle;

    TypeEquipement(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
