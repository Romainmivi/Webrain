package altn72.project.service;

import altn72.project.exception.RessourceIntrouvableException;
import altn72.project.modele.Incident;
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
    private EquipementRepository equipementRepository;
    private PrestataireRepository prestataireRepository;
    private IncidentService incidentService;
    private Equipement equipement;
    private Agent agent;
    private Prestataire prestataire;

    @BeforeEach
    void setUp() {
        incidentRepository = mock(IncidentRepository.class);
        equipementRepository = mock(EquipementRepository.class);
        AgentRepository agentRepository = mock(AgentRepository.class);
        prestataireRepository = mock(PrestataireRepository.class);
        incidentService = new IncidentService(
                incidentRepository, equipementRepository, agentRepository, prestataireRepository);
        equipement = new Ascenseur(
                "Ascenseur", TypeEquipement.ASCENSEUR, null, Equipement.Etat.EN_MARCHE);
        agent = new Agent("agent", "hash", "Alex", "Martin");
        prestataire = Prestataire.creer("Prestataire", "0123456789", "test@example.com");
        when(equipementRepository.findById(1)).thenReturn(Optional.of(equipement));
        when(agentRepository.findById(2)).thenReturn(Optional.of(agent));
        when(prestataireRepository.findById(3)).thenReturn(Optional.of(prestataire));
        when(incidentRepository.save(any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void creeUnIncidentAvecOuSansPrestataire() {
        Incident avecPrestataire = incidentService.ajouterIncident(1, 2, 3, "Panne");
        Incident sansPrestataire = incidentService.ajouterIncident(1, 2, null, "Autre panne");

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
    void leveUneErreurSiUneReferenceObligatoireEstInconnue() {
        when(equipementRepository.findById(9)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                RessourceIntrouvableException.class,
                () -> incidentService.ajouterIncident(9, 2, null, "Panne"));
        verify(incidentRepository, never()).save(any(Incident.class));
    }

    @Test
    void metAJourLaDateDeClotureSeulementQuandLeStatutEstCloture() {
        Incident incident = new Incident(equipement, "Panne", LocalDateTime.now(), agent);
        when(incidentRepository.findById(4)).thenReturn(Optional.of(incident));

        incidentService.modifierIncident(4, "Réparé", Incident.Status.CLOTURE, prestataire).orElseThrow();
        assertEquals(Incident.Status.CLOTURE, incident.getStatus());
        assertSame(prestataire, incident.getPrestataire());
        assertNotNull(incident.getDateCloture());

        incidentService.modifierIncident(4, "Toujours en cours", Incident.Status.EN_COURS, null)
                .orElseThrow();
        assertEquals(Incident.Status.EN_COURS, incident.getStatus());
        assertNull(incident.getDateCloture());
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
    void retourneLeResumeDesIncidentsPourLeTableauDeBord() {
        IncidentRepository.IncidentStatusSummary summary =
                mock(IncidentRepository.IncidentStatusSummary.class);
        when(incidentRepository.summarizeByStatus()).thenReturn(summary);

        assertSame(summary, incidentService.getDashboardSummary());
        verify(incidentRepository).summarizeByStatus();
    }
}
