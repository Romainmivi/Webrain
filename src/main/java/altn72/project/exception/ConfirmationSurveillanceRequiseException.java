package altn72.project.exception;

public class ConfirmationSurveillanceRequiseException extends RuntimeException {

    public ConfirmationSurveillanceRequiseException() {
        super("Ce prestataire est sous surveillance. Confirmez explicitement son affectation.");
    }
}
