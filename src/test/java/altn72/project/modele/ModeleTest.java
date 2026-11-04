package altn72.project.modele;

import altn72.project.modele.equipements.Ascenseur;
import altn72.project.modele.equipements.DistributeurTitreTransport;
import altn72.project.modele.equipements.EcranInformations;
import altn72.project.modele.equipements.EscaliersMecanique;
import altn72.project.modele.equipements.Equipement;
import altn72.project.modele.equipements.Portiques;
import altn72.project.modele.equipements.TypeEquipement;
import altn72.project.modele.personnes.Agent;
import altn72.project.modele.personnes.Personne;
import altn72.project.modele.personnes.Responsable;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

class ModeleTest {

    @Test
    void stationsMaintiennentLeursEquipementsEtCalculentLesHorsService() {
        Station station = new Station(1, "Gare", 48.0, 2.0, "Adresse", "Ville");
        Equipement hs = new Ascenseur("HS", TypeEquipement.ASCENSEUR, null, Equipement.Etat.HS);
        Equipement actif = new Portiques("Actif", TypeEquipement.PORTIQUE, null, Equipement.Etat.EN_MARCHE);

        station.ajouterEquipement(hs);
        station.addEquipements(List.of(actif));

        assertSame(station, hs.getStation());
        assertSame(station, actif.getStation());
        assertEquals(1, station.nbEquipementsHorsService());
        assertEquals(2, station.getEquipements().size());
    }

    @Test
    void sousTypesEquipementConserventLeurTypeEtLeurEtat() {
        Station station = new Station(1, "Gare", "Adresse", "Ville");
        List<Equipement> equipements = creerEquipements(station);

        assertInstanceOf(Ascenseur.class, equipements.get(0));
        assertInstanceOf(DistributeurTitreTransport.class, equipements.get(1));
        assertInstanceOf(EcranInformations.class, equipements.get(2));
        assertInstanceOf(EscaliersMecanique.class, equipements.get(3));
        assertInstanceOf(Portiques.class, equipements.get(4));
        assertEquals(TypeEquipement.PORTIQUE, equipements.get(4).getType());
        assertEquals("Distributeur", equipements.get(1).getDescription());
    }

    private List<Equipement> creerEquipements(Station station) {
        return List.of(
                new Ascenseur("Ascenseur", TypeEquipement.ASCENSEUR, station, Equipement.Etat.EN_MARCHE),
                new DistributeurTitreTransport("Distributeur", TypeEquipement.DISTRIBUTEUR, station, Equipement.Etat.ETEINT),
                new EcranInformations("Écran", TypeEquipement.ECRAN_INFORMATION, station, Equipement.Etat.HS),
                new EscaliersMecanique("Escalier", TypeEquipement.ESCALIER, station, Equipement.Etat.EN_MARCHE),
                new Portiques("Portique", TypeEquipement.PORTIQUE, station, Equipement.Etat.ETEINT)
        );
    }

    @Test
    void personnesAgentsEtResponsablesPartagentLesProprietesDeBase() {
        Personne agent = new Agent("agent", "hash", "Alex", "Martin");
        Personne responsable = new Responsable("responsable", "hash2", "Sam", "Durand");
        agent.setLogin("agent-modifie");
        agent.setMotDePasse("nouveau-hash");
        agent.setPrenom("Alexandre");
        agent.setNom("Martin");

        assertInstanceOf(Agent.class, agent);
        assertInstanceOf(Responsable.class, responsable);
        assertEquals("agent-modifie", agent.getLogin());
        assertEquals("nouveau-hash", agent.getMotDePasse());
        assertEquals("Alexandre", agent.getPrenom());
        assertEquals("Martin", agent.getNom());
        assertEquals("responsable", responsable.getLogin());
    }

    @Test
    void incidentExposeEtMetAJourSesRelationsEtSonStatut() {
        Station station = new Station(1, "Gare", "Adresse", "Ville");
        Equipement equipement = new Ascenseur(
                "Ascenseur", TypeEquipement.ASCENSEUR, station, Equipement.Etat.EN_MARCHE);
        Agent agent = new Agent("agent", "hash", "Alex", "Martin");
        Prestataire prestataire = Prestataire.creer("Prestataire", "0123456789", "test@example.com");
        LocalDateTime date = LocalDateTime.now();
        Incident incident = new Incident(equipement, "Panne", date, agent);

        incident.setDescription("Nouvelle panne");
        incident.setDateOuverture(date.plusMinutes(1));
        incident.setStatus(Incident.Status.CLOTURE);
        incident.setPrestataire(prestataire);
        incident.setDateCloture(date.plusHours(1));

        assertSame(equipement, incident.getEquipement());
        assertSame(agent, incident.getAgent());
        assertSame(prestataire, incident.getPrestataire());
        assertEquals("Nouvelle panne", incident.getDescription());
        assertEquals(Incident.Status.CLOTURE, incident.getStatus());
        assertEquals(date.plusMinutes(1), incident.getDateOuverture());
        assertEquals(date.plusHours(1), incident.getDateCloture());
    }

    @Test
    void prestataireEtTypesEquipementExposeLeursDonnees() {
        Prestataire prestataire = Prestataire.creer("Nom", "0102030405", "email@example.com");
        prestataire.setNom("Nouveau nom");
        prestataire.setNumTelephone("0607080910");
        prestataire.setEmail("nouveau@example.com");

        assertEquals("Nouveau nom", prestataire.getNom());
        assertEquals("0607080910", prestataire.getNumTelephone());
        assertEquals("nouveau@example.com", prestataire.getEmail());
        assertEquals("Ascenseur", TypeEquipement.ASCENSEUR.getLibelle());
    }
}
