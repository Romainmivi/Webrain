package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.service.IncidentService;
import org.springframework.stereotype.Controller;

@Controller

public class IncidentControleur {

    private final IncidentService incidentService;

    public IncidentControleur(IncidentService incidentService) {
        this.incidentService = incidentService;
    }
}
