package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.modele.personnes.Agent;
import altn72.tpfilrouge.modele.personnes.Responsable;
import altn72.tpfilrouge.modele.Programmeur;
import altn72.tpfilrouge.service.PersonneService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
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
        return "listeAgents";
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

    @GetMapping("/agent/nouveau")
    public String preparerAjoutAgent(Model model) {
        model.addAttribute("nouvelAgent", new AgentRequest("", "", "", ""));
        return "nouvelAgent";
    }

    @GetMapping("/getAgent/{id}")
    public String getAgent(@PathVariable Integer id, Model model) {
        Agent agent = personneService.getAgent(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Agent introuvable"));
        model.addAttribute("agent", agent);
        return "detailsAgent";
    }

    public record AgentRequest(String login, String motDePasse, String prenom, String nom) {
    }


    @GetMapping("/getResponsables")
    public String getResponsables(Model model){    // Avec Thymeleaf
        List<Responsable> liste = personneService.getResponsables();
        model.addAttribute("responsables", liste);
        return "listeResponsables";
    }

    @GetMapping("/responsable/nouveau")
    public String preparerAjoutProgrammeur(Model model) {
        model.addAttribute("nouveauProgrammeur", new Programmeur());
        return "nouveauResponsable";
    }

    // A revoir
    @PostMapping("/addResponsable")
    public ResponseEntity<ResponsableResponse> ajouterResponsable(@RequestBody ResponsableRequest request) {
        Responsable responsable = personneService.ajouterResponsable(
                request.login(),
                request.motDePasse(),
                request.prenom(),
                request.nom()
        );
        ResponsableResponse response = toResponse(responsable);
        return ResponseEntity.created(URI.create("/api/personnes/responsables/" + responsable.getId()))
                .body(response);
    }

    @GetMapping("/getResponsable/{id}")
    public ResponsableResponse getResponsable(@PathVariable Integer id) {
        Responsable responsable = personneService.getResponsable(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Responsable introuvable"));
        return toResponse(responsable);
    }

    private ResponsableResponse toResponse(Responsable responsable) {
        return new ResponsableResponse(
                responsable.getId(),
                responsable.getLogin(),
                responsable.getPrenom(),
                responsable.getNom()
        );
    }

    public record ResponsableRequest(String login, String motDePasse, String prenom, String nom) {
    }

    public record ResponsableResponse(Integer id, String login, String prenom, String nom) {
    }
}
