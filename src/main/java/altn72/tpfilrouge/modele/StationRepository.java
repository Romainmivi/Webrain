package altn72.tpfilrouge.modele;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Integer> {
    Boolean existsByNom(String nom);
}
