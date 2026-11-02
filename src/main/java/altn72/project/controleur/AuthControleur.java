package altn72.project.controleur;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class AuthControleur {

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
}
