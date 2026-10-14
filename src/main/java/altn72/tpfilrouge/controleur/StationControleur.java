package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.service.StationService;
import org.springframework.stereotype.Controller;

@Controller

public class StationControleur {

    private final StationService stationService;

    public StationControleur(StationService stationService) {
        this.stationService = stationService;
    }
}
