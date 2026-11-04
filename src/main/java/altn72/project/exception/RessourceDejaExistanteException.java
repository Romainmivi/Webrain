package altn72.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RessourceDejaExistanteException extends RuntimeException {

    public RessourceDejaExistanteException(String ressource, String champ, Object valeur) {
        super(ressource + " déjà existante : " + champ + " = " + valeur);
    }
}
