package co.com.gestorfranquicia.r2dbc.adapter;

import co.com.gestorfranquicia.model.branch.Branch;
import co.com.gestorfranquicia.model.branch.gateways.BranchRepository;
import co.com.gestorfranquicia.model.enums.CreationCheck;
import co.com.gestorfranquicia.model.enums.RenameCheck;
import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.TechnicalException;
import co.com.gestorfranquicia.r2dbc.data.BranchData;
import co.com.gestorfranquicia.r2dbc.repository.BranchReactiveRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class BranchReactiveRepositoryAdapter implements BranchRepository {

    private final BranchReactiveRepository repository;

    public BranchReactiveRepositoryAdapter(BranchReactiveRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Branch> save(Branch branch) {
        return onAdapterFailure(repository.save(toData(branch)).map(this::toEntity));
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return onAdapterFailure(repository.findById(id).map(this::toEntity));
    }

    @Override
    public Flux<Branch> findAll() {
        return onAdapterFailure(repository.findAll().map(this::toEntity));
    }

    @Override
    public Mono<CreationCheck> validateForCreation(String name, Long franchiseId) {
        return onAdapterFailure(repository.validateForCreation(name, franchiseId).map(CreationCheck::valueOf));
    }

    @Override
    public Mono<RenameCheck> validateForRename(String newName, Long id) {
        return onAdapterFailure(repository.validateForRename(newName, id).map(RenameCheck::valueOf));
    }

    @Override
    public Mono<Void> updateName(Long id, String name) {
        return onAdapterFailure(repository.updateName(id, name));
    }

    private <T> Mono<T> onAdapterFailure(Mono<T> source) {
        return source.onErrorMap(error -> new TechnicalException(error, TechnicalMessage.INTERNAL_ERROR));
    }

    private <T> Flux<T> onAdapterFailure(Flux<T> source) {
        return source.onErrorMap(error -> new TechnicalException(error, TechnicalMessage.INTERNAL_ERROR));
    }

    private BranchData toData(Branch branch) {
        return BranchData.builder()
                .id(branch.getId())
                .name(branch.getName())
                .franchiseId(branch.getFranchiseId())
                .build();
    }

    private Branch toEntity(BranchData data) {
        return Branch.reconstitute(data.getId(), data.getName(), data.getFranchiseId());
    }
}
