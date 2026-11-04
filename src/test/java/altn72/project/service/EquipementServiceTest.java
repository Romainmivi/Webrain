package altn72.project.service;

import altn72.project.exception.RessourceIntrouvableException;
import altn72.project.modele.Station;
import altn72.project.modele.StationRepository;
import altn72.project.modele.equipements.Ascenseur;
import altn72.project.modele.equipements.DistributeurTitreTransport;
import altn72.project.modele.equipements.EcranInformations;
import altn72.project.modele.equipements.EscaliersMecanique;
import altn72.project.modele.equipements.Equipement;
import altn72.project.modele.equipements.EquipementRepository;
import altn72.project.modele.equipements.Portiques;
import altn72.project.modele.equipements.TypeEquipement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EquipementServiceTest {

    private EquipementRepository equipementRepository;
    private StationRepository stationRepository;
    private EquipementService equipementService;
    private Station station;

    @BeforeEach
    void setUp() {
        equipementRepository = mock(EquipementRepository.class);
        stationRepository = mock(StationRepository.class);
        equipementService = new EquipementService(equipementRepository, stationRepository);
        station = new Station(4, "Station", "Adresse", "Ville");
        when(stationRepository.findById(4)).thenReturn(Optional.of(station));
        when(equipementRepository.save(any(Equipement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void creeLaSousClasseCorrespondantAChaqueType() {
        assertInstanceOf(Ascenseur.class, creer(TypeEquipement.ASCENSEUR));
        assertInstanceOf(DistributeurTitreTransport.class, creer(TypeEquipement.DISTRIBUTEUR));
        assertInstanceOf(EcranInformations.class, creer(TypeEquipement.ECRAN_INFORMATION));
        assertInstanceOf(EscaliersMecanique.class, creer(TypeEquipement.ESCALIER));
        assertInstanceOf(Portiques.class, creer(TypeEquipement.PORTIQUE));
    }

    @Test
    void refuseLaCreationSiLaStationNExistePas() {
        when(stationRepository.findById(9)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                RessourceIntrouvableException.class,
                () -> equipementService.ajouterEquipement(
                        "Description", TypeEquipement.ASCENSEUR, 9, Equipement.Etat.HS)
        );
        verify(equipementRepository, never()).save(any(Equipement.class));
    }

    @Test
    void modifieEtSupprimeUnEquipementExistant() {
        Equipement equipement = new Ascenseur(
                "Ancienne description", TypeEquipement.ASCENSEUR, station, Equipement.Etat.HS);
        when(equipementRepository.findById(2)).thenReturn(Optional.of(equipement));
        when(equipementRepository.existsById(2)).thenReturn(true);

        assertSame(equipement, equipementService.modifierEquipement(
                2, "Nouvelle description", station, Equipement.Etat.EN_MARCHE).orElseThrow());
        assertEquals("Nouvelle description", equipement.getDescription());
        assertEquals(Equipement.Etat.EN_MARCHE, equipement.getEtat());
        assertTrue(equipementService.supprimerEquipement(2));
        verify(equipementRepository).deleteById(2);
    }

    @Test
    void retourneAbsentOuFauxPourUnEquipementInexistant() {
        when(equipementRepository.findWithStationById(8)).thenReturn(Optional.empty());
        when(equipementRepository.findById(8)).thenReturn(Optional.empty());
        when(equipementRepository.existsById(8)).thenReturn(false);

        assertTrue(equipementService.getEquipement(8).isEmpty());
        assertTrue(equipementService.modifierEquipement(
                8, "Description", station, Equipement.Etat.HS).isEmpty());
        assertFalse(equipementService.supprimerEquipement(8));
    }

    private Equipement creer(TypeEquipement type) {
        Equipement equipement = equipementService.ajouterEquipement(
                "Description", type, 4, Equipement.Etat.EN_MARCHE);
        assertSame(station, equipement.getStation());
        assertEquals(type, equipement.getType());
        return equipement;
    }
}
