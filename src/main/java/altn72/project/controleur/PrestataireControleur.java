package altn72.project.controleur;

import altn72.project.exception.RessourceIntrouvableException;
import altn72.project.modele.Prestataire;
import altn72.project.service.EvaluationService;
import altn72.project.service.PrestataireService;
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
@RequestMapping("/api/prestataires")
public class PrestataireControleur {

    private final PrestataireService prestataireService;
    private final EvaluationService evaluationService;

    public PrestataireControleur(
            PrestataireService prestataireService,
            EvaluationService evaluationService
    ) {
        this.prestataireService = prestataireService;
        this.evaluationService = evaluationService;
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
                .orElseThrow(() -> new RessourceIntrouvableException("Prestataire", id));
        model.addAttribute("prestataire", prestataire);
        model.addAttribute(
                "evaluations",
                evaluationService.getEvaluationsByPrestataire(id)
        );
        return "prestataire/detailsPrestataire";
    }

    @PutMapping("/getPrestataire/{id}")
    public String modifierPrestataire(
            @PathVariable Integer id,
            @ModelAttribute PrestataireRequest request
    ) {
        prestataireService.modifierPrestataire(
                        id,
                        request.nom(),
                        request.numTelephone(),
                        request.email()
                )
                .orElseThrow(() -> new RessourceIntrouvableException("Prestataire", id));
        return "redirect:/api/prestataires/getPrestataire/" + id;
    }

    @DeleteMapping("/prestataire/{idPrestataire}")
    public String deletePrestataire(@PathVariable Integer idPrestataire) {
        if (!prestataireService.supprimerPrestataire(idPrestataire)) {
            throw new RessourceIntrouvableException("Prestataire", idPrestataire);
        }
        return "redirect:/api/prestataires/getPrestataires";
    }

    public record PrestataireRequest(String nom, String numTelephone, String email) {
    }
}
