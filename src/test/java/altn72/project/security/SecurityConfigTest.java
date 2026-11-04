package altn72.project.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Test
    void fournitUnEncodeurDeMotDePasseBCrypt() {
        SecurityConfig configuration = new SecurityConfig(mock(UserDetailsService.class));
        PasswordEncoder encoder = configuration.passwordEncoder();

        String encoded = encoder.encode("mot-de-passe");

        assertTrue(encoded.startsWith("$2"));
        assertTrue(encoder.matches("mot-de-passe", encoded));
    }
}
