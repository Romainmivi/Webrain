package altn72.project.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ModelAndView ressourceIntrouvable(RessourceIntrouvableException exception, Model model) {
        logger.warn("Ressource introuvable : {}", exception.getMessage());
        model.addAttribute("erreur", "Ressource introuvable : " + exception.getMessage());
        model.addAttribute("titreErreur", "Page non trouvée");
        return new ModelAndView("erreur/erreur404");
    }

    @ExceptionHandler(RequeteInvalideException.class)
    public ModelAndView requeteInvalide(RequeteInvalideException exception, Model model) {
        logger.warn("Requête invalide : {}", exception.getMessage());
        model.addAttribute("erreur", exception.getMessage());
        model.addAttribute("titreErreur", "Données invalides");
        return new ModelAndView("erreur/erreurValidation");
    }

    @ExceptionHandler(RegleMetierException.class)
    public ModelAndView regleMetier(RegleMetierException exception, Model model) {
        logger.warn("Règle métier violée : {}", exception.getMessage());
        model.addAttribute("erreur", exception.getMessage());
        model.addAttribute("titreErreur", "Opération impossible");
        return new ModelAndView("erreur/erreurMetier");
    }

    @ExceptionHandler(RessourceDejaExistanteException.class)
    public ModelAndView ressourceDejaExistante(RessourceDejaExistanteException exception, Model model) {
        logger.warn("Ressource déjà existante : {}", exception.getMessage());
        model.addAttribute("erreur", exception.getMessage());
        model.addAttribute("titreErreur", "Ressource déjà existante");
        return new ModelAndView("erreur/erreurValidation");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ModelAndView argumentTypeMismatch(MethodArgumentTypeMismatchException exception, Model model) {
        logger.warn("Type d'argument invalide : {}", exception.getMessage());
        String champ = exception.getName();
        model.addAttribute("erreur", "La valeur saisie pour \"" + champ + "\" n'est pas valide. Veuillez vérifier votre saisie.");
        model.addAttribute("titreErreur", "Données invalides");
        return new ModelAndView("erreur/erreurValidation");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView pageNotFound(NoHandlerFoundException exception, Model model) {
        logger.warn("Page non trouvée : {}", exception.getRequestURL());
        model.addAttribute("erreur", "La page demandée n'existe pas.");
        model.addAttribute("titreErreur", "Page non trouvée");
        return new ModelAndView("erreur/erreur404");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView accessDenied(AccessDeniedException exception, Model model) {
        logger.warn("Accès refusé : {}", exception.getMessage());
        model.addAttribute("erreur", "Vous n'avez pas les permissions nécessaires pour effectuer cette action.");
        model.addAttribute("titreErreur", "Accès refusé");
        return new ModelAndView("erreur/erreur403");
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView dataAccessError(DataAccessException exception, Model model) {
        logger.error("Erreur d'accès aux données", exception);
        model.addAttribute("erreur", "Une erreur est survenue lors de l'accès aux données. Veuillez réessayer plus tard.");
        model.addAttribute("titreErreur", "Erreur technique");
        return new ModelAndView("erreur/erreur500");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView uploadSizeExceeded(MaxUploadSizeExceededException exception, Model model) {
        logger.warn("Fichier trop volumineux : {}", exception.getMessage());
        model.addAttribute("erreur", "Le fichier sélectionné est trop volumineux. Veuillez choisir un fichier plus petit.");
        model.addAttribute("titreErreur", "Fichier trop volumineux");
        return new ModelAndView("erreur/erreurValidation");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView illegalArgument(IllegalArgumentException exception, Model model) {
        logger.warn("Argument illégal : {}", exception.getMessage());
        model.addAttribute("erreur", "Les données fournies ne sont pas valides : " + exception.getMessage());
        model.addAttribute("titreErreur", "Données invalides");
        return new ModelAndView("erreur/erreurValidation");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView genericError(Exception exception, Model model) {
        logger.error("Erreur inattendue", exception);
        model.addAttribute("erreur", "Une erreur inattendue est survenue. Veuillez réessayer ou contacter un administrateur.");
        model.addAttribute("titreErreur", "Erreur inattendue");
        return new ModelAndView("erreur/erreur500");
    }
}
