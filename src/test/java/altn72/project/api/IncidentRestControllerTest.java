package altn72.project.api;

import altn72.project.modele.Incident;
import altn72.project.modele.equipements.Equipement;
import altn72.project.modele.personnes.Agent;
import altn72.project.service.IncidentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IncidentRestControllerTest {

    private IncidentService incidentService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        incidentService = mock(IncidentService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new IncidentRestController(incidentService))
                .setControllerAdvice(new IncidentApiExceptionHandler())
                .build();
    }

    @Test
    void listeLesIncidentsAvecUneReponseJson() throws Exception {
        Incident incident = incident(Incident.Status.OUVERT);
        when(incidentService.getIncidents()).thenReturn(List.of(incident));

        mockMvc.perform(get("/api/v1/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(12))
                .andExpect(jsonPath("$[0].status").value("OUVERT"))
                .andExpect(jsonPath("$[0].equipementId").value(4))
                .andExpect(jsonPath("$[0].agentId").value(7));
    }

    @Test
    void retourne404PourUnIncidentInexistant() throws Exception {
        when(incidentService.getIncident(404)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/incidents/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Incident introuvable (id : 404)"));
    }

    @Test
    void modifieLeStatutEtRetourneLIncident() throws Exception {
        Incident incident = incident(Incident.Status.EN_COURS);
        when(incidentService.modifierStatut(12, Incident.Status.EN_COURS)).thenReturn(incident);

        mockMvc.perform(patch("/api/v1/incidents/12/status")
                        .contentType("application/json")
                        .content("""
                                {"status":"EN_COURS"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.status").value("EN_COURS"));

        verify(incidentService).modifierStatut(12, Incident.Status.EN_COURS);
    }

    @Test
    void refuseUnStatutManquant() throws Exception {
        mockMvc.perform(patch("/api/v1/incidents/12/status")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Le champ 'status' est obligatoire"));
    }

    private Incident incident(Incident.Status status) {
        Incident incident = mock(Incident.class);
        Equipement equipement = mock(Equipement.class);
        Agent agent = mock(Agent.class);
        when(incident.getId()).thenReturn(12);
        when(incident.getDescription()).thenReturn("Ascenseur en panne");
        when(incident.getStatus()).thenReturn(status);
        when(incident.getDateOuverture()).thenReturn(LocalDateTime.of(2026, 11, 4, 10, 0));
        when(incident.getDateCloture()).thenReturn(null);
        when(incident.getEquipement()).thenReturn(equipement);
        when(equipement.getId()).thenReturn(4);
        when(equipement.getDescription()).thenReturn("Ascenseur Nord");
        when(incident.getAgent()).thenReturn(agent);
        when(agent.getId()).thenReturn(7);
        when(incident.getPrestataire()).thenReturn(null);
        return incident;
    }
}
