package altn72.project.service;

import altn72.project.exception.RessourceDejaExistanteException;
import altn72.project.exception.RegleMetierException;
import altn72.project.modele.Station;
import altn72.project.modele.StationRepository;
import altn72.project.modele.equipements.EquipementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {

    private final StationRepository stationRepository;
    private final EquipementRepository equipementRepository;

    public StationService(StationRepository stationRepository, EquipementRepository equipementRepository) {
        this.stationRepository = stationRepository;
        this.equipementRepository = equipementRepository;
    }

    public List<Station> getStations() {
        return stationRepository.findAll();
    }

    public Station ajouterStation(String nom, String adresse, String ville) {
        if (stationRepository.existsByNom(nom)) {
            throw new RessourceDejaExistanteException("Station", "nom", nom);
        }
        Station station = new Station(1, nom, adresse, ville);
        return stationRepository.save(station);

    }

    public Optional<Station> getStation(Integer id) {
        return stationRepository.findById(id);
    }

    @Transactional
    public Optional<Station> modifierStation(Integer id, String nom, String adresse, String ville) {
        return stationRepository.findById(id).map(station -> {
            station.setNom(nom);
            station.setAdresse(adresse);
            station.setVille(ville);
            return station;
        });
    }

    public boolean supprimerStation(Integer idStation) {
        if (!stationRepository.existsById(idStation)) return false;
        long count = equipementRepository.countByStationId(idStation);
        if (count > 0) {
            throw new RegleMetierException(
                "Impossible de supprimer la station : elle possède " + count + " équipement(s) associé(s). Supprimez d'abord les équipements."
            );
        }
        stationRepository.deleteById(idStation);
        return true;
    }
}
