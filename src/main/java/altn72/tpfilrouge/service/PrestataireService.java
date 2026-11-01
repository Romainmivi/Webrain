package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Prestataire;
import altn72.tpfilrouge.modele.PrestataireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PrestataireService {

    private final PrestataireRepository prestataireRepository;

    public PrestataireService(PrestataireRepository prestataireRepository) {
        this.prestataireRepository = prestataireRepository;
    }

    public List<Prestataire> getPrestataires() {
        return prestataireRepository.findAll();
    }

    public Prestataire ajouterPrestataire(String nom, String numTelephone, String email) {
        return prestataireRepository.save(Prestataire.creer(nom, numTelephone, email));
    }

    public Optional<Prestataire> getPrestataire(Integer id) {
        return prestataireRepository.findById(id);
    }

    @Transactional
    public Optional<Prestataire> modifierPrestataire(Integer id, String nom, String numTelephone, String email) {
        return prestataireRepository.findById(id).map(prestataire -> {
            prestataire.setNom(nom);
            prestataire.setNumTelephone(numTelephone);
            prestataire.setEmail(email);
            return prestataire;
        });
    }

    public boolean supprimerPrestataire(Integer id) {
        if (!prestataireRepository.existsById(id)) {
            return false;
        }
        prestataireRepository.deleteById(id);
        return true;
    }
}
