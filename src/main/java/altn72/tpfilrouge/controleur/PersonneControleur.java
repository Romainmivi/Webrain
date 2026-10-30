package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.modele.personnes.Agent;
import altn72.tpfilrouge.modele.personnes.Responsable;
import altn72.tpfilrouge.service.PersonneService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping("/api/personnes")
public class PersonneControleur {
    private final PersonneService personneService;

    public PersonneControleur(PersonneService personneService) {
        this.personneService = personneService;
    }

    @GetMapping("/getAgents")
    public String getAgents(Model model){    // Avec Thymeleaf
        List<Agent> liste = personneService.getAgents();
        model.addAttribute("agents", liste);
        return "agent/listeAgents";
    }

    @GetMapping("/agent/nouveau")
    public String preparerAjoutAgent(Model model) {
        model.addAttribute("nouvelAgent", new AgentRequest("", "", "", ""));
        return "agent/nouvelAgent";
    }

    @PostMapping("/addAgent")
    public String ajouterAgent(@ModelAttribute AgentRequest request) {
        Agent agent = personneService.ajouterAgent(
                request.login(),
                request.motDePasse(),
                request.prenom(),
                request.nom()
        );
        return "redirect:/api/personnes/getAgent/" + agent.getId();
    }

    @GetMapping("/getAgent/{id}")
    public String getAgent(@PathVariable Integer id, Model model) {
        Agent agent = personneService.getAgent(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Agent introuvable"));
        model.addAttribute("agent", agent);
        return "agent/detailsAgent";
    }

    @DeleteMapping("/agent/{idAgent}")
    public String deleteAgent(@PathVariable("idAgent") Integer idAgent) {
        if (!personneService.supprimerAgent(idAgent)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "L'agent dont l'id est " + idAgent + " n'existe pas");
        }
        return "redirect:/api/personnes/getAgents";
    }

    public record AgentRequest(String login, String motDePasse, String prenom, String nom) {
    }


    @GetMapping("/getResponsables")
    public String getResponsables(Model model){    // Avec Thymeleaf
        List<Responsable> liste = personneService.getResponsables();
        model.addAttribute("responsables", liste);
        return "responsable/listeResponsables";
    }

    @GetMapping("/responsable/nouveau")
    public String preparerAjoutProgrammeur(Model model) {
        model.addAttribute("nouveauResponsable", new ResponsableRequest("","", "", ""));
        return "responsable/nouveauResponsable";
    }

    // A revoir
    @PostMapping("/addResponsable")
    public String ajouterResponsable(@ModelAttribute AgentRequest request) {
        Responsable responsable = personneService.ajouterResponsable(
                request.login(),
                request.motDePasse(),
                request.prenom(),
                request.nom()
        );
        return "redirect:/api/personnes/getResponsable/" + responsable.getId();
    }

    @GetMapping("/getResponsable/{id}")
    public String getResponsable(@PathVariable Integer id, Model model) {
        Responsable responsable = personneService.getResponsable(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Responsable introuvable"));
        model.addAttribute("responsable", responsable);
        return "responsable/detailsResponsable";
    }

    @DeleteMapping("/responsable/{idResponsable}")
    public String deleteResponsable(@PathVariable("idResponsable") Integer idResponsable) {
        if (!personneService.supprimerResponsable(idResponsable)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "L'agent dont l'id est " + idResponsable + " n'existe pas");
        }
        return "redirect:/api/personnes/getResponsables";
    }

    public record ResponsableRequest(String login, String motDePasse, String prenom, String nom) {
    }

}
