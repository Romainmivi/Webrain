package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.service.PrestataireService;
import org.springframework.stereotype.Controller;

@Controller

public class PrestataireControleur {

    private final PrestataireService prestataireService;

    public PrestataireControleur(PrestataireService prestataireService) {
        this.prestataireService = prestataireService;
    }
}
