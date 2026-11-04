package altn72.project.controleur;

import altn72.project.exception.RequeteInvalideException;
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
        if (request.nom() == null || request.nom().trim().isEmpty()) {
            throw new RequeteInvalideException("Le nom du prestataire est obligatoire");
        }
        if (request.numTelephone() != null && !request.numTelephone().trim().isEmpty() 
                && !request.numTelephone().trim().matches("[0-9+\\s-()]+")) {
            throw new RequeteInvalideException("Le numéro de téléphone contient des caractères invalides");
        }
        if (request.email() != null && !request.email().trim().isEmpty() 
                && !request.email().trim().contains("@")) {
            throw new RequeteInvalideException("L'adresse email n'est pas valide");
        }

        Prestataire prestataire = prestataireService.ajouterPrestataire(
                request.nom().trim(),
                request.numTelephone() == null ? null : request.numTelephone().trim(),
                request.email() == null ? null : request.email().trim()
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
        if (request.nom() == null || request.nom().trim().isEmpty()) {
            throw new RequeteInvalideException("Le nom du prestataire est obligatoire");
        }
        if (request.numTelephone() != null && !request.numTelephone().trim().isEmpty() 
                && !request.numTelephone().trim().matches("[0-9+\\s-()]+")) {
            throw new RequeteInvalideException("Le numéro de téléphone contient des caractères invalides");
        }
        if (request.email() != null && !request.email().trim().isEmpty() 
                && !request.email().trim().contains("@")) {
            throw new RequeteInvalideException("L'adresse email n'est pas valide");
        }

        prestataireService.modifierPrestataire(
                        id,
                        request.nom().trim(),
                        request.numTelephone() == null ? null : request.numTelephone().trim(),
                        request.email() == null ? null : request.email().trim()
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
