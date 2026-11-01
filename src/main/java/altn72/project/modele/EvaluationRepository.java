package altn72.project.modele;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {

    @Override
    @EntityGraph(attributePaths = {"incident", "incident.equipement", "incident.agent", "incident.prestataire"})
    @NullMarked
    List<Evaluation> findAll();

    @EntityGraph(attributePaths = {"incident", "incident.equipement", "incident.agent", "incident.prestataire"})
    Optional<Evaluation> findWithDetailsById(Integer id);
}
