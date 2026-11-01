package altn72.project.modele;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "evaluation")
public class Evaluation {

    private static final int NOTE_MIN = 0;
    private static final int NOTE_MAX = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "note", nullable = false)
    private int note;

    @Column(name = "commentaire", length = 1000)
    private String commentaire;   // peut être null

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    protected Evaluation() {
    }

    private Evaluation(int note, String commentaire, LocalDateTime date, Incident incident) {
        if (note < NOTE_MIN || note > NOTE_MAX) {
            throw new IllegalArgumentException(
                    "La note doit être comprise entre " + NOTE_MIN + " et " + NOTE_MAX);
        }
        if (commentaire != null && commentaire.length() > 1000) {
            throw new IllegalArgumentException("Le commentaire ne peut pas dépasser 1000 caractères");
        }
        this.incident = Objects.requireNonNull(incident, "incident obligatoire");
        this.note = note;
        this.commentaire = commentaire;
        this.date = Objects.requireNonNull(date);
    }

    public static Evaluation creer(int note, String commentaire, LocalDateTime date, Incident incident) {
        return new Evaluation(note, commentaire, date, incident);
    }

    public Integer getId() {
        return id;
    }

    public int getNote() {
        return note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Incident getIncident() {
        return incident;
    }
}
