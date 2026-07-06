package co.com.gestorfranquicia.r2dbc.adapter;

import co.com.gestorfranquicia.model.branch.Branch;
import co.com.gestorfranquicia.model.enums.CreationCheck;
import co.com.gestorfranquicia.model.enums.RenameCheck;
import co.com.gestorfranquicia.r2dbc.data.BranchData;
import co.com.gestorfranquicia.r2dbc.repository.BranchReactiveRepository;
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
class BranchReactiveRepositoryAdapterTest {

    @Mock
    private BranchReactiveRepository repository;

    @InjectMocks
    private BranchReactiveRepositoryAdapter adapter;

    @Test
    void shouldSaveAndMapToEntity() {
        Branch branch = Branch.reconstitute(1L, "Downtown", 2L);
        BranchData savedData = BranchData.builder().id(1L).name("Downtown").franchiseId(2L).build();
        when(repository.save(any(BranchData.class))).thenReturn(Mono.just(savedData));

        StepVerifier.create(adapter.save(branch))
                .expectNextMatches(saved -> saved.getId().equals(1L) && saved.getName().equals("Downtown"))
                .verifyComplete();

        verify(repository).save(any(BranchData.class));
    }

    @Test
    void shouldFindByIdAndMapToEntity() {
        BranchData data = BranchData.builder().id(1L).name("Downtown").franchiseId(2L).build();
        when(repository.findById(1L)).thenReturn(Mono.just(data));

        StepVerifier.create(adapter.findById(1L))
                .expectNextMatches(branch -> branch.getId().equals(1L))
                .verifyComplete();

        verify(repository).findById(1L);
    }

    @Test
    void shouldFindAllAndMapToEntities() {
        BranchData data = BranchData.builder().id(1L).name("Downtown").franchiseId(2L).build();
        when(repository.findAll()).thenReturn(Flux.just(data));

        StepVerifier.create(adapter.findAll())
                .expectNextMatches(branch -> branch.getName().equals("Downtown"))
                .verifyComplete();

        verify(repository).findAll();
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
        when(repository.validateForCreation("Downtown", 2L)).thenReturn(Mono.just("ALLOWED"));

        StepVerifier.create(adapter.validateForCreation("Downtown", 2L))
                .expectNext(CreationCheck.ALLOWED)
                .verifyComplete();
    }

    @Test
    void shouldMapValidateForCreationParentNotFound() {
        when(repository.validateForCreation("Downtown", 2L)).thenReturn(Mono.just("PARENT_NOT_FOUND"));

        StepVerifier.create(adapter.validateForCreation("Downtown", 2L))
                .expectNext(CreationCheck.PARENT_NOT_FOUND)
                .verifyComplete();
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
