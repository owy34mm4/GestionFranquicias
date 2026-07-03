package co.com.bancolombia.r2dbc.repository;

import co.com.bancolombia.r2dbc.data.ProductData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductReactiveRepository
        extends ReactiveCrudRepository<ProductData, Long>, ReactiveQueryByExampleExecutor<ProductData> {
}
