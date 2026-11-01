package altn72.project.modele;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Integer> {
    Boolean existsByNom(String nom);
}
