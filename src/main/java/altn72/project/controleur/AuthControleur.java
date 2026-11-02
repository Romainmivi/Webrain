package altn72.project.controleur;

import altn72.project.service.PersonneService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class AuthControleur {

    private final PersonneService personneService;

    public AuthControleur(PersonneService personneService) {
        this.personneService = personneService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        for (var authority : authentication.getAuthorities()) {
            if (authority instanceof SimpleGrantedAuthority) {
                String role = ((SimpleGrantedAuthority) authority).getAuthority();
                if ("ROLE_AGENT".equals(role)) {
                    return "redirect:/dashboard/agent";
                } else if ("ROLE_RESPONSABLE".equals(role)) {
                    return "redirect:/dashboard/responsable";
                }
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/dashboard/agent")
    public String dashboardAgent() {
        return "agent/dashboardAgent";
    }

    @GetMapping("/dashboard/responsable")
    public String dashboardResponsable() {
        return "responsable/dashboardResponsable";
    }

    @GetMapping("/api/auth/migrate-passwords")
    @ResponseBody
    public String migrerMotsDePasse() {
        int count = personneService.migrerMotsDePasse();
        return count + " mot(s) de passe migré(s) vers BCrypt.";
    }
}
