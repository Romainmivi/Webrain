package altn72.project.modele;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Integer> {

    @Override
    @EntityGraph(attributePaths = {"equipement", "agent", "prestataire"})
    @NullMarked
    List<Incident> findAll();

    @EntityGraph(attributePaths = {"equipement", "agent", "prestataire"})
    Optional<Incident> findWithDetailsById(Integer id);
}
