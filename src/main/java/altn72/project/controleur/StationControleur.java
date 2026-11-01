package altn72.project.controleur;

import altn72.project.modele.Station;
import altn72.project.service.StationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/api/stations")

public class StationControleur {

    private final StationService stationService;

    public StationControleur(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("/getStations")
    public String getStations(Model model) {
        List<Station> stations = stationService.getStations();
        model.addAttribute("stations", stations);
        return "station/listeStations";
    }

    //Revoir le model necessaire ?
    @GetMapping("/station/nouveau")
    public String preparerAjoutStation(Model model) {
        model.addAttribute("nouvelleStation", new StationRequest("", "", ""));

        return "station/nouvelleStation";
    }

    @PostMapping("/addStation")
    public String ajouterStation(@ModelAttribute StationRequest request) throws Exception {
        Station station = stationService.ajouterStation(
                request.nom(),
                request.adresse(),
                request.ville()
        );
        return "redirect:/api/stations/getStation/" + station.getId();
    }

    @GetMapping("/getStation/{id}")
    public String getStation(@PathVariable Integer id, Model model) {
        Station station = stationService.getStation(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Station introuvable"));
        model.addAttribute("station", station);
        return "station/detailsStation";
    }

    @PutMapping("/getStation/{id}")
    public String modifierStation(@PathVariable Integer id, @ModelAttribute StationRequest request) {
        stationService.modifierStation(id, request.nom(), request.adresse(), request.ville())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Station introuvable"));
        return "redirect:/api/stations/getStation/" + id;
    }

    @DeleteMapping("/station/{idStation}")
    public String deleteStation(@PathVariable Integer idStation) {
        if (!stationService.supprimerStation(idStation)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La station dont l'id est " + idStation + " n'existe pas");
        }
        return "redirect:/api/stations/getStations";
    }

    public record StationRequest(
            String nom,
            String adresse,
            String ville
    ) {
    }
}
