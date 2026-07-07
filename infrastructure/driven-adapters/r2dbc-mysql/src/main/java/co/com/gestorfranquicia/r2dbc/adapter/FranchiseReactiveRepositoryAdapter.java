package co.com.gestorfranquicia.r2dbc.adapter;

import co.com.gestorfranquicia.model.branchtopproduct.BranchTopProduct;
import co.com.gestorfranquicia.model.enums.RenameCheck;
import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.TechnicalException;
import co.com.gestorfranquicia.model.franchise.Franchise;
import co.com.gestorfranquicia.model.franchise.gateways.FranchiseRepository;
import co.com.gestorfranquicia.r2dbc.data.BranchTopProductData;
import co.com.gestorfranquicia.r2dbc.data.FranchiseData;
import co.com.gestorfranquicia.r2dbc.repository.FranchiseReactiveRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class FranchiseReactiveRepositoryAdapter implements FranchiseRepository {

    private final FranchiseReactiveRepository repository;

    public FranchiseReactiveRepositoryAdapter(FranchiseReactiveRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return onAdapterFailure(repository.save(toData(franchise)).map(this::toEntity));
    }

    @Override
    public Mono<Franchise> findById(Long id) {
        return onAdapterFailure(repository.findById(id).map(this::toEntity));
    }

    @Override
    public Flux<Franchise> findAll() {
        return onAdapterFailure(repository.findAll().map(this::toEntity));
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return onAdapterFailure(repository.existsByName(name));
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return onAdapterFailure(repository.existsById(id));
    }

    @Override
    public Flux<BranchTopProduct> findTopStockPerBranch(Long franchiseId) {
        return onAdapterFailure(repository.findTopStockPerBranch(franchiseId).map(this::toReadModel));
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

    private BranchTopProduct toReadModel(BranchTopProductData data) {
        return BranchTopProduct.reconstitute(data.getBranchId(), data.getBranchName(),
                data.getProductId(), data.getProductName(), data.getStock());
    }

    private FranchiseData toData(Franchise franchise) {
        return FranchiseData.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .build();
    }

    private Franchise toEntity(FranchiseData data) {
        return Franchise.reconstitute(data.getId(), data.getName());
    }
}
