package co.com.gestorfranquicia.usecase.franchise;

import co.com.gestorfranquicia.model.branchtopproduct.BranchTopProduct;
import co.com.gestorfranquicia.model.enums.RenameCheck;
import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.BusinessException;
import co.com.gestorfranquicia.model.exception.DomainException;
import co.com.gestorfranquicia.model.franchise.Franchise;
import co.com.gestorfranquicia.model.franchise.gateways.FranchiseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private FranchiseUseCase franchiseUseCase;

    @Test
    void shouldCreateFranchiseWhenNameDoesNotExist() {
        Franchise saved = Franchise.reconstitute(1L, "Coffee Shop");
        when(franchiseRepository.existsByName("Coffee Shop")).thenReturn(Mono.just(false));
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(franchiseUseCase.create("Coffee Shop"))
                .expectNext(saved)
                .verifyComplete();

        verify(franchiseRepository).save(any(Franchise.class));
    }

    @Test
    void shouldFailToCreateFranchiseWhenNameAlreadyExists() {
        when(franchiseRepository.existsByName("Coffee Shop")).thenReturn(Mono.just(true));

        StepVerifier.create(franchiseUseCase.create("Coffee Shop"))
                .expectErrorSatisfies(e -> assertThat(e)
                        .isInstanceOf(BusinessException.class)
                        .extracting(err -> ((DomainException) err).getTechnicalMessage())
                        .isEqualTo(TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                .verify();

        verify(franchiseRepository, never()).save(any());
    }

    @Test
    void shouldReturnTopStockPerBranchWhenFranchiseExists() {
        BranchTopProduct topProduct = BranchTopProduct.reconstitute(1L, "Downtown", 2L, "Espresso", 10);
        when(franchiseRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(franchiseRepository.findTopStockPerBranch(1L)).thenReturn(Flux.just(topProduct));

        StepVerifier.create(franchiseUseCase.topStockPerBranch(1L))
                .expectNext(topProduct)
                .verifyComplete();
    }

    @Test
    void shouldFailToGetTopStockPerBranchWhenFranchiseDoesNotExist() {
        when(franchiseRepository.existsById(1L)).thenReturn(Mono.just(false));

        StepVerifier.create(franchiseUseCase.topStockPerBranch(1L))
                .expectErrorSatisfies(e -> assertThat(e)
                        .isInstanceOf(BusinessException.class)
                        .extracting(err -> ((DomainException) err).getTechnicalMessage())
                        .isEqualTo(TechnicalMessage.FRANCHISE_NOT_FOUND))
                .verify();
    }

    @Test
    void shouldUpdateNameWhenAllowed() {
        when(franchiseRepository.validateForRename("New Name", 1L)).thenReturn(Mono.just(RenameCheck.ALLOWED));
        when(franchiseRepository.updateName(anyLong(), anyString())).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.updateName(1L, "New Name"))
                .verifyComplete();

        verify(franchiseRepository).updateName(1L, "New Name");
    }

    @Test
    void shouldFailToUpdateNameWhenFranchiseNotFound() {
        when(franchiseRepository.validateForRename("New Name", 1L)).thenReturn(Mono.just(RenameCheck.NOT_FOUND));

        StepVerifier.create(franchiseUseCase.updateName(1L, "New Name"))
                .expectErrorSatisfies(e -> assertThat(e)
                        .isInstanceOf(BusinessException.class)
                        .extracting(err -> ((DomainException) err).getTechnicalMessage())
                        .isEqualTo(TechnicalMessage.FRANCHISE_NOT_FOUND))
                .verify();

        verify(franchiseRepository, never()).updateName(anyLong(), anyString());
    }

    @Test
    void shouldFailToUpdateNameWhenAlreadyExists() {
        when(franchiseRepository.validateForRename("New Name", 1L)).thenReturn(Mono.just(RenameCheck.ALREADY_EXISTS));

        StepVerifier.create(franchiseUseCase.updateName(1L, "New Name"))
                .expectErrorSatisfies(e -> assertThat(e)
                        .isInstanceOf(BusinessException.class)
                        .extracting(err -> ((DomainException) err).getTechnicalMessage())
                        .isEqualTo(TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                .verify();

        verify(franchiseRepository, never()).updateName(anyLong(), anyString());
    }
}
