package altn72.project.service;

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
    public List<Incident> getIncidentsEvaluables() {
        return incidentRepository.findByStatusAndPrestataireIsNotNull(Incident.Status.CLOTURE);
    }

    @Transactional
    public Optional<Evaluation> ajouterEvaluation(Integer incidentId, Integer note, String commentaire) {
        Optional<Incident> incident = incidentRepository.findWithDetailsById(incidentId)
                .filter(candidate -> candidate.getStatus() == Incident.Status.CLOTURE)
                .filter(candidate -> candidate.getPrestataire() != null);

        return incident.map(candidate -> evaluationRepository.save(Evaluation.creer(
                note,
                commentaire,
                LocalDateTime.now(),
                candidate
        )));
    }
}
