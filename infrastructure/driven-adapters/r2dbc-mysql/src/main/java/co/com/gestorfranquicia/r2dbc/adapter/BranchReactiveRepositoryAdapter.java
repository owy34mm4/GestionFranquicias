package co.com.gestorfranquicia.r2dbc.adapter;

import co.com.gestorfranquicia.model.branch.Branch;
import co.com.gestorfranquicia.model.branch.gateways.BranchRepository;
import co.com.gestorfranquicia.model.enums.CreationCheck;
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
        return repository.save(toData(branch)).map(this::toEntity);
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return repository.findById(id).map(this::toEntity);
    }

    @Override
    public Flux<Branch> findAll() {
        return repository.findAll().map(this::toEntity);
    }

    @Override
    public Mono<CreationCheck> validateForCreation(String name, Long franchiseId) {
        return repository.validateForCreation(name, franchiseId).map(CreationCheck::valueOf);
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
