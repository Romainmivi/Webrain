package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.service.PersonneService;
import org.springframework.stereotype.Controller;

@Controller

public class PersonneControleur {

    private final PersonneService personneService;

    public PersonneControleur(PersonneService personneService) {
        this.personneService = personneService;
    }
}
