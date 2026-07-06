package co.com.gestorfranquicia.usecase.branch;

import co.com.gestorfranquicia.model.branch.Branch;
import co.com.gestorfranquicia.model.branch.gateways.BranchRepository;
import co.com.gestorfranquicia.model.enums.CreationCheck;
import co.com.gestorfranquicia.model.enums.RenameCheck;
import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.BusinessException;
import co.com.gestorfranquicia.model.exception.DomainException;
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
class BranchUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private BranchUseCase branchUseCase;

    @Test
    void shouldCreateBranchWhenAllowed() {
        Branch saved = Branch.reconstitute(1L, "Downtown", 2L);
        when(branchRepository.validateForCreation("Downtown", 2L)).thenReturn(Mono.just(CreationCheck.ALLOWED));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(branchUseCase.create("Downtown", 2L))
                .expectNext(saved)
                .verifyComplete();

        verify(branchRepository).save(any(Branch.class));
    }

    @Test
    void shouldFailToCreateBranchWhenFranchiseNotFound() {
        when(branchRepository.validateForCreation("Downtown", 2L)).thenReturn(Mono.just(CreationCheck.PARENT_NOT_FOUND));

        StepVerifier.create(branchUseCase.create("Downtown", 2L))
                .expectErrorSatisfies(e -> assertThat(e)
                        .isInstanceOf(BusinessException.class)
                        .extracting(err -> ((DomainException) err).getTechnicalMessage())
                        .isEqualTo(TechnicalMessage.FRANCHISE_NOT_FOUND))
                .verify();

        verify(branchRepository, never()).save(any());
    }

    @Test
    void shouldFailToCreateBranchWhenAlreadyExists() {
        when(branchRepository.validateForCreation("Downtown", 2L)).thenReturn(Mono.just(CreationCheck.ALREADY_EXISTS));

        StepVerifier.create(branchUseCase.create("Downtown", 2L))
                .expectErrorSatisfies(e -> assertThat(e)
                        .isInstanceOf(BusinessException.class)
                        .extracting(err -> ((DomainException) err).getTechnicalMessage())
                        .isEqualTo(TechnicalMessage.BRANCH_ALREADY_EXISTS))
                .verify();

        verify(branchRepository, never()).save(any());
    }

    @Test
    void shouldUpdateNameWhenAllowed() {
        when(branchRepository.validateForRename("New Name", 1L)).thenReturn(Mono.just(RenameCheck.ALLOWED));
        when(branchRepository.updateName(anyLong(), anyString())).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.updateName(1L, "New Name"))
                .verifyComplete();

        verify(branchRepository).updateName(1L, "New Name");
    }

    @Test
    void shouldFailToUpdateNameWhenBranchNotFound() {
        when(branchRepository.validateForRename("New Name", 1L)).thenReturn(Mono.just(RenameCheck.NOT_FOUND));

        StepVerifier.create(branchUseCase.updateName(1L, "New Name"))
                .expectErrorSatisfies(e -> assertThat(e)
                        .isInstanceOf(BusinessException.class)
                        .extracting(err -> ((DomainException) err).getTechnicalMessage())
                        .isEqualTo(TechnicalMessage.BRANCH_NOT_FOUND))
                .verify();

        verify(branchRepository, never()).updateName(anyLong(), anyString());
    }

    @Test
    void shouldFailToUpdateNameWhenAlreadyExists() {
        when(branchRepository.validateForRename("New Name", 1L)).thenReturn(Mono.just(RenameCheck.ALREADY_EXISTS));

        StepVerifier.create(branchUseCase.updateName(1L, "New Name"))
                .expectErrorSatisfies(e -> assertThat(e)
                        .isInstanceOf(BusinessException.class)
                        .extracting(err -> ((DomainException) err).getTechnicalMessage())
                        .isEqualTo(TechnicalMessage.BRANCH_ALREADY_EXISTS))
                .verify();

        verify(branchRepository, never()).updateName(anyLong(), anyString());
    }
}
