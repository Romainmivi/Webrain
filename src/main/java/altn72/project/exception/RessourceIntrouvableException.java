package altn72.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RessourceIntrouvableException extends RuntimeException {

    public RessourceIntrouvableException(String ressource, Object id) {
        super(ressource + " introuvable (id : " + id + ")");
    }

    public RessourceIntrouvableException(String message) {
        super(message);
    }
}
