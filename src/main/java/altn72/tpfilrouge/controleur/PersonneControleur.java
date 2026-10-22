package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.modele.personnes.Agent;
import altn72.tpfilrouge.service.PersonneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/personnes/agents")
public class PersonneControleur {
    private final PersonneService personneService;

    public PersonneControleur(PersonneService personneService) {
        this.personneService = personneService;
    }

    @PostMapping
    public ResponseEntity<AgentResponse> ajouterAgent(@RequestBody AgentRequest request) {
        Agent agent = personneService.ajouterAgent(
                request.login(),
                request.motDePasse(),
                request.prenom(),
                request.nom()
        );
        AgentResponse response = toResponse(agent);
        return ResponseEntity.created(URI.create("/api/personnes/agents/" + agent.getId()))
                .body(response);
    }

    @GetMapping("/{id}")
    public AgentResponse getAgent(@PathVariable Integer id) {
        Agent agent = personneService.getAgent(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Agent introuvable"));
        return toResponse(agent);
    }

    private AgentResponse toResponse(Agent agent) {
        return new AgentResponse(
                agent.getId(),
                agent.getLogin(),
                agent.getPrenom(),
                agent.getNom()
        );
    }

    public record AgentRequest(String login, String motDePasse, String prenom, String nom) {
    }

    public record AgentResponse(Integer id, String login, String prenom, String nom) {
    }
}
