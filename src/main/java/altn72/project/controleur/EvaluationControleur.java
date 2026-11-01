package altn72.project.controleur;

import altn72.project.modele.Evaluation;
import altn72.project.service.EvaluationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/api/evaluations")
public class EvaluationControleur {

    private final EvaluationService evaluationService;

    public EvaluationControleur(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping("/getEvaluations")
    public String getEvaluations(Model model) {
        List<Evaluation> evaluations = evaluationService.getEvaluations();
        model.addAttribute("evaluations", evaluations);
        return "evaluation/listeEvaluations";
    }

    @GetMapping("/evaluation/nouveau")
    public String preparerAjoutEvaluation(
            @RequestParam(required = false) Integer incidentId,
            Model model
    ) {
        model.addAttribute("nouvelleEvaluation", new EvaluationRequest(incidentId, null, ""));
        model.addAttribute("incidents", evaluationService.getIncidentsEvaluables());
        return "evaluation/nouvelleEvaluation";
    }

    @PostMapping("/addEvaluation")
    public String ajouterEvaluation(@ModelAttribute EvaluationRequest request) {
        if (request.incidentId() == null || request.note() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Un incident et une note sont obligatoires");
        }
        if (request.note() < 0 || request.note() > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La note doit être comprise entre 0 et 5");
        }
        if (request.commentaire() != null && request.commentaire().length() > 1000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Le commentaire ne peut pas dépasser 1000 caractères");
        }

        Evaluation evaluation = evaluationService.ajouterEvaluation(
                        request.incidentId(),
                        request.note(),
                        request.commentaire()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "L'incident doit être clôturé et avoir un prestataire affecté"));
        return "redirect:/api/evaluations/getEvaluation/" + evaluation.getId();
    }

    @GetMapping("/getEvaluation/{id}")
    public String getEvaluation(@PathVariable Integer id, Model model) {
        Evaluation evaluation = evaluationService.getEvaluation(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Évaluation introuvable"));
        model.addAttribute("evaluation", evaluation);
        return "evaluation/detailsEvaluation";
    }

    public record EvaluationRequest(Integer incidentId, Integer note, String commentaire) {
    }
}
