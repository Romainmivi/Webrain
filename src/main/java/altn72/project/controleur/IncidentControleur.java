package altn72.project.controleur;

import altn72.project.exception.RequeteInvalideException;
import altn72.project.exception.ConfirmationSurveillanceRequiseException;
import altn72.project.exception.RessourceIntrouvableException;
import altn72.project.modele.Incident;
import altn72.project.modele.IncidentEvent;
import altn72.project.modele.Prestataire;
import altn72.project.service.IncidentService;
import altn72.project.service.EquipementService;
import altn72.project.service.PersonneService;
import altn72.project.service.PrestataireService;
import altn72.project.service.PrestataireSurveillanceService;
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
@RequestMapping("/api/incidents")
public class IncidentControleur {

    private final IncidentService incidentService;
    private final EquipementService equipementService;
    private final PersonneService personneService;
    private final PrestataireService prestataireService;
    private final PrestataireSurveillanceService prestataireSurveillanceService;
    private final StationService stationService;

    public IncidentControleur(
            IncidentService incidentService,
            EquipementService equipementService,
            PersonneService personneService,
            PrestataireService prestataireService,
            PrestataireSurveillanceService prestataireSurveillanceService,
            StationService stationService
    ) {
        this.incidentService = incidentService;
        this.equipementService = equipementService;
        this.personneService = personneService;
        this.prestataireService = prestataireService;
        this.prestataireSurveillanceService = prestataireSurveillanceService;
        this.stationService = stationService;
    }

    @GetMapping("/getIncidents")
    public String getIncidents(Model model) {
        List<Incident> incidents = incidentService.getIncidents();
        model.addAttribute("incidents", incidents);
        return "incident/listeIncidents";
    }

    @GetMapping("/incident/nouveau")
    public String preparerAjoutIncident(Model model) {
        preparerFormulaireNouvelIncident(model, new IncidentRequest(null, null, null, "", false, null));
        return "incident/nouvelIncident";
    }

    @PostMapping("/addIncident")
    public String ajouterIncident(@ModelAttribute IncidentRequest request, Model model) {
        if (request.equipementId() == null) {
            throw new RequeteInvalideException("L'équipement est obligatoire");
        }
        if (request.agentId() == null) {
            throw new RequeteInvalideException("L'agent est obligatoire");
        }
        if (request.description() == null || request.description().trim().isEmpty()) {
            throw new RequeteInvalideException("La description de l'incident est obligatoire");
        }
        if (request.description().length() > 2000) {
            throw new RequeteInvalideException("La description ne peut pas dépasser 2000 caractères");
        }

        try {
            Incident incident = incidentService.ajouterIncident(
                    request.equipementId(),
                    request.agentId(),
                    request.prestataireId(),
                    request.description().trim(),
                    Boolean.TRUE.equals(request.confirmationSurveillance())
            );
            return "redirect:/api/incidents/getIncident/" + incident.getId();
        } catch (ConfirmationSurveillanceRequiseException exception) {
            preparerFormulaireNouvelIncident(model, new IncidentRequest(
                    request.equipementId(),
                    request.agentId(),
                    request.prestataireId(),
                    request.description().trim(),
                    false,
                    request.stationId()
            ));
            model.addAttribute("confirmationSurveillanceRequise", true);
            model.addAttribute("erreurSurveillance", exception.getMessage());
            return "incident/nouvelIncident";
        }
    }

    @GetMapping("/getIncident/{id}")
    public String getIncident(@PathVariable Integer id, Model model) {
        Incident incident = incidentService.getIncident(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Incident", id));
        return afficherDetails(incident, model, new IncidentUpdateRequest(
                incident.getDescription(),
                incident.getStatus(),
                incident.getPrestataire() == null ? null : incident.getPrestataire().getId(),
                false
        ));
    }

    @PutMapping("/getIncident/{id}")
    public String modifierIncident(
            @PathVariable Integer id,
            @ModelAttribute IncidentUpdateRequest request,
            Model model
    ) {
        if (request.description() == null || request.description().trim().isEmpty()) {
            throw new RequeteInvalideException("La description de l'incident est obligatoire");
        }
        if (request.description().length() > 2000) {
            throw new RequeteInvalideException("La description ne peut pas dépasser 2000 caractères");
        }
        if (request.status() == null) {
            throw new RequeteInvalideException("Le statut est obligatoire");
        }

        try {
            Prestataire prestataire = request.prestataireId() == null
                    ? null
                    : prestataireService.getPrestataire(request.prestataireId())
                            .orElseThrow(() -> new RessourceIntrouvableException(
                                    "Prestataire", request.prestataireId()));
            incidentService.modifierIncident(
                            id,
                            request.description().trim(),
                            request.status(),
                            prestataire,
                            Boolean.TRUE.equals(request.confirmationSurveillance())
                    )
                    .orElseThrow(() -> new RessourceIntrouvableException("Incident", id));
            return "redirect:/api/incidents/getIncident/" + id;
        } catch (ConfirmationSurveillanceRequiseException exception) {
            Incident incident = incidentService.getIncident(id)
                    .orElseThrow(() -> new RessourceIntrouvableException("Incident", id));
            afficherDetails(incident, model, new IncidentUpdateRequest(
                    request.description().trim(),
                    request.status(),
                    request.prestataireId(),
                    false
            ));
            model.addAttribute("confirmationSurveillanceRequise", true);
            model.addAttribute("erreurSurveillance", exception.getMessage());
            return "incident/detailsIncident";
        }
    }

    @DeleteMapping("/incident/{idIncident}")
    public String deleteIncident(@PathVariable Integer idIncident) {
        if (!incidentService.supprimerIncident(idIncident)) {
            throw new RessourceIntrouvableException("Incident", idIncident);
        }
        return "redirect:/api/incidents/getIncidents";
    }

    public record IncidentRequest(
            Integer equipementId,
            Integer agentId,
            Integer prestataireId,
            String description,
            Boolean confirmationSurveillance,
            Integer stationId
    ) {
    }

    public record IncidentUpdateRequest(
            String description,
            Incident.Status status,
            Integer prestataireId,
            Boolean confirmationSurveillance
    ) {
    }

    private void preparerFormulaireNouvelIncident(Model model, IncidentRequest request) {
        model.addAttribute("nouvelIncident", request);
        model.addAttribute("equipements", equipementService.getEquipements());
        model.addAttribute("stations", stationService.getStations());
        model.addAttribute("agents", personneService.getAgents());
        model.addAttribute("prestataires", prestataireSurveillanceService.listerPrestataires());
    }

    private String afficherDetails(Incident incident, Model model, IncidentUpdateRequest request) {
        model.addAttribute("incident", incident);
        model.addAttribute("incidentUpdate", request);
        model.addAttribute("statutsIncident", Incident.Status.values());
        model.addAttribute("prestataires", prestataireSurveillanceService.listerPrestataires());
        model.addAttribute("prestataireSousSurveillance", incident.getPrestataire() != null
                && prestataireSurveillanceService.analyser(incident.getPrestataire().getId()).sousSurveillance());
        model.addAttribute("incidentEvaluable",
                incident.getStatus() == Incident.Status.CLOTURE
                        && incident.getPrestataire() != null
                        && !incidentService.incidentEstDejaEvalue(incident.getId()));
        List<IncidentEvent> historique = incidentService.getHistorique(incident.getId());
        model.addAttribute("historique", historique);
        return "incident/detailsIncident";
    }
}
