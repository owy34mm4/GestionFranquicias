package co.com.gestorfranquicia.model.exception;

import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException {

    private final TechnicalMessage technicalMessage;

    protected DomainException(String message, TechnicalMessage technicalMessage) {
        super(message);
        this.technicalMessage = technicalMessage;
    }

    protected DomainException(Throwable cause, TechnicalMessage technicalMessage) {
        super(cause);
        this.technicalMessage = technicalMessage;
    }
}
