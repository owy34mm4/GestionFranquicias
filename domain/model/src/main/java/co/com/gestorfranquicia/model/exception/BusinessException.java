package co.com.gestorfranquicia.model.exception;

import co.com.gestorfranquicia.model.enums.TechnicalMessage;

public class BusinessException extends ProcessorException {

    public BusinessException(TechnicalMessage technicalMessage) {
        super(technicalMessage.getMessage(), technicalMessage);
    }
}
