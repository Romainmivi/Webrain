package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.personnes.Agent;
import altn72.tpfilrouge.modele.personnes.AgentRepository;
import altn72.tpfilrouge.modele.personnes.Responsable;
import altn72.tpfilrouge.modele.personnes.ResponsableRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonneService {

    private final AgentRepository agentRepository;
    private final ResponsableRepository responsableRepository;

    public PersonneService(AgentRepository agentRepository, ResponsableRepository responsableRepository) {
        this.agentRepository = agentRepository;
        this.responsableRepository = responsableRepository;
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


    @Transactional
    public Responsable ajouterResponsable(String login, String s, String prenom, String nom) {
        Responsable responsable = new Responsable(login, s, prenom, nom);
        return responsableRepository.save(responsable);
    }

    @Transactional(readOnly = true)
    public Optional<Responsable> getResponsable(Integer id) {
        return responsableRepository.findById(id);
    }

    public List<Responsable> getResponsables() {
        return responsableRepository.findAll();
    }

    public List<Agent> getAgents() {
        return agentRepository.findAll();
    }

    @Transactional
    public boolean supprimerAgent(Integer idAgent) {
        if (!agentRepository.existsById(idAgent)) return false;
        agentRepository.deleteById(idAgent);
        return true;
    }

    public boolean supprimerResponsable(Integer idResponsable) {
        if (!responsableRepository.existsById(idResponsable)) return false;
        responsableRepository.deleteById(idResponsable);
        return true;
    }
}
