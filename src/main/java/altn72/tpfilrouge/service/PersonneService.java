package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.personnes.PersonneRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonneService {

    private final PersonneRepository personneRepository;

    public PersonneService(PersonneRepository personneRepository) {
        this.personneRepository = personneRepository;
    }
}
