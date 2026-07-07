package co.com.gestorfranquicia.r2dbc.adapter;

import co.com.gestorfranquicia.model.enums.CreationCheck;
import co.com.gestorfranquicia.model.enums.RenameCheck;
import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.TechnicalException;
import co.com.gestorfranquicia.model.product.Product;
import co.com.gestorfranquicia.model.product.gateways.ProductRepository;
import co.com.gestorfranquicia.r2dbc.data.ProductData;
import co.com.gestorfranquicia.r2dbc.repository.ProductReactiveRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ProductReactiveRepositoryAdapter implements ProductRepository {

    private final ProductReactiveRepository repository;

    public ProductReactiveRepositoryAdapter(ProductReactiveRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Product> save(Product product) {
        return onAdapterFailure(repository.save(toData(product)).map(this::toEntity));
    }

    @Override
    public Mono<Product> findById(Long id) {
        return onAdapterFailure(repository.findById(id).map(this::toEntity));
    }

    @Override
    public Flux<Product> findAll() {
        return onAdapterFailure(repository.findAll().map(this::toEntity));
    }

    @Override
    public Mono<CreationCheck> validateForCreation(String name, Long branchId) {
        return onAdapterFailure(repository.validateForCreation(name, branchId).map(CreationCheck::valueOf));
    }

    @Override
    public Mono<Boolean> existsByIdAndBranchId(Long id, Long branchId) {
        return onAdapterFailure(repository.existsByIdAndBranchId(id, branchId));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return onAdapterFailure(repository.deleteById(id));
    }

    @Override
    public Mono<Product> findByIdAndBranchId(Long id, Long branchId) {
        return onAdapterFailure(repository.findByIdAndBranchId(id, branchId).map(this::toEntity));
    }

    @Override
    public Mono<Void> updateStock(Long id, Integer stock) {
        return onAdapterFailure(repository.updateStock(id, stock));
    }

    @Override
    public Mono<RenameCheck> validateForRename(String newName, Long branchId, Long id) {
        return onAdapterFailure(repository.validateForRename(newName, branchId, id).map(RenameCheck::valueOf));
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

    private ProductData toData(Product product) {
        return ProductData.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .branchId(product.getBranchId())
                .build();
    }

    private Product toEntity(ProductData data) {
        return Product.reconstitute(data.getId(), data.getName(), data.getStock(), data.getBranchId());
    }
}
