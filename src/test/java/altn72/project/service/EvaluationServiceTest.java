package altn72.project.service;

import altn72.project.modele.Evaluation;
import altn72.project.modele.EvaluationRepository;
import altn72.project.modele.Incident;
import altn72.project.modele.IncidentRepository;
import altn72.project.modele.Prestataire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EvaluationServiceTest {

    private EvaluationRepository evaluationRepository;
    private IncidentRepository incidentRepository;
    private EvaluationService evaluationService;

    @BeforeEach
    void setUp() {
        evaluationRepository = mock(EvaluationRepository.class);
        incidentRepository = mock(IncidentRepository.class);
        evaluationService = new EvaluationService(evaluationRepository, incidentRepository);
    }

    @Test
    void ajouteUneEvaluationPourUnIncidentClotureAvecPrestataire() {
        Incident incident = mock(Incident.class);
        when(incident.getStatus()).thenReturn(Incident.Status.CLOTURE);
        when(incident.getPrestataire()).thenReturn(Prestataire.creer("Prestataire", "0123456789", "test@example.com"));
        when(incidentRepository.findWithDetailsById(12)).thenReturn(Optional.of(incident));
        when(evaluationRepository.save(any(Evaluation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        LocalDateTime avantCreation = LocalDateTime.now();

        Optional<Evaluation> resultat = evaluationService.ajouterEvaluation(12, 4, "Bon travail");

        LocalDateTime apresCreation = LocalDateTime.now();
        assertTrue(resultat.isPresent());
        Evaluation evaluation = resultat.orElseThrow();
        assertEquals(4, evaluation.getNote());
        assertEquals("Bon travail", evaluation.getCommentaire());
        assertSame(incident, evaluation.getIncident());
        assertFalse(evaluation.getDate().isBefore(avantCreation));
        assertFalse(evaluation.getDate().isAfter(apresCreation));
        verify(evaluationRepository).save(any(Evaluation.class));
    }

    @Test
    void refuseUnIncidentQuiNestPasCloture() {
        Incident incident = mock(Incident.class);
        when(incident.getStatus()).thenReturn(Incident.Status.EN_COURS);
        when(incidentRepository.findWithDetailsById(12)).thenReturn(Optional.of(incident));

        assertTrue(evaluationService.ajouterEvaluation(12, 4, null).isEmpty());
        verify(evaluationRepository, never()).save(any(Evaluation.class));
    }

    @Test
    void refuseUnIncidentSansPrestataire() {
        Incident incident = mock(Incident.class);
        when(incident.getStatus()).thenReturn(Incident.Status.CLOTURE);
        when(incident.getPrestataire()).thenReturn(null);
        when(incidentRepository.findWithDetailsById(12)).thenReturn(Optional.of(incident));

        assertTrue(evaluationService.ajouterEvaluation(12, 4, null).isEmpty());
        verify(evaluationRepository, never()).save(any(Evaluation.class));
    }

    @Test
    void rechercheLesEvaluationsParPrestataire() {
        when(evaluationRepository.findByPrestataireId(7)).thenReturn(java.util.List.of());

        assertTrue(evaluationService.getEvaluationsByPrestataire(7).isEmpty());
        verify(evaluationRepository).findByPrestataireId(7);
    }
}
