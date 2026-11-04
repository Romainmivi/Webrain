package altn72.project.modele;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IncidentEventRepository extends JpaRepository<IncidentEvent, Integer> {

    @Query("""
            SELECT e FROM IncidentEvent e
            WHERE e.incident.id = :incidentId
            ORDER BY e.dateEvenement DESC
            """)
    List<IncidentEvent> findByIncidentIdOrderByDateDesc(Integer incidentId);
}
