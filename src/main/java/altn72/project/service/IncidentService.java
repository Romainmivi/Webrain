package altn72.project.service;

import altn72.project.evenement.IncidentChangeEvent;
import altn72.project.exception.RessourceIntrouvableException;
import altn72.project.exception.RequeteInvalideException;
import altn72.project.modele.Incident;
import altn72.project.modele.IncidentEvent;
import altn72.project.modele.IncidentEventRepository;
import altn72.project.modele.IncidentRepository;
import altn72.project.modele.Prestataire;
import altn72.project.modele.PrestataireRepository;
import altn72.project.modele.equipements.Equipement;
import altn72.project.modele.equipements.EquipementRepository;
import altn72.project.modele.personnes.Agent;
import altn72.project.modele.personnes.AgentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {

    private static final int LONGUEUR_MAX_APERCU = 100;

    private final IncidentRepository incidentRepository;
    private final EquipementRepository equipementRepository;
    private final AgentRepository agentRepository;
    private final PrestataireRepository prestataireRepository;
    private final IncidentEventRepository incidentEventRepository;
    private final ApplicationEventPublisher eventPublisher;

    public IncidentService(
            IncidentRepository incidentRepository,
            EquipementRepository equipementRepository,
            AgentRepository agentRepository,
            PrestataireRepository prestataireRepository,
            IncidentEventRepository incidentEventRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.incidentRepository = incidentRepository;
        this.equipementRepository = equipementRepository;
        this.agentRepository = agentRepository;
        this.prestataireRepository = prestataireRepository;
        this.incidentEventRepository = incidentEventRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<Incident> getIncidents() {
        return incidentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public IncidentRepository.IncidentStatusSummary getDashboardSummary() {
        return incidentRepository.summarizeByStatus();
    }

    @Transactional
    public Incident ajouterIncident(
            Integer equipementId,
            Integer agentId,
            Integer prestataireId,
            String description
    ) {
        Equipement equipement = equipementRepository.findById(equipementId)
                .orElseThrow(() -> new RessourceIntrouvableException("Équipement", equipementId));
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RessourceIntrouvableException("Agent", agentId));
        Prestataire prestataire = prestataireId == null
                ? null
                : prestataireRepository.findById(prestataireId)
                        .orElseThrow(() -> new RessourceIntrouvableException("Prestataire", prestataireId));

        Incident incident = new Incident(
                equipement,
                description,
                LocalDateTime.now(),
                prestataire,
                agent
        );
        Incident savedIncident = incidentRepository.save(incident);

        publierEvenement(
                savedIncident,
                agent,
                IncidentChangeEvent.ChangeType.INCIDENT_CREE,
                "Incident créé par " + agent.getPrenom() + " " + agent.getNom() + " pour l'équipement " + equipement.getDescription()
        );

        return savedIncident;
    }

    @Transactional(readOnly = true)
    public Optional<Incident> getIncident(Integer id) {
        return incidentRepository.findWithDetailsById(id);
    }

    @Transactional(readOnly = true)
    public List<IncidentEvent> getHistorique(Integer incidentId) {
        return incidentEventRepository.findByIncidentIdOrderByDateDesc(incidentId);
    }

    @Transactional
    public Optional<Incident> modifierIncident(
            Integer id,
            String description,
            Incident.Status status,
            Prestataire prestataire
    ) {
        return incidentRepository.findById(id).map(incident -> {
                String ancienneDescription = incident.getDescription();
                Incident.Status ancienStatut = incident.getStatus();
                Prestataire ancienPrestataire = incident.getPrestataire();
                Agent agent = incident.getAgent();

                incident.setDescription(description);
                mettreAJourStatut(incident, status);
                incident.setPrestataire(prestataire);

                Incident savedIncident = incidentRepository.save(incident);

                if (!ancienneDescription.equals(description)) {
                    String apercuAncien = ancienneDescription.substring(0, Math.min(ancienneDescription.length(), LONGUEUR_MAX_APERCU)) + "...";
                    String apercuNouveau = description.substring(0, Math.min(description.length(), LONGUEUR_MAX_APERCU)) + "...";
                    publierEvenement(
                            savedIncident,
                            agent,
                            IncidentChangeEvent.ChangeType.DESCRIPTION_MODIFIEE,
                            "Description modifiée par " + agent.getPrenom() + " " + agent.getNom() + " : \"" + apercuAncien + "\" → \"" + apercuNouveau + "\""
                    );
                }

                if (ancienStatut != status) {
                    publierEvenement(
                            savedIncident,
                            agent,
                            IncidentChangeEvent.ChangeType.STATUT_MODIFIE,
                            "Statut modifié de " + ancienStatut + " à " + status + " par " + agent.getPrenom() + " " + agent.getNom()
                    );
                }

                if (prestataire != null && (ancienPrestataire == null || !prestataire.getId().equals(ancienPrestataire.getId()))) {
                    publierEvenement(
                            savedIncident,
                            agent,
                            IncidentChangeEvent.ChangeType.PRESTATAIRE_AFFECTE,
                            "Prestataire affecté : " + prestataire.getNom() + " par " + agent.getPrenom() + " " + agent.getNom()
                    );
                } else if (prestataire == null && ancienPrestataire != null) {
                    publierEvenement(
                            savedIncident,
                            agent,
                            IncidentChangeEvent.ChangeType.PRESTATAIRE_DESACTIVER,
                            "Prestataire désactivé : " + ancienPrestataire.getNom() + " par " + agent.getPrenom() + " " + agent.getNom()
                    );
                }

                return savedIncident;
            });
    }

    @Transactional
    public Incident modifierStatut(Integer id, Incident.Status nouveauStatut) {
        if (nouveauStatut == null) {
            throw new RequeteInvalideException("Le statut est obligatoire");
        }
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Incident", id));
        Incident.Status ancienStatut = incident.getStatus();
        mettreAJourStatut(incident, nouveauStatut);
        Incident savedIncident = incidentRepository.save(incident);

        publierEvenement(
                savedIncident,
                incident.getAgent(),
                IncidentChangeEvent.ChangeType.STATUT_MODIFIE,
                "Statut modifié de " + ancienStatut + " à " + nouveauStatut + " par " + incident.getAgent().getPrenom() + " " + incident.getAgent().getNom()
        );

        return savedIncident;
    }

    private void mettreAJourStatut(Incident incident, Incident.Status nouveauStatut) {
        Incident.Status ancienStatut = incident.getStatus();
        incident.setStatus(nouveauStatut);
        if (nouveauStatut == Incident.Status.CLOTURE) {
            if (ancienStatut != Incident.Status.CLOTURE || incident.getDateCloture() == null) {
                incident.setDateCloture(LocalDateTime.now());
            }
        } else {
            incident.setDateCloture(null);
        }
    }

    @Transactional
    public boolean supprimerIncident(Integer id) {
        if (!incidentRepository.existsById(id)) {
            return false;
        }
        Incident incident = incidentRepository.findWithDetailsById(id)
                .orElse(null);
        if (incident != null) {
            eventPublisher.publishEvent(new IncidentChangeEvent(
                    this,
                    id,
                    incident.getDescription(),
                    IncidentChangeEvent.ChangeType.INCIDENT_SUPPRIME,
                    "Incident supprimé par " + incident.getAgent().getPrenom() + " " + incident.getAgent().getNom(),
                    LocalDateTime.now(),
                    incident.getAgent()
            ));
        }
        incidentRepository.deleteById(id);
        return true;
    }

    private void publierEvenement(Incident incident, Agent agent, IncidentChangeEvent.ChangeType type, String description) {
        eventPublisher.publishEvent(new IncidentChangeEvent(
                this,
                incident.getId(),
                incident.getDescription(),
                type,
                description,
                LocalDateTime.now(),
                agent
        ));
    }
}
