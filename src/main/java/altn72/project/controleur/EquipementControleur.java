package altn72.project.controleur;

import altn72.project.exception.RequeteInvalideException;
import altn72.project.exception.RessourceIntrouvableException;
import altn72.project.modele.Station;
import altn72.project.modele.equipements.Equipement;
import altn72.project.modele.equipements.TypeEquipement;
import altn72.project.service.EquipementService;
import altn72.project.service.StationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/equipements")
public class EquipementControleur {

    private final EquipementService equipementService;
    private final StationService stationService;

    public EquipementControleur(EquipementService equipementService, StationService stationService) {
        this.equipementService = equipementService;
        this.stationService = stationService;
    }

    @GetMapping("/getEquipements")
    public String getEquipements(Model model) {
        List<Equipement> equipements = equipementService.getEquipements();
        model.addAttribute("equipements", equipements);
        return "equipement/listeEquipements";
    }

    @GetMapping("/equipement/nouveau")
    public String preparerAjoutEquipement(Model model) {
        model.addAttribute("nouvelEquipement", new EquipementRequest("", null, null, null));
        model.addAttribute("typesEquipement", TypeEquipement.values());
        model.addAttribute("stationsDisponible", stationService.getStations());
        model.addAttribute("etatsEquipement", Equipement.Etat.values());
        return "equipement/nouvelEquipement";
    }

    @PostMapping("/addEquipement")
    public String ajouterEquipement(@ModelAttribute EquipementRequest request) {
        if (request.description() == null || request.description().trim().isEmpty()) {
            throw new RequeteInvalideException("La description de l'équipement est obligatoire");
        }
        if (request.typeEquipement() == null) {
            throw new RequeteInvalideException("Le type d'équipement est obligatoire");
        }
        if (request.idStation() == null) {
            throw new RequeteInvalideException("La station est obligatoire");
        }

        Equipement equipement = equipementService.ajouterEquipement(
                request.description().trim(),
                request.typeEquipement(),
                request.idStation(),
                request.etat()
        );
        return "redirect:/api/equipements/getEquipement/" + equipement.getId();
    }

    @GetMapping("/getEquipement/{id}")
    public String getEquipement(@PathVariable Integer id, Model model) {
        Equipement equipement = equipementService.getEquipement(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Équipement", id));
        model.addAttribute("equipement", equipement);
        model.addAttribute("equipementUpdate", new EquipementUpdateRequest(
                equipement.getDescription(),
                equipement.getStation().getId(),
                equipement.getEtat()
        ));
        model.addAttribute("stationsDisponible", stationService.getStations());
        model.addAttribute("etatsEquipement", Equipement.Etat.values());
        return "equipement/detailsEquipement";
    }

    @PutMapping("/getEquipement/{id}")
    public String modifierEquipement(
            @PathVariable Integer id,
            @ModelAttribute EquipementUpdateRequest request
    ) {
        if (request.description() == null || request.description().trim().isEmpty()) {
            throw new RequeteInvalideException("La description de l'équipement est obligatoire");
        }
        if (request.idStation() == null) {
            throw new RequeteInvalideException("La station est obligatoire");
        }

        Station station = stationService.getStation(request.idStation())
                .orElseThrow(() -> new RessourceIntrouvableException("Station", request.idStation()));
        equipementService.modifierEquipement(id, request.description().trim(), station, request.etat())
                .orElseThrow(() -> new RessourceIntrouvableException("Équipement", id));
        return "redirect:/api/equipements/getEquipement/" + id;
    }

    @DeleteMapping("/equipement/{idEquipement}")
    public String deleteEquipement(@PathVariable Integer idEquipement) {
        if (!equipementService.supprimerEquipement(idEquipement)) {
            throw new RessourceIntrouvableException("Équipement", idEquipement);
        }
        return "redirect:/api/equipements/getEquipements";
    }

    public record EquipementRequest(
            String description,
            TypeEquipement typeEquipement,
            Integer idStation,
            Equipement.Etat etat
    ) {
    }

    public record EquipementUpdateRequest(
            String description,
            Integer idStation,
            Equipement.Etat etat
    ) {
    }

}
