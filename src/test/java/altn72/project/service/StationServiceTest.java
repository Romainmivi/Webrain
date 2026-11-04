package altn72.project.service;

import altn72.project.exception.RessourceDejaExistanteException;
import altn72.project.modele.Station;
import altn72.project.modele.StationRepository;
import altn72.project.modele.equipements.EquipementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StationServiceTest {

    private StationRepository stationRepository;
    private EquipementRepository equipementRepository;
    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationRepository = mock(StationRepository.class);
        equipementRepository = mock(EquipementRepository.class);
        stationService = new StationService(stationRepository, equipementRepository);
    }

    @Test
    void ajouteUneStationEtRefuseUnNomDejaUtilise() {
        when(stationRepository.existsByNom("Gare")).thenReturn(false);
        when(stationRepository.save(org.mockito.ArgumentMatchers.any(Station.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Station station = stationService.ajouterStation("Gare", "Adresse", "Ville");
        assertEquals("Gare", station.getNom());
        verify(stationRepository).save(station);

        when(stationRepository.existsByNom("Gare")).thenReturn(true);
        assertThrows(
                RessourceDejaExistanteException.class,
                () -> stationService.ajouterStation("Gare", "Adresse", "Ville")
        );
    }

    @Test
    void modifieEtSupprimeUneStationExistante() {
        Station station = new Station(5, "Avant", "Adresse", "Ville");
        when(stationRepository.findById(5)).thenReturn(Optional.of(station));
        when(stationRepository.existsById(5)).thenReturn(true);

        stationService.modifierStation(5, "Après", "Nouvelle adresse", "Nouvelle ville").orElseThrow();

        assertEquals("Après", station.getNom());
        assertEquals("Nouvelle adresse", station.getAdresse());
        assertEquals("Nouvelle ville", station.getVille());
        assertTrue(stationService.supprimerStation(5));
        verify(stationRepository).deleteById(5);
    }

    @Test
    void retourneVideEtFauxPourUneStationInconnue() {
        when(stationRepository.findById(99)).thenReturn(Optional.empty());
        when(stationRepository.existsById(99)).thenReturn(false);

        assertTrue(stationService.getStation(99).isEmpty());
        assertTrue(stationService.modifierStation(99, "Nom", "Adresse", "Ville").isEmpty());
        assertFalse(stationService.supprimerStation(99));
    }
}
