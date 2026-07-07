package co.com.gestorfranquicia.model.exception;

import co.com.gestorfranquicia.model.enums.TechnicalMessage;

public class TechnicalException extends ProcessorException {

    public TechnicalException(TechnicalMessage technicalMessage) {
        super(technicalMessage.getMessage(), technicalMessage);
    }

    public TechnicalException(Throwable cause, TechnicalMessage technicalMessage) {
        super(cause, technicalMessage);
    }
}
