package altn72.project.modele;

import altn72.project.modele.equipements.Ascenseur;
import altn72.project.modele.equipements.Equipement;
import altn72.project.modele.equipements.TypeEquipement;
import altn72.project.modele.personnes.Agent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IncidentIndicateurCouleurTest {

    final ZoneId zone = ZoneId.systemDefault();

    @BeforeEach
    void setUp() {
        // Par défaut, utiliser l'horloge système
        Incident.resetClock();
    }

    @AfterEach
    void tearDown() {
        // Réinitialiser l'horloge après chaque test pour éviter les effets de bord
        Incident.resetClock();
    }

    @Test
    void incident_ouvert_depuis_47_heures_est_vert() {
        Clock clock = Clock.fixed(Instant.parse("2026-11-06T09:00:00Z"), zone);
        Incident.setClock(clock);
        LocalDateTime maintenant = LocalDateTime.now(clock);
        LocalDateTime dateOuverture = maintenant.minusHours(47);

        Incident incident = creerIncident(dateOuverture);

        assertEquals("success", incident.getIndicateurCouleur());
    }

    @Test
    void incident_ouvert_depuis_48_heures_est_orange() {
        Clock clock = Clock.fixed(Instant.parse("2026-11-06T10:00:00Z"), zone);
        Incident.setClock(clock);
        LocalDateTime maintenant = LocalDateTime.now(clock);
        LocalDateTime dateOuverture = maintenant.minusHours(48);

        Incident incident = creerIncident(dateOuverture);

        assertEquals("warning", incident.getIndicateurCouleur());
    }

    @Test
    void incident_ouvert_depuis_60_heures_est_orange() {
        Clock clock = Clock.fixed(Instant.parse("2026-11-06T10:00:00Z"), zone);
        Incident.setClock(clock);
        LocalDateTime maintenant = LocalDateTime.now(clock);
        LocalDateTime dateOuverture = maintenant.minusHours(60);

        Incident incident = creerIncident(dateOuverture);

        assertEquals("warning", incident.getIndicateurCouleur());
    }

    @Test
    void incident_ouvert_depuis_72_heures_est_rouge() {
        Clock clock = Clock.fixed(Instant.parse("2026-11-06T10:00:00Z"), zone);
        Incident.setClock(clock);
        LocalDateTime maintenant = LocalDateTime.now(clock);
        LocalDateTime dateOuverture = maintenant.minusHours(72);

        Incident incident = creerIncident(dateOuverture);

        assertEquals("danger", incident.getIndicateurCouleur());
    }

    @Test
    void incident_ouvert_depuis_100_heures_est_rouge() {
        Clock clock = Clock.fixed(Instant.parse("2026-11-06T10:00:00Z"), zone);
        Incident.setClock(clock);
        LocalDateTime maintenant = LocalDateTime.now(clock);
        LocalDateTime dateOuverture = maintenant.minusHours(100);

        Incident incident = creerIncident(dateOuverture);

        assertEquals("danger", incident.getIndicateurCouleur());
    }

    @Test
    void incident_freshment_ouvert_est_vert() {
        Clock clock = Clock.fixed(Instant.parse("2026-11-04T10:00:00Z"), zone);
        Incident.setClock(clock);
        LocalDateTime maintenant = LocalDateTime.now(clock);

        Incident incident = creerIncident(maintenant);

        assertEquals("success", incident.getIndicateurCouleur());
    }

    @Test
    void seuils_sont_des_constantes_publiques() {
        assertEquals(48, Incident.SEUIL_VERT_HEURES);
        assertEquals(72, Incident.SEUIL_ORANGE_HEURES);
    }

    private Incident creerIncident(LocalDateTime dateOuverture) {
        Equipement equipement = new Ascenseur(
                "Ascenseur", TypeEquipement.ASCENSEUR, null, Equipement.Etat.EN_MARCHE);
        Agent agent = new Agent("agent", "hash", "Alex", "Martin");
        Incident incident = new Incident(equipement, "Panne", dateOuverture, agent);
        return incident;
    }
}
