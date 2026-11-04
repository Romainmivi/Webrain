package altn72.project.service;

import altn72.project.modele.personnes.Agent;
import altn72.project.modele.personnes.AgentRepository;
import altn72.project.modele.personnes.PersonneRepository;
import altn72.project.modele.personnes.ResponsableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersonneServiceTest {

    private AgentRepository agentRepository;
    private PersonneRepository personneRepository;
    private PasswordEncoder passwordEncoder;
    private PersonneService personneService;

    @BeforeEach
    void setUp() {
        agentRepository = mock(AgentRepository.class);
        personneRepository = mock(PersonneRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        personneService = new PersonneService(
                agentRepository,
                mock(ResponsableRepository.class),
                personneRepository,
                passwordEncoder
        );
    }

    @Test
    void creeUnAgentAvecUnMotDePasseEncode() {
        when(passwordEncoder.encode("mot-de-passe")).thenReturn("hash");
        when(agentRepository.save(any(Agent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Agent agent = personneService.ajouterAgent("agent", "mot-de-passe", "Alex", "Martin");

        assertEquals("hash", agent.getMotDePasse());
        assertEquals("agent", agent.getLogin());
        verify(passwordEncoder).encode("mot-de-passe");
        verify(agentRepository).save(agent);
    }

    @Test
    void migreSeulementLesMotsDePasseQuiNeSontPasDejaHashes() {
        Agent motDePasseEnClair = new Agent("agent", "secret", "Alex", "Martin");
        Agent motDePasseHash = new Agent("responsable", "$2b$already-hashed", "Sam", "Durand");
        when(personneRepository.findAll()).thenReturn(List.of(motDePasseEnClair, motDePasseHash));
        when(passwordEncoder.encode("secret")).thenReturn("$2a$encoded");

        int nombreMigre = personneService.migrerMotsDePasse();

        assertEquals(1, nombreMigre);
        assertEquals("$2a$encoded", motDePasseEnClair.getMotDePasse());
        assertEquals("$2b$already-hashed", motDePasseHash.getMotDePasse());
        verify(personneRepository).save(motDePasseEnClair);
        verify(personneRepository, never()).save(motDePasseHash);
        verify(passwordEncoder).encode("secret");
        verify(passwordEncoder, never()).encode("$2b$already-hashed");
    }
}
