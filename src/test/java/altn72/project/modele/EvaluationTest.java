package altn72.project.modele;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class EvaluationTest {

    private final Incident incident = mock(Incident.class);
    private final LocalDateTime date = LocalDateTime.of(2026, 11, 3, 18, 0);

    @Test
    void accepteLesNotesAuxBornesEtLeCommentaireFacultatif() {
        Evaluation noteMinimale = Evaluation.creer(0, null, date, incident);
        Evaluation noteMaximale = Evaluation.creer(5, "Très bien", date, incident);

        assertEquals(0, noteMinimale.getNote());
        assertEquals(5, noteMaximale.getNote());
        assertNull(noteMinimale.getCommentaire());
        assertEquals("Très bien", noteMaximale.getCommentaire());
    }

    @Test
    void refuseUneNoteEnDehorsDeZeroEtCinq() {
        assertThrows(IllegalArgumentException.class, () -> Evaluation.creer(-1, null, date, incident));
        assertThrows(IllegalArgumentException.class, () -> Evaluation.creer(6, null, date, incident));
    }

    @Test
    void refuseUnCommentaireTropLong() {
        String commentaire = "x".repeat(1001);

        assertThrows(
                IllegalArgumentException.class,
                () -> Evaluation.creer(3, commentaire, date, incident)
        );
    }

    @Test
    void refuseUnIncidentOuUneDateNulle() {
        assertThrows(NullPointerException.class, () -> Evaluation.creer(3, null, date, null));
        assertThrows(NullPointerException.class, () -> Evaluation.creer(3, null, null, incident));
    }
}
