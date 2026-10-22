package altn72.tpfilrouge.modele.equipements;

import altn72.tpfilrouge.modele.Station;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "equipement")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "categorie_equipement")
@DiscriminatorValue("EQUIPEMENT")
public class Equipement {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_id", length = 40)
    private TypeEquipement typeEquipement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false, length = 20)
    private Etat enFonctionnement = Etat.ETEINT;

    protected Equipement() {
    }

    public Equipement(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public enum Etat{ //nom à revoir
        ETEINT, EN_MARCHE, HS
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Etat getEtat() {
        return enFonctionnement;
    }

    public void setEtat(Etat enFonctionnement) {
        this.enFonctionnement = enFonctionnement;
    }
}
