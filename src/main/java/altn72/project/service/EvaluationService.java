package altn72.project.service;

import altn72.project.exception.RegleMetierException;
import altn72.project.exception.RessourceIntrouvableException;
import altn72.project.modele.Evaluation;
import altn72.project.modele.EvaluationRepository;
import altn72.project.modele.Incident;
import altn72.project.modele.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final IncidentRepository incidentRepository;

    public EvaluationService(
            EvaluationRepository evaluationRepository,
            IncidentRepository incidentRepository
    ) {
        this.evaluationRepository = evaluationRepository;
        this.incidentRepository = incidentRepository;
    }

    @Transactional(readOnly = true)
    public List<Evaluation> getEvaluations() {
        return evaluationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Evaluation> getEvaluation(Integer id) {
        return evaluationRepository.findWithDetailsById(id);
    }

    @Transactional(readOnly = true)
    public List<Evaluation> getEvaluationsByPrestataire(Integer prestataireId) {
        return evaluationRepository.findByPrestataireId(prestataireId);
    }

    @Transactional(readOnly = true)
    public List<Incident> getIncidentsEvaluables() {
        return incidentRepository.findByStatusAndPrestataireIsNotNull(Incident.Status.CLOTURE);
    }

    @Transactional
    public Evaluation ajouterEvaluation(Integer incidentId, Integer note, String commentaire) {
        Incident incident = incidentRepository.findWithDetailsById(incidentId)
                .orElseThrow(() -> new RessourceIntrouvableException("Incident", incidentId));
        if (incident.getStatus() != Incident.Status.CLOTURE || incident.getPrestataire() == null) {
            throw new RegleMetierException(
                    "Une évaluation exige un incident clôturé avec un prestataire affecté");
        }

        return evaluationRepository.save(Evaluation.creer(
                note,
                commentaire,
                LocalDateTime.now(),
                incident
        ));
    }
}
