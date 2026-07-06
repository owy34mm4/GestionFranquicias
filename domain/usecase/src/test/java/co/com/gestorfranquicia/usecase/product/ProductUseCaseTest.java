package co.com.gestorfranquicia.usecase.product;

import co.com.gestorfranquicia.model.enums.CreationCheck;
import co.com.gestorfranquicia.model.enums.RenameCheck;
import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.BusinessException;
import co.com.gestorfranquicia.model.exception.DomainException;
import co.com.gestorfranquicia.model.product.Product;
import co.com.gestorfranquicia.model.product.gateways.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class ProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductUseCase productUseCase;

    @Test
    void shouldCreateProductWhenAllowed() {
        Product saved = Product.reconstitute(1L, "Espresso", 10, 2L);
        when(productRepository.validateForCreation("Espresso", 2L)).thenReturn(Mono.just(CreationCheck.ALLOWED));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(productUseCase.create("Espresso", 10, 2L))
                .expectNext(saved)
                .verifyComplete();

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldFailToCreateProductWhenBranchNotFound() {
        when(productRepository.validateForCreation("Espresso", 2L)).thenReturn(Mono.just(CreationCheck.PARENT_NOT_FOUND));

        StepVerifier.create(productUseCase.create("Espresso", 10, 2L))
                .expectErrorSatisfies(e -> assertThat(e)
                        .isInstanceOf(BusinessException.class)
                        .extracting(err -> ((DomainException) err).getTechnicalMessage())
                        .isEqualTo(TechnicalMessage.BRANCH_NOT_FOUND))
                .verify();

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldFailToCreateProductWhenAlreadyExists() {
        when(productRepository.validateForCreation("Espresso", 2L)).thenReturn(Mono.just(CreationCheck.ALREADY_EXISTS));

        StepVerifier.create(productUseCase.create("Espresso", 10, 2L))
                .expectErrorSatisfies(e -> assertThat(e)
                        .isInstanceOf(BusinessException.class)
                        .extracting(err -> ((DomainException) err).getTechnicalMessage())
                        .isEqualTo(TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                .verify();

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldDeleteProductWhenExists() {
        when(productRepository.existsByIdAndBranchId(1L, 2L)).thenReturn(Mono.just(true));
        when(productRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.delete(1L, 2L))
                .verifyComplete();

        verify(productRepository).deleteById(1L);
    }

    @Test
    void shouldFailToDeleteProductWhenNotFound() {
        when(productRepository.existsByIdAndBranchId(1L, 2L)).thenReturn(Mono.just(false));
        when(productRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.delete(1L, 2L))
                .expectErrorSatisfies(e -> assertThat(e)
                        .isInstanceOf(BusinessException.class)
                        .extracting(err -> ((DomainException) err).getTechnicalMessage())
                        .isEqualTo(TechnicalMessage.PRODUCT_NOT_FOUND))
                .verify();
    }

    @Test
    void shouldUpdateStockWhenProductExists() {
        Product product = Product.reconstitute(1L, "Espresso", 10, 2L);
        when(productRepository.findByIdAndBranchId(1L, 2L)).thenReturn(Mono.just(product));
        when(productRepository.updateStock(1L, 20)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateStock(1L, 2L, 20))
                .expectNextMatches(updated -> updated.getStock().equals(20))
                .verifyComplete();

        verify(productRepository).updateStock(1L, 20);
    }

    @Test
    void shouldFailToUpdateStockWhenProductNotFound() {
        when(productRepository.findByIdAndBranchId(1L, 2L)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateStock(1L, 2L, 20))
                .expectErrorSatisfies(e -> assertThat(e)
                        .isInstanceOf(BusinessException.class)
                        .extracting(err -> ((DomainException) err).getTechnicalMessage())
                        .isEqualTo(TechnicalMessage.PRODUCT_NOT_FOUND))
                .verify();
    }

    @Test
    void shouldUpdateNameWhenAllowed() {
        when(productRepository.validateForRename("New Name", 2L, 1L)).thenReturn(Mono.just(RenameCheck.ALLOWED));
        when(productRepository.updateName(anyLong(), anyString())).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateName(1L, 2L, "New Name"))
                .verifyComplete();

        verify(productRepository).updateName(1L, "New Name");
    }

    @Test
    void shouldFailToUpdateNameWhenProductNotFound() {
        when(productRepository.validateForRename("New Name", 2L, 1L)).thenReturn(Mono.just(RenameCheck.NOT_FOUND));

        StepVerifier.create(productUseCase.updateName(1L, 2L, "New Name"))
                .expectErrorSatisfies(e -> assertThat(e)
                        .isInstanceOf(BusinessException.class)
                        .extracting(err -> ((DomainException) err).getTechnicalMessage())
                        .isEqualTo(TechnicalMessage.PRODUCT_NOT_FOUND))
                .verify();

        verify(productRepository, never()).updateName(anyLong(), anyString());
    }

    @Test
    void shouldFailToUpdateNameWhenAlreadyExists() {
        when(productRepository.validateForRename("New Name", 2L, 1L)).thenReturn(Mono.just(RenameCheck.ALREADY_EXISTS));

        StepVerifier.create(productUseCase.updateName(1L, 2L, "New Name"))
                .expectErrorSatisfies(e -> assertThat(e)
                        .isInstanceOf(BusinessException.class)
                        .extracting(err -> ((DomainException) err).getTechnicalMessage())
                        .isEqualTo(TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                .verify();

        verify(productRepository, never()).updateName(anyLong(), anyString());
    }
}
