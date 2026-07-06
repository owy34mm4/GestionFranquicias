package co.com.gestorfranquicia.r2dbc.adapter;

import co.com.gestorfranquicia.model.enums.CreationCheck;
import co.com.gestorfranquicia.model.enums.RenameCheck;
import co.com.gestorfranquicia.model.product.Product;
import co.com.gestorfranquicia.r2dbc.data.ProductData;
import co.com.gestorfranquicia.r2dbc.repository.ProductReactiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductReactiveRepositoryAdapterTest {

    @Mock
    private ProductReactiveRepository repository;

    @InjectMocks
    private ProductReactiveRepositoryAdapter adapter;

    @Test
    void shouldSaveAndMapToEntity() {
        Product product = Product.reconstitute(1L, "Espresso", 10, 2L);
        ProductData savedData = ProductData.builder().id(1L).name("Espresso").stock(10).branchId(2L).build();
        when(repository.save(any(ProductData.class))).thenReturn(Mono.just(savedData));

        StepVerifier.create(adapter.save(product))
                .expectNextMatches(saved -> saved.getId().equals(1L) && saved.getName().equals("Espresso"))
                .verifyComplete();

        verify(repository).save(any(ProductData.class));
    }

    @Test
    void shouldFindByIdAndMapToEntity() {
        ProductData data = ProductData.builder().id(1L).name("Espresso").stock(10).branchId(2L).build();
        when(repository.findById(1L)).thenReturn(Mono.just(data));

        StepVerifier.create(adapter.findById(1L))
                .expectNextMatches(product -> product.getId().equals(1L))
                .verifyComplete();

        verify(repository).findById(1L);
    }

    @Test
    void shouldFindAllAndMapToEntities() {
        ProductData data = ProductData.builder().id(1L).name("Espresso").stock(10).branchId(2L).build();
        when(repository.findAll()).thenReturn(Flux.just(data));

        StepVerifier.create(adapter.findAll())
                .expectNextMatches(product -> product.getName().equals("Espresso"))
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void shouldDelegateExistsByIdAndBranchId() {
        when(repository.existsByIdAndBranchId(1L, 2L)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByIdAndBranchId(1L, 2L))
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsByIdAndBranchId(1L, 2L);
    }

    @Test
    void shouldDelegateDeleteById() {
        when(repository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById(1L))
                .verifyComplete();

        verify(repository).deleteById(1L);
    }

    @Test
    void shouldFindByIdAndBranchIdAndMapToEntity() {
        ProductData data = ProductData.builder().id(1L).name("Espresso").stock(10).branchId(2L).build();
        when(repository.findByIdAndBranchId(1L, 2L)).thenReturn(Mono.just(data));

        StepVerifier.create(adapter.findByIdAndBranchId(1L, 2L))
                .expectNextMatches(product -> product.getBranchId().equals(2L))
                .verifyComplete();

        verify(repository).findByIdAndBranchId(1L, 2L);
    }

    @Test
    void shouldDelegateUpdateStock() {
        when(repository.updateStock(1L, 20)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.updateStock(1L, 20))
                .verifyComplete();

        verify(repository).updateStock(1L, 20);
    }

    @Test
    void shouldUpdateName() {
        when(repository.updateName(1L, "New Name")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.updateName(1L, "New Name"))
                .verifyComplete();

        verify(repository).updateName(1L, "New Name");
    }

    @Test
    void shouldMapValidateForCreationAllowed() {
        when(repository.validateForCreation("Espresso", 2L)).thenReturn(Mono.just("ALLOWED"));

        StepVerifier.create(adapter.validateForCreation("Espresso", 2L))
                .expectNext(CreationCheck.ALLOWED)
                .verifyComplete();
    }

    @Test
    void shouldMapValidateForCreationAlreadyExists() {
        when(repository.validateForCreation("Espresso", 2L)).thenReturn(Mono.just("ALREADY_EXISTS"));

        StepVerifier.create(adapter.validateForCreation("Espresso", 2L))
                .expectNext(CreationCheck.ALREADY_EXISTS)
                .verifyComplete();
    }

    @Test
    void shouldMapValidateForRenameAllowed() {
        when(repository.validateForRename("New Name", 2L, 1L)).thenReturn(Mono.just("ALLOWED"));

        StepVerifier.create(adapter.validateForRename("New Name", 2L, 1L))
                .expectNext(RenameCheck.ALLOWED)
                .verifyComplete();
    }

    @Test
    void shouldMapValidateForRenameNotFound() {
        when(repository.validateForRename("New Name", 2L, 1L)).thenReturn(Mono.just("NOT_FOUND"));

        StepVerifier.create(adapter.validateForRename("New Name", 2L, 1L))
                .expectNext(RenameCheck.NOT_FOUND)
                .verifyComplete();
    }
}
