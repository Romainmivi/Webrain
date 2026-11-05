package altn72.project.modele;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Integer> {

    @Override
    @EntityGraph(attributePaths = {"equipement", "equipement.station", "agent", "prestataire"})
    @NullMarked
    List<Incident> findAll();

    @EntityGraph(attributePaths = {"equipement", "agent", "prestataire"})
    Optional<Incident> findWithDetailsById(Integer id);

    @EntityGraph(attributePaths = {"equipement", "agent", "prestataire"})
    List<Incident> findByStatusAndPrestataireIsNotNull(Incident.Status status);

    @EntityGraph(attributePaths = {"equipement", "agent", "prestataire"})
    @Query("""
            select incident
            from Incident incident
            where incident.status = :status
              and incident.prestataire is not null
              and not exists (
                  select evaluation.id
                  from Evaluation evaluation
                  where evaluation.incident = incident
              )
            """)
    List<Incident> findEvaluablesByStatus(Incident.Status status);

    @SuppressWarnings("SqlDialectInspection")
    @Query(value = """
            SELECT
                COUNT(CASE WHEN statut = 'OUVERT' THEN 1 END) AS openCount,
                COUNT(CASE WHEN statut = 'EN_COURS' THEN 1 END) AS inProgressCount,
                COUNT(CASE WHEN statut = 'CLOTURE' THEN 1 END) AS closedCount
            FROM incident
            """, nativeQuery = true)
    IncidentStatusSummary summarizeByStatus();

    interface IncidentStatusSummary {
        Long getOpenCount();

        Long getInProgressCount();

        Long getClosedCount();
    }
}
