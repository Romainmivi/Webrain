package altn72.project.api;

import altn72.project.exception.RequeteInvalideException;
import altn72.project.exception.RessourceIntrouvableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = IncidentRestController.class)
public class IncidentApiExceptionHandler {

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<ProblemDetail> ressourceIntrouvable(RessourceIntrouvableException exception) {
        return probleme(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(RequeteInvalideException.class)
    public ResponseEntity<ProblemDetail> requeteInvalide(RequeteInvalideException exception) {
        return probleme(HttpStatus.BAD_REQUEST, exception);
    }

    private ResponseEntity<ProblemDetail> probleme(HttpStatus status, RuntimeException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, exception.getMessage());
        problem.setTitle(status.getReasonPhrase());
        return ResponseEntity.status(status).body(problem);
    }
}
