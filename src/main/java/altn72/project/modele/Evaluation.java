package altn72.project.modele;

import altn72.project.modele.personnes.Personne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "evaluation")
public class Evaluation {

    private static final int NOTE_MIN = 0;
    private static final int NOTE_MAX = 5;

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "note", nullable = false)
    private int note;

    @Column(name = "commentaire")
    private String commentaire;   // peut être null

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    private Incident incident;

    protected Evaluation() {
    }

    public Evaluation(int id, int note,
                      String commentaire, LocalDate date, Incident incident) {
        this.incident = incident;
        if (note < NOTE_MIN || note > NOTE_MAX) {
            throw new IllegalArgumentException(
                    "La note doit être comprise entre " + NOTE_MIN + " et " + NOTE_MAX);
        }
        this.id = id;
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
        return incident.getAgent();
    }

    public Incident getIncident_id() {
        return incident;
    }
}
