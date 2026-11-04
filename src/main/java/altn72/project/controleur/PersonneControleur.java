package altn72.project.controleur;

import altn72.project.exception.RequeteInvalideException;
import altn72.project.exception.RessourceIntrouvableException;
import altn72.project.modele.personnes.Agent;
import altn72.project.modele.personnes.Responsable;
import altn72.project.service.PersonneService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Controller
@RequestMapping("/api/personnes")
public class PersonneControleur {
    private final PersonneService personneService;

    public PersonneControleur(PersonneService personneService) {
        this.personneService = personneService;
    }

    @GetMapping("/getAgents")
    public String getAgents(Model model) {
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
        if (request.login() == null || request.login().trim().isEmpty()) {
            throw new RequeteInvalideException("Le login est obligatoire");
        }
        if (request.motDePasse() == null || request.motDePasse().trim().isEmpty()) {
            throw new RequeteInvalideException("Le mot de passe est obligatoire");
        }
        if (request.motDePasse().length() < 4) {
            throw new RequeteInvalideException("Le mot de passe doit contenir au moins 4 caractères");
        }
        if (request.prenom() == null || request.prenom().trim().isEmpty()) {
            throw new RequeteInvalideException("Le prénom est obligatoire");
        }
        if (request.nom() == null || request.nom().trim().isEmpty()) {
            throw new RequeteInvalideException("Le nom est obligatoire");
        }

        Agent agent = personneService.ajouterAgent(
                request.login().trim(),
                request.motDePasse(),
                request.prenom().trim(),
                request.nom().trim()
        );
        return "redirect:/api/personnes/getAgent/" + agent.getId();
    }

    @GetMapping("/getAgent/{id}")
    public String getAgent(@PathVariable Integer id, Model model) {
        Agent agent = personneService.getAgent(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Agent", id));
        model.addAttribute("agent", agent);
        return "agent/detailsAgent";
    }

    @PutMapping("/getAgent/{id}")
    public String modifierAgent(@PathVariable Integer id, @ModelAttribute PersonneRequest request) {
        if (request.login() == null || request.login().trim().isEmpty()) {
            throw new RequeteInvalideException("Le login est obligatoire");
        }
        if (request.prenom() == null || request.prenom().trim().isEmpty()) {
            throw new RequeteInvalideException("Le prénom est obligatoire");
        }
        if (request.nom() == null || request.nom().trim().isEmpty()) {
            throw new RequeteInvalideException("Le nom est obligatoire");
        }

        personneService.modifierAgent(id, request.login().trim(), request.prenom().trim(), request.nom().trim())
                .orElseThrow(() -> new RessourceIntrouvableException("Agent", id));
        return "redirect:/api/personnes/getAgent/" + id;
    }

    @DeleteMapping("/agent/{idAgent}")
    public String deleteAgent(@PathVariable("idAgent") Integer idAgent) {
        if (!personneService.supprimerAgent(idAgent)) {
            throw new RessourceIntrouvableException("Agent", idAgent);
        }
        return "redirect:/api/personnes/getAgents";
    }

    public record AgentRequest(String login, String motDePasse, String prenom, String nom) {
    }

    @GetMapping("/getResponsables")
    public String getResponsables(Model model) {
        List<Responsable> liste = personneService.getResponsables();
        model.addAttribute("responsables", liste);
        return "responsable/listeResponsables";
    }

    @GetMapping("/responsable/nouveau")
    public String preparerAjoutResponsable(Model model) {
        model.addAttribute("nouveauResponsable", new ResponsableRequest("","", "", ""));
        return "responsable/nouveauResponsable";
    }

    @PostMapping("/addResponsable")
    public String ajouterResponsable(@ModelAttribute ResponsableRequest request) {
        if (request.login() == null || request.login().trim().isEmpty()) {
            throw new RequeteInvalideException("Le login est obligatoire");
        }
        if (request.motDePasse() == null || request.motDePasse().trim().isEmpty()) {
            throw new RequeteInvalideException("Le mot de passe est obligatoire");
        }
        if (request.motDePasse().length() < 4) {
            throw new RequeteInvalideException("Le mot de passe doit contenir au moins 4 caractères");
        }
        if (request.prenom() == null || request.prenom().trim().isEmpty()) {
            throw new RequeteInvalideException("Le prénom est obligatoire");
        }
        if (request.nom() == null || request.nom().trim().isEmpty()) {
            throw new RequeteInvalideException("Le nom est obligatoire");
        }

        Responsable responsable = personneService.ajouterResponsable(
                request.login().trim(),
                request.motDePasse(),
                request.prenom().trim(),
                request.nom().trim()
        );
        return "redirect:/api/personnes/getResponsable/" + responsable.getId();
    }

    @GetMapping("/getResponsable/{id}")
    public String getResponsable(@PathVariable Integer id, Model model) {
        Responsable responsable = personneService.getResponsable(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Responsable", id));
        model.addAttribute("responsable", responsable);
        return "responsable/detailsResponsable";
    }

    @PutMapping("/getResponsable/{id}")
    public String modifierResponsable(@PathVariable Integer id, @ModelAttribute PersonneRequest request) {
        if (request.login() == null || request.login().trim().isEmpty()) {
            throw new RequeteInvalideException("Le login est obligatoire");
        }
        if (request.prenom() == null || request.prenom().trim().isEmpty()) {
            throw new RequeteInvalideException("Le prénom est obligatoire");
        }
        if (request.nom() == null || request.nom().trim().isEmpty()) {
            throw new RequeteInvalideException("Le nom est obligatoire");
        }

        personneService.modifierResponsable(id, request.login().trim(), request.prenom().trim(), request.nom().trim())
                .orElseThrow(() -> new RessourceIntrouvableException("Responsable", id));
        return "redirect:/api/personnes/getResponsable/" + id;
    }

    @DeleteMapping("/responsable/{idResponsable}")
    public String deleteResponsable(@PathVariable("idResponsable") Integer idResponsable) {
        if (!personneService.supprimerResponsable(idResponsable)) {
            throw new RessourceIntrouvableException("Responsable", idResponsable);
        }
        return "redirect:/api/personnes/getResponsables";
    }

    public record ResponsableRequest(String login, String motDePasse, String prenom, String nom) {
    }

    public record PersonneRequest(String login, String prenom, String nom) {
    }

}
