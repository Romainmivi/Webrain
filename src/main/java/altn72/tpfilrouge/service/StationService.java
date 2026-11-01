package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Station;
import altn72.tpfilrouge.modele.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<Station> getStations() {
        return stationRepository.findAll();
    }

    public Station ajouterStation(String nom, String adresse, String ville) throws Exception {
        if(stationRepository.existsByNom(nom)){
            throw new Exception();
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
        stationRepository.deleteById(idStation);
        return true;
    }
}
