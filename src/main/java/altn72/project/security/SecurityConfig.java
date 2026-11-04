package altn72.project.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Routes publiques
                .requestMatchers(
                    "/login", "/api/auth/**", "/css/**", "/js/**", "/images/**",
                    "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**"
                ).permitAll()
                // Dashboard selon le rôle
                .requestMatchers("/dashboard/agent").hasRole("AGENT")
                .requestMatchers("/dashboard/responsable").hasRole("RESPONSABLE")
                // Gestion des personnes : réservé au Responsable
                .requestMatchers("/api/personnes/**").hasRole("RESPONSABLE")
                // Routes API : DELETE réservé au Responsable
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/**").hasRole("RESPONSABLE")
                // Toutes les autres routes nécessitent une authentification
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .usernameParameter("login")
                .passwordParameter("motDePasse")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .userDetailsService(userDetailsService);

        return http.build();
    }
}
