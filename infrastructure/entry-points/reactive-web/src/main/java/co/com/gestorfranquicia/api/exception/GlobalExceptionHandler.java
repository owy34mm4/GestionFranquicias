package co.com.gestorfranquicia.api.exception;

import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.BusinessException;
import co.com.gestorfranquicia.model.exception.DomainException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.webflux.autoconfigure.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.webflux.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  WebProperties webProperties,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer codecConfigurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(codecConfigurer.getWriters());
        this.setMessageReaders(codecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderError);
    }

    private Mono<ServerResponse> renderError(ServerRequest request) {
        Throwable error = getError(request);
        TechnicalMessage technicalMessage = resolve(error);
        HttpStatus status = statusOf(error, technicalMessage);
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ErrorResponse(technicalMessage.getCode(), technicalMessage.getMessage()));
    }

    private TechnicalMessage resolve(Throwable error) {
        if (error instanceof DomainException domainException) {
            return domainException.getTechnicalMessage();
        }
        if (error instanceof CallNotPermittedException) {
            return TechnicalMessage.SERVICE_UNAVAILABLE;
        }
        return TechnicalMessage.INTERNAL_ERROR;
    }

    private HttpStatus statusOf(Throwable error, TechnicalMessage technicalMessage) {
        HttpStatus parsed = parseStatus(technicalMessage.getCode());
        if (parsed != null) {
            return parsed;
        }
        if (error instanceof BusinessException) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private HttpStatus parseStatus(String code) {
        try {
            return HttpStatus.resolve(Integer.parseInt(code));
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
