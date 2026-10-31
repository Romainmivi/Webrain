package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.modele.Prestataire;
import altn72.tpfilrouge.service.PrestataireService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/api/prestataires")
public class PrestataireControleur {

    private final PrestataireService prestataireService;

    public PrestataireControleur(PrestataireService prestataireService) {
        this.prestataireService = prestataireService;
    }

    @GetMapping("/getPrestataires")
    public String getPrestataires(Model model) {
        List<Prestataire> prestataires = prestataireService.getPrestataires();
        model.addAttribute("prestataires", prestataires);
        return "prestataire/listePrestataires";
    }

    @GetMapping("/prestataire/nouveau")
    public String preparerAjoutPrestataire(Model model) {
        model.addAttribute("nouveauPrestataire", new PrestataireRequest("", "", ""));
        return "prestataire/nouveauPrestataire";
    }

    @PostMapping("/addPrestataire")
    public String ajouterPrestataire(@ModelAttribute PrestataireRequest request) {
        Prestataire prestataire = prestataireService.ajouterPrestataire(
                request.nom(),
                request.numTelephone(),
                request.email()
        );
        return "redirect:/api/prestataires/getPrestataire/" + prestataire.getId();
    }

    @GetMapping("/getPrestataire/{id}")
    public String getPrestataire(@PathVariable Integer id, Model model) {
        Prestataire prestataire = prestataireService.getPrestataire(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Prestataire introuvable"));
        model.addAttribute("prestataire", prestataire);
        return "prestataire/detailsPrestataire";
    }

    @DeleteMapping("/prestataire/{idPrestataire}")
    public String deletePrestataire(@PathVariable Integer idPrestataire) {
        if (!prestataireService.supprimerPrestataire(idPrestataire)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Le prestataire dont l'id est " + idPrestataire + " n'existe pas");
        }
        return "redirect:/api/prestataires/getPrestataires";
    }

    public record PrestataireRequest(String nom, String numTelephone, String email) {
    }
}
