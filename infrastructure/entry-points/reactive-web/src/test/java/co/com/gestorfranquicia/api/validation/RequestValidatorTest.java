package co.com.gestorfranquicia.api.validation;

import co.com.gestorfranquicia.api.dto.CreateFranchiseRequest;
import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.BusinessException;
import co.com.gestorfranquicia.model.exception.ProcessorException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class RequestValidatorTest {

    private final RequestValidator validator = new RequestValidator(
            new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator()));

    @Test
    void shouldPassThroughValidRequest() {
        CreateFranchiseRequest request = new CreateFranchiseRequest("Coffee");

        StepVerifier.create(validator.validate(request))
                .expectNext(request)
                .verifyComplete();
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        CreateFranchiseRequest request = new CreateFranchiseRequest("");

        StepVerifier.create(validator.validate(request))
                .expectErrorSatisfies(e -> assertThat(e)
                        .isInstanceOf(BusinessException.class)
                        .extracting(err -> ((ProcessorException) err).getTechnicalMessage())
                        .isEqualTo(TechnicalMessage.INVALID_REQUEST))
                .verify();
    }
}
