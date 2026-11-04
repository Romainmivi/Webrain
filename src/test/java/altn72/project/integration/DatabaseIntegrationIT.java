package altn72.project.integration;

import altn72.project.modele.Evaluation;
import altn72.project.modele.EvaluationRepository;
import altn72.project.modele.Incident;
import altn72.project.modele.IncidentRepository;
import altn72.project.modele.Prestataire;
import altn72.project.modele.PrestataireRepository;
import altn72.project.modele.Station;
import altn72.project.modele.StationRepository;
import altn72.project.modele.equipements.Ascenseur;
import altn72.project.modele.equipements.Equipement;
import altn72.project.modele.equipements.EquipementRepository;
import altn72.project.modele.equipements.TypeEquipement;
import altn72.project.modele.personnes.Agent;
import altn72.project.modele.personnes.AgentRepository;
import altn72.project.modele.personnes.PersonneRepository;
import altn72.project.modele.personnes.Responsable;
import altn72.project.modele.personnes.ResponsableRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class DatabaseIntegrationIT {

    @Container
    @SuppressWarnings("resource")
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("webrain_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", MYSQL::getJdbcUrl);
        properties.add("spring.datasource.username", MYSQL::getUsername);
        properties.add("spring.datasource.password", MYSQL::getPassword);
        properties.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private EquipementRepository equipementRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private PersonneRepository personneRepository;

    @Autowired
    private ResponsableRepository responsableRepository;

    @Autowired
    private PrestataireRepository prestataireRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final AtomicInteger fixtureNumber = new AtomicInteger();

    @Test
    @Transactional
    void persisteUneEvaluationEtChargeSesRelationsDepuisMySql() {
        Fixture fixture = creerFixture(true);
        Evaluation evaluation = evaluationRepository.saveAndFlush(
                Evaluation.creer(5, "Intervention efficace", LocalDateTime.now(), fixture.incident())
        );
        Integer evaluationId = evaluation.getId();
        assertNotNull(evaluationId);

        entityManager.clear();

        Evaluation rechargee = evaluationRepository.findWithDetailsById(evaluationId).orElseThrow();

        assertEquals(5, rechargee.getNote());
        assertEquals("Intervention efficace", rechargee.getCommentaire());
        assertTrue(Hibernate.isInitialized(rechargee.getIncident()));
        assertTrue(Hibernate.isInitialized(rechargee.getIncident().getEquipement()));
        assertTrue(Hibernate.isInitialized(rechargee.getIncident().getAgent()));
        assertTrue(Hibernate.isInitialized(rechargee.getIncident().getPrestataire()));
        assertEquals(fixture.prestataire().getNom(), rechargee.getIncident().getPrestataire().getNom());
        assertEquals(fixture.equipement().getDescription(), rechargee.getIncident().getEquipement().getDescription());
    }

    @Test
    @Transactional
    void neRetourneQueLesIncidentsCloturesAvecPrestatairePourEvaluation() {
        Fixture eligible = creerFixture(true);
        Fixture open = creerFixture(true);
        open.incident().setStatus(Incident.Status.EN_COURS);
        incidentRepository.saveAndFlush(open.incident());
        Fixture withoutProvider = creerFixture(false);
        withoutProvider.incident().setStatus(Incident.Status.CLOTURE);
        incidentRepository.saveAndFlush(withoutProvider.incident());

        List<Incident> resultats = incidentRepository.findByStatusAndPrestataireIsNotNull(
                Incident.Status.CLOTURE
        );

        assertEquals(1, resultats.size());
        assertEquals(eligible.incident().getId(), resultats.getFirst().getId());
        assertFalse(resultats.stream().anyMatch(incident -> incident.getStatus() != Incident.Status.CLOTURE));
    }

    @Test
    @Transactional
    void requeteNativeCalculeLeResumeDesIncidentsParStatut() {
        Fixture open = creerFixture(true);
        open.incident().setStatus(Incident.Status.OUVERT);
        incidentRepository.saveAndFlush(open.incident());

        Fixture inProgress = creerFixture(true);
        inProgress.incident().setStatus(Incident.Status.EN_COURS);
        incidentRepository.saveAndFlush(inProgress.incident());

        Fixture closed = creerFixture(true);
        closed.incident().setStatus(Incident.Status.CLOTURE);
        incidentRepository.saveAndFlush(closed.incident());

        IncidentRepository.IncidentStatusSummary summary = incidentRepository.summarizeByStatus();

        assertEquals(1L, summary.getOpenCount());
        assertEquals(1L, summary.getInProgressCount());
        assertEquals(1L, summary.getClosedCount());
    }

    @Test
    @Transactional
    void requeteJpqlRetourneLesEvaluationsDuPrestataireDemande() {
        Fixture evaluated = creerFixture(true);
        Fixture otherProvider = creerFixture(true);
        evaluationRepository.saveAndFlush(
                Evaluation.creer(4, "Bon travail", LocalDateTime.now(), evaluated.incident())
        );
        evaluationRepository.saveAndFlush(
                Evaluation.creer(2, "Autre prestataire", LocalDateTime.now(), otherProvider.incident())
        );
        entityManager.flush();
        entityManager.clear();

        List<Evaluation> evaluations =
                evaluationRepository.findByPrestataireId(evaluated.prestataire().getId());

        assertEquals(1, evaluations.size());
        assertEquals(4, evaluations.getFirst().getNote());
        assertEquals("Bon travail", evaluations.getFirst().getCommentaire());
        assertEquals(
                evaluated.prestataire().getId(),
                evaluations.getFirst().getIncident().getPrestataire().getId()
        );
        assertTrue(Hibernate.isInitialized(evaluations.getFirst().getIncident().getEquipement()));
    }

    @Test
    @Transactional
    void persisteEtRechercheLesPersonnesSelonLeurSousType() {
        Agent agent = agentRepository.saveAndFlush(
                new Agent("agent-login", "agent-hash", "Alex", "Agent")
        );
        Responsable responsable = responsableRepository.saveAndFlush(
                new Responsable("responsable-login", "responsable-hash", "Sam", "Responsable")
        );
        entityManager.clear();

        assertEquals(agent.getId(), personneRepository.findByLogin("agent-login").getId());
        assertEquals(
                responsable.getId(),
                personneRepository.findByLogin("responsable-login").getId()
        );
        assertEquals(
                Responsable.class,
                personneRepository.findByLogin("responsable-login").getClass()
        );
    }

    private Fixture creerFixture(boolean avecPrestataire) {
        int fixtureId = fixtureNumber.incrementAndGet();
        Station station = stationRepository.saveAndFlush(
                new Station(1000 + fixtureId, "Station de test", 48.8566, 2.3522, "1 rue de test", "Paris")
        );
        Equipement equipement = equipementRepository.saveAndFlush(
                new Ascenseur("Ascenseur de test", TypeEquipement.ASCENSEUR, station, Equipement.Etat.EN_MARCHE)
        );
        Agent agent = agentRepository.saveAndFlush(
                new Agent("agent-test-" + fixtureId, "mot-de-passe-hache", "Alex", "Test")
        );
        Prestataire prestataire = avecPrestataire
                ? prestataireRepository.saveAndFlush(
                        Prestataire.creer("Prestataire de test", "0102030405", "test@example.com"))
                : null;
        Incident incident = incidentRepository.saveAndFlush(
                new Incident(equipement, "Incident de test", LocalDateTime.now(), prestataire, agent)
        );
        incident.setStatus(Incident.Status.CLOTURE);
        incidentRepository.saveAndFlush(incident);

        return new Fixture(incident, equipement, prestataire);
    }

    private record Fixture(Incident incident, Equipement equipement, Prestataire prestataire) {
    }
}
