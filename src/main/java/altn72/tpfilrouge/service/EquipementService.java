package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.equipements.EquipementRepository;
import org.springframework.stereotype.Service;

@Service
public class EquipementService {

    private final EquipementRepository equipementRepository;

    public EquipementService(EquipementRepository equipementRepository) {
        this.equipementRepository = equipementRepository;
    }
}
