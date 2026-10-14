package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.service.EquipementService;
import org.springframework.stereotype.Controller;

@Controller

public class EquipementControleur {

    private final EquipementService equipementService;

    public EquipementControleur(EquipementService equipementService) {
        this.equipementService = equipementService;
    }
}
