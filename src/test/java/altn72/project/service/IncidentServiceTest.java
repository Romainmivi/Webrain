package altn72.project.service;

import altn72.project.exception.RessourceIntrouvableException;
import altn72.project.exception.ConfirmationSurveillanceRequiseException;
import altn72.project.exception.RegleMetierException;
import altn72.project.modele.EvaluationRepository;
import altn72.project.modele.Incident;
import altn72.project.modele.IncidentEventRepository;
import altn72.project.modele.IncidentRepository;
import altn72.project.modele.Prestataire;
import altn72.project.modele.PrestataireRepository;
import altn72.project.modele.equipements.Ascenseur;
import altn72.project.modele.equipements.Equipement;
import altn72.project.modele.equipements.EquipementRepository;
import altn72.project.modele.equipements.TypeEquipement;
import altn72.project.modele.personnes.Agent;
import altn72.project.modele.personnes.AgentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IncidentServiceTest {

    private IncidentRepository incidentRepository;
    private EvaluationRepository evaluationRepository;
    private EquipementRepository equipementRepository;
    private PrestataireRepository prestataireRepository;
    private IncidentEventRepository incidentEventRepository;
    private ApplicationEventPublisher eventPublisher;
    private PrestataireSurveillanceService prestataireSurveillanceService;
    private IncidentService incidentService;
    private Equipement equipement;
    private Agent agent;
    private Prestataire prestataire;

    @BeforeEach
    void setUp() {
        incidentRepository = mock(IncidentRepository.class);
        evaluationRepository = mock(EvaluationRepository.class);
        equipementRepository = mock(EquipementRepository.class);
        AgentRepository agentRepository = mock(AgentRepository.class);
        prestataireRepository = mock(PrestataireRepository.class);
        incidentEventRepository = mock(IncidentEventRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        prestataireSurveillanceService = mock(PrestataireSurveillanceService.class);
        incidentService = new IncidentService(
                incidentRepository, evaluationRepository, equipementRepository, agentRepository, prestataireRepository,
                incidentEventRepository, eventPublisher, prestataireSurveillanceService);
        equipement = new Ascenseur(
                "Ascenseur", TypeEquipement.ASCENSEUR, null, Equipement.Etat.EN_MARCHE);
        agent = new Agent("agent", "hash", "Alex", "Martin");
        prestataire = mock(Prestataire.class);
        when(prestataire.getId()).thenReturn(3);
        when(prestataire.getNom()).thenReturn("Prestataire");
        when(prestataireSurveillanceService.analyser(3))
                .thenReturn(new PrestataireSurveillanceService.EtatSurveillance(3, 3.5, false));
        when(equipementRepository.findById(1)).thenReturn(Optional.of(equipement));
        when(agentRepository.findById(2)).thenReturn(Optional.of(agent));
        when(prestataireRepository.findById(3)).thenReturn(Optional.of(prestataire));
        when(incidentRepository.save(any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void creeUnIncidentAvecOuSansPrestataire() {
        Incident avecPrestataire = incidentService.ajouterIncident(1, 2, 3, "Panne", false);
        Incident sansPrestataire = incidentService.ajouterIncident(1, 2, null, "Autre panne", false);

        assertSame(equipement, avecPrestataire.getEquipement());
        assertSame(agent, avecPrestataire.getAgent());
        assertSame(prestataire, avecPrestataire.getPrestataire());
        assertEquals(Incident.Status.OUVERT, avecPrestataire.getStatus());
        assertTrue(avecPrestataire.getDateOuverture().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertEquals("Autre panne", sansPrestataire.getDescription());
        assertNull(sansPrestataire.getPrestataire());
        verify(prestataireRepository, times(1)).findById(3);
    }

    @Test
    void refuseLaCreationSansConfirmationSiLePrestataireEstSousSurveillance() {
        when(prestataireSurveillanceService.analyser(3))
                .thenReturn(new PrestataireSurveillanceService.EtatSurveillance(3, 2.8, true));

        assertThrows(
                ConfirmationSurveillanceRequiseException.class,
                () -> incidentService.ajouterIncident(1, 2, 3, "Panne", false)
        );

        verify(incidentRepository, never()).save(any(Incident.class));
        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }

    @Test
    void creeUnIncidentAvecPrestataireSousSurveillanceApresConfirmation() {
        when(prestataireSurveillanceService.analyser(3))
                .thenReturn(new PrestataireSurveillanceService.EtatSurveillance(3, 2.8, true));

        Incident incident = incidentService.ajouterIncident(1, 2, 3, "Panne", true);

        assertSame(prestataire, incident.getPrestataire());
        verify(incidentRepository).save(incident);
    }

    @Test
    void leveUneErreurSiUneReferenceObligatoireEstInconnue() {
        when(equipementRepository.findById(9)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                RessourceIntrouvableException.class,
                () -> incidentService.ajouterIncident(9, 2, null, "Panne", false));
        verify(incidentRepository, never()).save(any(Incident.class));
    }

    @Test
    void metAJourLaDateDeClotureSeulementQuandLeStatutEstCloture() {
        Incident incident = new Incident(equipement, "Panne", LocalDateTime.now(), agent);
        when(incidentRepository.findById(4)).thenReturn(Optional.of(incident));

        incidentService.modifierIncident(4, "Réparé", Incident.Status.CLOTURE, prestataire, false).orElseThrow();
        assertEquals(Incident.Status.CLOTURE, incident.getStatus());
        assertSame(prestataire, incident.getPrestataire());
        assertNotNull(incident.getDateCloture());

        incidentService.modifierIncident(4, "Toujours en cours", Incident.Status.EN_COURS, null, false)
                .orElseThrow();
        assertEquals(Incident.Status.EN_COURS, incident.getStatus());
        assertNull(incident.getDateCloture());
    }

    @Test
    void refuseSansModificationUneAffectationSousSurveillanceNonConfirmee() {
        when(prestataireSurveillanceService.analyser(3))
                .thenReturn(new PrestataireSurveillanceService.EtatSurveillance(3, 2.8, true));
        Incident incident = new Incident(equipement, "Panne", LocalDateTime.now(), agent);
        when(incidentRepository.findById(6)).thenReturn(Optional.of(incident));

        assertThrows(
                ConfirmationSurveillanceRequiseException.class,
                () -> incidentService.modifierIncident(
                        6, "Nouvelle description", Incident.Status.EN_COURS, prestataire, false)
        );

        assertEquals("Panne", incident.getDescription());
        assertEquals(Incident.Status.OUVERT, incident.getStatus());
        assertNull(incident.getPrestataire());
        verify(incidentRepository, never()).save(any(Incident.class));
        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }

    @Test
    void accepteUneAffectationSousSurveillanceApresConfirmation() {
        when(prestataireSurveillanceService.analyser(3))
                .thenReturn(new PrestataireSurveillanceService.EtatSurveillance(3, 2.8, true));
        Incident incident = new Incident(equipement, "Panne", LocalDateTime.now(), agent);
        when(incidentRepository.findById(7)).thenReturn(Optional.of(incident));

        assertSame(
                incident,
                incidentService.modifierIncident(7, "Panne", Incident.Status.OUVERT, prestataire, true)
                        .orElseThrow()
        );

        assertSame(prestataire, incident.getPrestataire());
        verify(incidentRepository).save(incident);
    }

    @Test
    void refuseLaReaffectationDUnIncidentDejaEvalue() {
        Incident incident = new Incident(equipement, "Panne", LocalDateTime.now(), agent);
        incident.setPrestataire(prestataire);
        when(incidentRepository.findById(7)).thenReturn(Optional.of(incident));
        when(evaluationRepository.existsByIncident_Id(7)).thenReturn(true);
        Prestataire autrePrestataire = mock(Prestataire.class);
        when(autrePrestataire.getId()).thenReturn(4);

        assertThrows(
                RegleMetierException.class,
                () -> incidentService.modifierIncident(
                        7, "Nouvelle description", Incident.Status.EN_COURS, autrePrestataire, true)
        );

        assertEquals("Panne", incident.getDescription());
        assertEquals(Incident.Status.OUVERT, incident.getStatus());
        assertSame(prestataire, incident.getPrestataire());
        verify(incidentRepository, never()).save(any(Incident.class));
        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }

    @Test
    void refuseLaDesaffectationDUnIncidentDejaEvalue() {
        Incident incident = new Incident(equipement, "Panne", LocalDateTime.now(), agent);
        incident.setPrestataire(prestataire);
        when(incidentRepository.findById(8)).thenReturn(Optional.of(incident));
        when(evaluationRepository.existsByIncident_Id(8)).thenReturn(true);

        assertThrows(
                RegleMetierException.class,
                () -> incidentService.modifierIncident(
                        8, "Panne", Incident.Status.OUVERT, null, false)
        );

        assertSame(prestataire, incident.getPrestataire());
        verify(incidentRepository, never()).save(any(Incident.class));
    }

    @Test
    void modifieLeStatutEtGereLaDateDeCloture() {
        Incident incident = new Incident(equipement, "Panne", LocalDateTime.now(), agent);
        when(incidentRepository.findById(5)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Incident cloture = incidentService.modifierStatut(5, Incident.Status.CLOTURE);

        assertSame(incident, cloture);
        assertEquals(Incident.Status.CLOTURE, cloture.getStatus());
        assertNotNull(cloture.getDateCloture());

        Incident reouvert = incidentService.modifierStatut(5, Incident.Status.EN_COURS);

        assertEquals(Incident.Status.EN_COURS, reouvert.getStatus());
        assertNull(reouvert.getDateCloture());
        verify(incidentRepository, times(2)).save(incident);
    }

    @Test
    void signaleUnIncidentAbsentLorsDuChangementDeStatut() {
        when(incidentRepository.findById(404)).thenReturn(Optional.empty());

        assertThrows(
                RessourceIntrouvableException.class,
                () -> incidentService.modifierStatut(404, Incident.Status.CLOTURE)
        );
        verify(incidentRepository, never()).save(any(Incident.class));
    }

    @Test
    void neSupprimePasUnIncidentInexistant() {
        when(incidentRepository.existsById(99)).thenReturn(false);

        assertFalse(incidentService.supprimerIncident(99));
        verify(incidentRepository, never()).deleteById(99);
    }

    @Test
    void supprimeUnIncidentExistant() {
        Incident incident = new Incident(equipement, "Panne", LocalDateTime.now(), agent);
        when(incidentRepository.existsById(10)).thenReturn(true);
        when(incidentRepository.findWithDetailsById(10)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertTrue(incidentService.supprimerIncident(10));
        verify(incidentRepository).delete(incident);
    }

    @Test
    void retourneLeResumeDesIncidentsPourLeTableauDeBord() {
        IncidentRepository.IncidentStatusSummary summary =
                mock(IncidentRepository.IncidentStatusSummary.class);
        when(incidentRepository.summarizeByStatus()).thenReturn(summary);

        assertSame(summary, incidentService.getDashboardSummary());
        verify(incidentRepository).summarizeByStatus();
    }
}
