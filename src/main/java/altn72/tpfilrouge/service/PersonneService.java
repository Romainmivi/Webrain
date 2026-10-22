package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.personnes.Agent;
import altn72.tpfilrouge.modele.personnes.AgentRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonneService {

    private final AgentRepository agentRepository;

    public PersonneService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @Transactional
    public Agent ajouterAgent(String login, String motDePasse, String prenom, String nom) {
        Agent agent = new Agent(login, motDePasse, prenom, nom);
        return agentRepository.save(agent);
    }

    @Transactional(readOnly = true)
    public Optional<Agent> getAgent(Integer id) {
        return agentRepository.findById(id);
    }
}
