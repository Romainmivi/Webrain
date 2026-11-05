package altn72.project.service;

import altn72.project.modele.EvaluationRepository;
import altn72.project.modele.Prestataire;
import altn72.project.modele.PrestataireRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class PrestataireSurveillanceServiceTest {

    private EvaluationRepository evaluationRepository;
    private PrestataireRepository prestataireRepository;
    private PrestataireSurveillanceService service;

    @BeforeEach
    void setUp() {
        evaluationRepository = mock(EvaluationRepository.class);
        prestataireRepository = mock(PrestataireRepository.class);
        service = new PrestataireSurveillanceService(evaluationRepository, prestataireRepository);
    }

    @Test
    void ignoreLaMoyenneQuandIlYAMoinsDeCinqEvaluations() {
        when(evaluationRepository.findRecentNotesByPrestataireId(1, PageRequest.of(0, 5)))
                .thenReturn(List.of(4, 1, 1, 1));

        PrestataireSurveillanceService.EtatSurveillance resultat = service.analyser(1);

        assertNull(resultat.moyenneDernieresEvaluations());
        assertFalse(resultat.sousSurveillance());
    }

    @Test
    void neSurveillePasUneMoyenneEgaleAuSeuil() {
        when(evaluationRepository.findRecentNotesByPrestataireId(2, PageRequest.of(0, 5)))
                .thenReturn(List.of(3, 3, 3, 3, 3));

        PrestataireSurveillanceService.EtatSurveillance resultat = service.analyser(2);

        assertEquals(3.0, resultat.moyenneDernieresEvaluations());
        assertFalse(resultat.sousSurveillance());
    }

    @Test
    void surveilleUneMoyenneStrictementInferieureAuSeuil() {
        when(evaluationRepository.findRecentNotesByPrestataireId(3, PageRequest.of(0, 5)))
                .thenReturn(List.of(3, 3, 3, 3, 2));

        PrestataireSurveillanceService.EtatSurveillance resultat = service.analyser(3);

        assertEquals(2.8, resultat.moyenneDernieresEvaluations());
        assertTrue(resultat.sousSurveillance());
    }

    @Test
    void neSurveillePasSiUneMauvaiseNoteEstSortieDeLaFenetreGlissante() {
        when(evaluationRepository.findRecentNotesByPrestataireId(4, PageRequest.of(0, 5)))
                .thenReturn(List.of(5, 5, 5, 5, 5));

        PrestataireSurveillanceService.EtatSurveillance resultat = service.analyser(4);

        assertEquals(5.0, resultat.moyenneDernieresEvaluations());
        assertFalse(resultat.sousSurveillance());
        verify(evaluationRepository).findRecentNotesByPrestataireId(4, PageRequest.of(0, 5));
    }

    @Test
    void inclutLeStatutDansLesOptionsDesPrestataires() {
        Prestataire surveille = mock(Prestataire.class);
        Prestataire normal = mock(Prestataire.class);
        when(surveille.getId()).thenReturn(7);
        when(surveille.getNom()).thenReturn("Surveillé");
        when(normal.getId()).thenReturn(8);
        when(normal.getNom()).thenReturn("Normal");
        when(prestataireRepository.findAll()).thenReturn(List.of(surveille, normal));
        when(evaluationRepository.findRecentNotesByPrestataireId(7, PageRequest.of(0, 5)))
                .thenReturn(List.of(1, 1, 1, 1, 1));
        when(evaluationRepository.findRecentNotesByPrestataireId(8, PageRequest.of(0, 5)))
                .thenReturn(List.of(4, 4, 4, 4, 4));

        List<PrestataireSurveillanceService.OptionPrestataire> options = service.listerPrestataires();

        assertEquals(2, options.size());
        assertEquals("Surveillé", options.getFirst().nom());
        assertTrue(options.getFirst().sousSurveillance());
        assertFalse(options.get(1).sousSurveillance());
    }
}
