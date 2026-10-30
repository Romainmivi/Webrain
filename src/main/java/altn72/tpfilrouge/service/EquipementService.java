package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Station;
import altn72.tpfilrouge.modele.StationRepository;
import altn72.tpfilrouge.modele.equipements.Equipement;
import altn72.tpfilrouge.modele.equipements.EquipementRepository;
import altn72.tpfilrouge.modele.equipements.TypeEquipement;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipementService {

    private final EquipementRepository equipementRepository;
    private final StationRepository stationRepository;

    public EquipementService(EquipementRepository equipementRepository, StationRepository stationRepository) {
        this.equipementRepository = equipementRepository;
        this.stationRepository = stationRepository;
    }

    public List<Equipement> getEquipements() {
        return equipementRepository.findAll();
    }


    public Optional<Equipement> getEquipement(Integer id) {
        return equipementRepository.findWithStationById(id);
    }

    public Equipement ajouterEquipement(String description, TypeEquipement typeEquipement, Integer idStation, Equipement.Etat etat) {
        Station station = stationRepository.findById(idStation)
                .orElseThrow(() -> new IllegalArgumentException("Station introuvable"));

        Equipement equipement = new Equipement(1, description, typeEquipement, station, etat);
        return equipementRepository.save(equipement);
    }

    public boolean supprimerEquipement(Integer idEquipement) {
        if (!equipementRepository.existsById(idEquipement)) return false;
        equipementRepository.deleteById(idEquipement);
        return true;
    }
}
