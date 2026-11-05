package altn72.project.service;

import altn72.project.modele.EvaluationRepository;
import altn72.project.modele.Prestataire;
import altn72.project.modele.PrestataireRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrestataireSurveillanceService {

    private static final int TAILLE_FENETRE = 5;
    private static final double SEUIL_SURVEILLANCE = 3.0;

    private final EvaluationRepository evaluationRepository;
    private final PrestataireRepository prestataireRepository;

    public PrestataireSurveillanceService(
            EvaluationRepository evaluationRepository,
            PrestataireRepository prestataireRepository
    ) {
        this.evaluationRepository = evaluationRepository;
        this.prestataireRepository = prestataireRepository;
    }

    @Transactional(readOnly = true)
    public EtatSurveillance analyser(Integer prestataireId) {
        List<Integer> notes = evaluationRepository.findRecentNotesByPrestataireId(
                prestataireId,
                PageRequest.of(0, TAILLE_FENETRE)
        );
        if (notes.size() < TAILLE_FENETRE) {
            return new EtatSurveillance(prestataireId, null, false);
        }

        double moyenne = notes.stream().mapToInt(Integer::intValue).average().orElseThrow();
        return new EtatSurveillance(prestataireId, moyenne, moyenne < SEUIL_SURVEILLANCE);
    }

    @Transactional(readOnly = true)
    public List<OptionPrestataire> listerPrestataires() {
        return prestataireRepository.findAll().stream()
                .map(prestataire -> new OptionPrestataire(
                        prestataire.getId(),
                        prestataire.getNom(),
                        analyser(prestataire.getId()).sousSurveillance()
                ))
                .toList();
    }

    public record OptionPrestataire(Integer id, String nom, boolean sousSurveillance) {
    }

    public record EtatSurveillance(
            Integer id,
            Double moyenneDernieresEvaluations,
            boolean sousSurveillance
    ) {
    }
}
