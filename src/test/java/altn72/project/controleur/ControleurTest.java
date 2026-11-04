package altn72.project.controleur;

import altn72.project.service.EquipementService;
import altn72.project.service.EvaluationService;
import altn72.project.service.IncidentService;
import altn72.project.service.PersonneService;
import altn72.project.service.PrestataireService;
import altn72.project.service.StationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.ExtendedModelMap;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControleurTest {

    private EquipementService equipementService;
    private StationService stationService;
    private EvaluationService evaluationService;
    private IncidentService incidentService;
    private PersonneService personneService;
    private PrestataireService prestataireService;

    @BeforeEach
    void setUp() {
        equipementService = mock(EquipementService.class);
        stationService = mock(StationService.class);
        evaluationService = mock(EvaluationService.class);
        incidentService = mock(IncidentService.class);
        personneService = mock(PersonneService.class);
        prestataireService = mock(PrestataireService.class);
    }

    @Test
    void authControleurRetourneLesPagesEtRedirigeSelonLeRole() {
        AuthControleur controleur = new AuthControleur(personneService);

        assertEquals("login", controleur.loginPage());
        assertEquals("redirect:/login", controleur.dashboard(null));
        assertEquals("agent/dashboardAgent", controleur.dashboardAgent());
        assertEquals("responsable/dashboardResponsable", controleur.dashboardResponsable());
        assertEquals("0 mot(s) de passe migré(s) vers BCrypt.", controleur.migrerMotsDePasse());

        Authentication agent = mock(Authentication.class);
        when(agent.isAuthenticated()).thenReturn(true);
        doReturn(java.util.List.of(new SimpleGrantedAuthority("ROLE_AGENT")))
                .when(agent).getAuthorities();
        assertEquals("redirect:/dashboard/agent", controleur.dashboard(agent));

        Authentication responsable = mock(Authentication.class);
        when(responsable.isAuthenticated()).thenReturn(true);
        doReturn(java.util.List.of(new SimpleGrantedAuthority("ROLE_RESPONSABLE")))
                .when(responsable).getAuthorities();
        assertEquals("redirect:/dashboard/responsable", controleur.dashboard(responsable));
    }

    @Test
    void equipementControleurPrepareLeFormulaireEtLaListe() {
        EquipementControleur controleur = new EquipementControleur(equipementService, stationService);
        ExtendedModelMap model = new ExtendedModelMap();

        assertEquals("equipement/listeEquipements", controleur.getEquipements(model));
        assertNotNull(model.getAttribute("equipements"));
        assertEquals("equipement/nouvelEquipement", controleur.preparerAjoutEquipement(model));
        assertNotNull(model.getAttribute("nouvelEquipement"));
        assertNotNull(model.getAttribute("typesEquipement"));
        assertNotNull(model.getAttribute("etatsEquipement"));
    }

    @Test
    void evaluationControleurPrepareLeFormulaireEtLaListe() {
        EvaluationControleur controleur = new EvaluationControleur(evaluationService);
        ExtendedModelMap model = new ExtendedModelMap();

        assertEquals("evaluation/listeEvaluations", controleur.getEvaluations(model));
        assertNotNull(model.getAttribute("evaluations"));
        assertEquals("evaluation/nouvelleEvaluation", controleur.preparerAjoutEvaluation(3, model));
        assertEquals(3, ((EvaluationControleur.EvaluationRequest) Objects.requireNonNull(model.getAttribute("nouvelleEvaluation"))).incidentId());
        assertNotNull(model.getAttribute("incidents"));
    }

    @Test
    void incidentControleurPrepareLeFormulaireEtLaListe() {
        IncidentControleur controleur = new IncidentControleur(
                incidentService, equipementService, personneService, prestataireService);
        ExtendedModelMap model = new ExtendedModelMap();

        assertEquals("incident/listeIncidents", controleur.getIncidents(model));
        assertNotNull(model.getAttribute("incidents"));
        assertEquals("incident/nouvelIncident", controleur.preparerAjoutIncident(model));
        assertNotNull(model.getAttribute("nouvelIncident"));
        assertNotNull(model.getAttribute("equipements"));
        assertNotNull(model.getAttribute("agents"));
        assertNotNull(model.getAttribute("prestataires"));
    }

    @Test
    void personneControleurPrepareLesFormulairesAgentEtResponsable() {
        PersonneControleur controleur = new PersonneControleur(personneService);
        ExtendedModelMap model = new ExtendedModelMap();

        assertEquals("agent/listeAgents", controleur.getAgents(model));
        assertNotNull(model.getAttribute("agents"));
        assertEquals("agent/nouvelAgent", controleur.preparerAjoutAgent(model));
        assertNotNull(model.getAttribute("nouvelAgent"));
        assertEquals("responsable/listeResponsables", controleur.getResponsables(model));
        assertNotNull(model.getAttribute("responsables"));
        assertEquals("responsable/nouveauResponsable", controleur.preparerAjoutResponsable(model));
        assertNotNull(model.getAttribute("nouveauResponsable"));
    }

    @Test
    void prestataireControleurPrepareLeFormulaireEtLaListe() {
        PrestataireControleur controleur = new PrestataireControleur(prestataireService);
        ExtendedModelMap model = new ExtendedModelMap();

        assertEquals("prestataire/listePrestataires", controleur.getPrestataires(model));
        assertNotNull(model.getAttribute("prestataires"));
        assertEquals("prestataire/nouveauPrestataire", controleur.preparerAjoutPrestataire(model));
        assertNotNull(model.getAttribute("nouveauPrestataire"));
    }

    @Test
    void stationControleurPrepareLeFormulaireEtLaListe() {
        StationControleur controleur = new StationControleur(stationService);
        ExtendedModelMap model = new ExtendedModelMap();

        assertEquals("station/listeStations", controleur.getStations(model));
        assertNotNull(model.getAttribute("stations"));
        assertEquals("station/nouvelleStation", controleur.preparerAjoutStation(model));
        assertNotNull(model.getAttribute("nouvelleStation"));
    }
}
