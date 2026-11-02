package altn72.project.security;

import altn72.project.modele.personnes.Agent;
import altn72.project.modele.personnes.Personne;
import altn72.project.modele.personnes.PersonneRepository;
import altn72.project.modele.personnes.Responsable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PersonneUserDetailsService implements UserDetailsService {

    private final PersonneRepository personneRepository;

    public PersonneUserDetailsService(PersonneRepository personneRepository) {
        this.personneRepository = personneRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Personne personne = personneRepository.findByLogin(username);

        if (personne == null) {
            throw new UsernameNotFoundException("Utilisateur non trouvé : " + username);
        }

        String role;
        if (personne instanceof Agent) {
            role = "ROLE_AGENT";
        } else if (personne instanceof Responsable) {
            role = "ROLE_RESPONSABLE";
        } else {
            throw new UsernameNotFoundException("Type d'utilisateur inconnu pour : " + username);
        }

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

        return new User(personne.getLogin(), personne.getMotDePasse(), authorities);
    }
}
