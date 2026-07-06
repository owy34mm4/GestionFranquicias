package co.com.gestorfranquicia.r2dbc.adapter;

import co.com.gestorfranquicia.model.enums.RenameCheck;
import co.com.gestorfranquicia.model.franchise.Franchise;
import co.com.gestorfranquicia.r2dbc.data.BranchTopProductData;
import co.com.gestorfranquicia.r2dbc.data.FranchiseData;
import co.com.gestorfranquicia.r2dbc.repository.FranchiseReactiveRepository;
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
class FranchiseReactiveRepositoryAdapterTest {

    @Mock
    private FranchiseReactiveRepository repository;

    @InjectMocks
    private FranchiseReactiveRepositoryAdapter adapter;

    @Test
    void shouldSaveAndMapToEntity() {
        Franchise franchise = Franchise.reconstitute(1L, "Coffee Shop");
        FranchiseData savedData = FranchiseData.builder().id(1L).name("Coffee Shop").build();
        when(repository.save(any(FranchiseData.class))).thenReturn(Mono.just(savedData));

        StepVerifier.create(adapter.save(franchise))
                .expectNextMatches(saved -> saved.getId().equals(1L) && saved.getName().equals("Coffee Shop"))
                .verifyComplete();

        verify(repository).save(any(FranchiseData.class));
    }

    @Test
    void shouldFindByIdAndMapToEntity() {
        FranchiseData data = FranchiseData.builder().id(1L).name("Coffee Shop").build();
        when(repository.findById(1L)).thenReturn(Mono.just(data));

        StepVerifier.create(adapter.findById(1L))
                .expectNextMatches(franchise -> franchise.getId().equals(1L))
                .verifyComplete();

        verify(repository).findById(1L);
    }

    @Test
    void shouldFindAllAndMapToEntities() {
        FranchiseData data = FranchiseData.builder().id(1L).name("Coffee Shop").build();
        when(repository.findAll()).thenReturn(Flux.just(data));

        StepVerifier.create(adapter.findAll())
                .expectNextMatches(franchise -> franchise.getName().equals("Coffee Shop"))
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void shouldDelegateExistsByName() {
        when(repository.existsByName("Coffee Shop")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByName("Coffee Shop"))
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsByName("Coffee Shop");
    }

    @Test
    void shouldDelegateExistsById() {
        when(repository.existsById(1L)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsById(1L))
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsById(1L);
    }

    @Test
    void shouldFindTopStockPerBranchAndMapToReadModel() {
        BranchTopProductData data = BranchTopProductData.builder()
                .branchId(1L).branchName("Downtown").productId(2L).productName("Espresso").stock(10)
                .build();
        when(repository.findTopStockPerBranch(1L)).thenReturn(Flux.just(data));

        StepVerifier.create(adapter.findTopStockPerBranch(1L))
                .expectNextMatches(readModel -> readModel.getProductName().equals("Espresso"))
                .verifyComplete();

        verify(repository).findTopStockPerBranch(1L);
    }

    @Test
    void shouldUpdateName() {
        when(repository.updateName(1L, "New Name")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.updateName(1L, "New Name"))
                .verifyComplete();

        verify(repository).updateName(1L, "New Name");
    }

    @Test
    void shouldMapValidateForRenameAllowed() {
        when(repository.validateForRename("New Name", 1L)).thenReturn(Mono.just("ALLOWED"));

        StepVerifier.create(adapter.validateForRename("New Name", 1L))
                .expectNext(RenameCheck.ALLOWED)
                .verifyComplete();
    }

    @Test
    void shouldMapValidateForRenameNotFound() {
        when(repository.validateForRename("New Name", 1L)).thenReturn(Mono.just("NOT_FOUND"));

        StepVerifier.create(adapter.validateForRename("New Name", 1L))
                .expectNext(RenameCheck.NOT_FOUND)
                .verifyComplete();
    }
}
