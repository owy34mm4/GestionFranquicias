package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import co.com.bancolombia.r2dbc.data.ProductData;
import co.com.bancolombia.r2dbc.repository.ProductReactiveRepository;
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
        return repository.save(toData(product)).map(this::toEntity);
    }

    @Override
    public Mono<Product> findById(Long id) {
        return repository.findById(id).map(this::toEntity);
    }

    @Override
    public Flux<Product> findAll() {
        return repository.findAll().map(this::toEntity);
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
