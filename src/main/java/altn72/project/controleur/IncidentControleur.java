package altn72.project.controleur;

import altn72.project.modele.Incident;
import altn72.project.modele.Prestataire;
import altn72.project.service.IncidentService;
import altn72.project.service.EquipementService;
import altn72.project.service.PersonneService;
import altn72.project.service.PrestataireService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/api/incidents")
public class IncidentControleur {

    private final IncidentService incidentService;
    private final EquipementService equipementService;
    private final PersonneService personneService;
    private final PrestataireService prestataireService;

    public IncidentControleur(
            IncidentService incidentService,
            EquipementService equipementService,
            PersonneService personneService,
            PrestataireService prestataireService
    ) {
        this.incidentService = incidentService;
        this.equipementService = equipementService;
        this.personneService = personneService;
        this.prestataireService = prestataireService;
    }

    @GetMapping("/getIncidents")
    public String getIncidents(Model model) {
        List<Incident> incidents = incidentService.getIncidents();
        model.addAttribute("incidents", incidents);
        return "incident/listeIncidents";
    }

    @GetMapping("/incident/nouveau")
    public String preparerAjoutIncident(Model model) {
        model.addAttribute("nouvelIncident", new IncidentRequest(null, null, null, ""));
        model.addAttribute("equipements", equipementService.getEquipements());
        model.addAttribute("agents", personneService.getAgents());
        model.addAttribute("prestataires", prestataireService.getPrestataires());
        return "incident/nouvelIncident";
    }

    @PostMapping("/addIncident")
    public String ajouterIncident(@ModelAttribute IncidentRequest request) {
        Incident incident = incidentService.ajouterIncident(
                request.equipementId(),
                request.agentId(),
                request.prestataireId(),
                request.description()
        );
        return "redirect:/api/incidents/getIncident/" + incident.getId();
    }

    @GetMapping("/getIncident/{id}")
    public String getIncident(@PathVariable Integer id, Model model) {
        Incident incident = incidentService.getIncident(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Incident introuvable"));
        model.addAttribute("incident", incident);
        model.addAttribute("incidentUpdate", new IncidentUpdateRequest(
                incident.getDescription(),
                incident.getStatus(),
                incident.getPrestataire() == null ? null : incident.getPrestataire().getId()
        ));
        model.addAttribute("statutsIncident", Incident.Status.values());
        model.addAttribute("prestataires", prestataireService.getPrestataires());
        return "incident/detailsIncident";
    }

    @PutMapping("/getIncident/{id}")
    public String modifierIncident(
            @PathVariable Integer id,
            @ModelAttribute IncidentUpdateRequest request
    ) {
        Prestataire prestataire = request.prestataireId() == null
                ? null
                : prestataireService.getPrestataire(request.prestataireId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Prestataire introuvable"));
        incidentService.modifierIncident(
                        id,
                        request.description(),
                        request.status(),
                        prestataire
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Incident introuvable"));
        return "redirect:/api/incidents/getIncident/" + id;
    }

    @DeleteMapping("/incident/{idIncident}")
    public String deleteIncident(@PathVariable Integer idIncident) {
        if (!incidentService.supprimerIncident(idIncident)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "L'incident dont l'id est " + idIncident + " n'existe pas");
        }
        return "redirect:/api/incidents/getIncidents";
    }

    public record IncidentRequest(
            Integer equipementId,
            Integer agentId,
            Integer prestataireId,
            String description
    ) {
    }

    public record IncidentUpdateRequest(
            String description,
            Incident.Status status,
            Integer prestataireId
    ) {
    }
}
