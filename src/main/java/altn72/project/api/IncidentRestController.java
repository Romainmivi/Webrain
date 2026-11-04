package altn72.project.api;

import altn72.project.modele.Incident;
import altn72.project.exception.RequeteInvalideException;
import altn72.project.exception.RessourceIntrouvableException;
import altn72.project.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents")
@Tag(name = "Incidents", description = "Consultation et mise à jour des incidents")
public class IncidentRestController {

    private final IncidentService incidentService;

    public IncidentRestController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    @Operation(summary = "Lister les incidents")
    @ApiResponse(responseCode = "200", description = "Liste des incidents")
    public List<IncidentResponse> listerIncidents() {
        return incidentService.getIncidents().stream()
                .map(IncidentResponse::depuis)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un incident")
    @ApiResponse(responseCode = "200", description = "Incident trouvé")
    @ApiResponse(responseCode = "404", description = "Incident inexistant",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public IncidentResponse consulterIncident(@PathVariable Integer id) {
        return incidentService.getIncident(id)
                .map(IncidentResponse::depuis)
                .orElseThrow(() -> new RessourceIntrouvableException("Incident", id));
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Modifier le statut d'un incident")
    @ApiResponse(responseCode = "200", description = "Statut mis à jour")
    @ApiResponse(responseCode = "400", description = "Statut manquant ou invalide",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Incident inexistant",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public IncidentResponse modifierStatut(
            @PathVariable Integer id,
            @RequestBody ModifierStatutRequest request
    ) {
        if (request == null || request.status() == null) {
            throw new RequeteInvalideException("Le champ 'status' est obligatoire");
        }
        return IncidentResponse.depuis(incidentService.modifierStatut(id, request.status()));
    }

    @Schema(name = "ModifierStatutRequest", description = "Nouveau statut de l'incident")
    public record ModifierStatutRequest(
            @Schema(example = "EN_COURS", requiredMode = Schema.RequiredMode.REQUIRED)
            Incident.Status status
    ) {
    }

    @Schema(name = "IncidentResponse", description = "Données publiques d'un incident")
    public record IncidentResponse(
            Integer id,
            String description,
            Incident.Status status,
            LocalDateTime dateOuverture,
            LocalDateTime dateCloture,
            Integer equipementId,
            String equipementDescription,
            Integer agentId,
            Integer prestataireId
    ) {
        private static IncidentResponse depuis(Incident incident) {
            return new IncidentResponse(
                    incident.getId(),
                    incident.getDescription(),
                    incident.getStatus(),
                    incident.getDateOuverture(),
                    incident.getDateCloture(),
                    incident.getEquipement().getId(),
                    incident.getEquipement().getDescription(),
                    incident.getAgent().getId(),
                    incident.getPrestataire() == null ? null : incident.getPrestataire().getId()
            );
        }
    }
}
