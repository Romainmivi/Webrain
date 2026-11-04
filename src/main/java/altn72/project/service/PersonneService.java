package altn72.project.service;

import altn72.project.modele.personnes.Agent;
import altn72.project.modele.personnes.AgentRepository;
import altn72.project.modele.personnes.Personne;
import altn72.project.modele.personnes.PersonneRepository;
import altn72.project.modele.personnes.Responsable;
import altn72.project.modele.personnes.ResponsableRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonneService {

    private final AgentRepository agentRepository;
    private final ResponsableRepository responsableRepository;
    private final PersonneRepository personneRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonneService(AgentRepository agentRepository, ResponsableRepository responsableRepository, PersonneRepository personneRepository, PasswordEncoder passwordEncoder) {
        this.agentRepository = agentRepository;
        this.responsableRepository = responsableRepository;
        this.personneRepository = personneRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Agent ajouterAgent(String login, String motDePasse, String prenom, String nom) {
        String motDePasseHash = passwordEncoder.encode(motDePasse);
        Agent agent = new Agent(login, motDePasseHash, prenom, nom);
        return agentRepository.save(agent);
    }

    @Transactional(readOnly = true)
    public Optional<Agent> getAgent(Integer id) {
        return agentRepository.findById(id);
    }

    @Transactional
    public Optional<Agent> modifierAgent(Integer id, String login, String prenom, String nom) {
        return agentRepository.findById(id).map(agent -> {
            agent.setLogin(login);
            agent.setPrenom(prenom);
            agent.setNom(nom);
            return agent;
        });
    }

    @Transactional
    public Responsable ajouterResponsable(String login, String motDePasse, String prenom, String nom) {
        String motDePasseHash = passwordEncoder.encode(motDePasse);
        Responsable responsable = new Responsable(login, motDePasseHash, prenom, nom);
        return responsableRepository.save(responsable);
    }

    @Transactional(readOnly = true)
    public Optional<Responsable> getResponsable(Integer id) {
        return responsableRepository.findById(id);
    }

    @Transactional
    public Optional<Responsable> modifierResponsable(Integer id, String login, String prenom, String nom) {
        return responsableRepository.findById(id).map(responsable -> {
            responsable.setLogin(login);
            responsable.setPrenom(prenom);
            responsable.setNom(nom);
            return responsable;
        });
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

    private static final String PREFIXE_BCRYPT_2A = "$2a$";
    private static final String PREFIXE_BCRYPT_2B = "$2b$";

    /**
     * Migre les mots de passe en clair vers BCrypt.
     * Un mot de passe est considéré comme déjà hashé s'il commence par "$2a$" ou "$2b$".
     * @return le nombre de mots de passe migrés
     */
    @Transactional
    public int migrerMotsDePasse() {
        List<Personne> personnes = personneRepository.findAll();
        int count = 0;
        for (Personne personne : personnes) {
            String motDePasse = personne.getMotDePasse();
            if (motDePasse != null && !motDePasse.startsWith(PREFIXE_BCRYPT_2A) && !motDePasse.startsWith(PREFIXE_BCRYPT_2B)) {
                personne.setMotDePasse(passwordEncoder.encode(motDePasse));
                personneRepository.save(personne);
                count++;
            }
        }
        return count;
    }
}
