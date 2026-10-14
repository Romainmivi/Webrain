package altn72.tpfilrouge.modele;

import altn72.tpfilrouge.modele.personnes.Personne;

import java.time.LocalDate;
import java.util.Objects;

public class Evaluation {

    private static final int NOTE_MIN = 0;
    private static final int NOTE_MAX = 5;

    private final int id;
    private final Personne personne;
    private final int note;
    private final String commentaire;   // peut être null
    private final LocalDate date;
    private final Incident incident;

    public Evaluation(int id, Personne personne, int note,
                      String commentaire, LocalDate date, Incident incident) {
        this.incident = incident;
        if (note < NOTE_MIN || note > NOTE_MAX) {
            throw new IllegalArgumentException(
                    "La note doit être comprise entre " + NOTE_MIN + " et " + NOTE_MAX);
        }
        this.id = id;
        this.personne = Objects.requireNonNull(personne);
        this.note = note;
        this.commentaire = commentaire;
        this.date = Objects.requireNonNull(date);
    }

    public int getId() {
        return id;
    }

    public int getNote() {
        return note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public LocalDate getDate() {
        return date;
    }

    public Personne getPersonne() {
        return personne;
    }

    public Incident getIncident_id() {
        return incident;
    }
}
