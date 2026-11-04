package altn72.project.modele;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {

    @Override
    @EntityGraph(attributePaths = {"incident", "incident.equipement", "incident.agent", "incident.prestataire"})
    @NullMarked
    List<Evaluation> findAll();

    @EntityGraph(attributePaths = {"incident", "incident.equipement", "incident.agent", "incident.prestataire"})
    Optional<Evaluation> findWithDetailsById(Integer id);

    @Query("""
            select evaluation
            from Evaluation evaluation
            join fetch evaluation.incident incident
            join fetch incident.equipement
            join fetch incident.agent
            join fetch incident.prestataire prestataire
            where prestataire.id = :prestataireId
            order by evaluation.date desc
            """)
    List<Evaluation> findByPrestataireId(@Param("prestataireId") Integer prestataireId);
}
