package altn72.project.service;

import altn72.project.modele.Prestataire;
import altn72.project.modele.PrestataireRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PrestataireServiceTest {

    private PrestataireRepository prestataireRepository;
    private PrestataireService prestataireService;

    @BeforeEach
    void setUp() {
        prestataireRepository = mock(PrestataireRepository.class);
        prestataireService = new PrestataireService(prestataireRepository);
    }

    @Test
    void supprimeUnPrestataireExistant() {
        when(prestataireRepository.existsById(7)).thenReturn(true);

        assertTrue(prestataireService.supprimerPrestataire(7));
        verify(prestataireRepository).deleteById(7);
    }

    @Test
    void neSupprimePasUnPrestataireInexistant() {
        when(prestataireRepository.existsById(7)).thenReturn(false);

        assertFalse(prestataireService.supprimerPrestataire(7));
        verify(prestataireRepository).existsById(7);
    }

    @Test
    void retourneLePrestataireTrouveParId() {
        Prestataire prestataire = Prestataire.creer("Entreprise", "0123456789", "contact@example.com");
        when(prestataireRepository.findById(7)).thenReturn(Optional.of(prestataire));

        assertTrue(prestataireService.getPrestataire(7).isPresent());
        verify(prestataireRepository).findById(7);
    }
}
