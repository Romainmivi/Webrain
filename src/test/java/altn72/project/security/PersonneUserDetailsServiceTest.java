package altn72.project.security;

import altn72.project.modele.personnes.Agent;
import altn72.project.modele.personnes.Personne;
import altn72.project.modele.personnes.PersonneRepository;
import altn72.project.modele.personnes.Responsable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PersonneUserDetailsServiceTest {

    private PersonneRepository personneRepository;
    private PersonneUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        personneRepository = mock(PersonneRepository.class);
        userDetailsService = new PersonneUserDetailsService(personneRepository);
    }

    @Test
    void chargeUnAgentAvecLeRoleAgent() {
        when(personneRepository.findByLogin("agent"))
                .thenReturn(new Agent("agent", "hash", "Alex", "Martin"));

        UserDetails user = userDetailsService.loadUserByUsername("agent");

        assertEquals("agent", user.getUsername());
        assertEquals("hash", user.getPassword());
        assertEquals("ROLE_AGENT", user.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void chargeUnResponsableAvecLeRoleResponsable() {
        when(personneRepository.findByLogin("responsable"))
                .thenReturn(new Responsable("responsable", "hash", "Sam", "Durand"));

        UserDetails user = userDetailsService.loadUserByUsername("responsable");

        assertEquals("ROLE_RESPONSABLE", user.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void refuseUnLoginInconnu() {
        when(personneRepository.findByLogin("absent")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("absent"));
    }

    @Test
    void refuseUnTypeDePersonneNonSupporte() {
        Personne personneInconnue = mock(Personne.class);
        when(personneInconnue.getLogin()).thenReturn("autre");
        when(personneRepository.findByLogin("autre")).thenReturn(personneInconnue);

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("autre"));
    }
}
