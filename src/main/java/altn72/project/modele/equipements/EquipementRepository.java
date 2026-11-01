package altn72.project.modele.equipements;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface EquipementRepository extends JpaRepository<Equipement, Integer> {
    @Override
    @EntityGraph(attributePaths = "station")
    @NullMarked
    List<Equipement> findAll();

    @EntityGraph(attributePaths = "station")
    Optional<Equipement> findWithStationById(Integer id);

}
