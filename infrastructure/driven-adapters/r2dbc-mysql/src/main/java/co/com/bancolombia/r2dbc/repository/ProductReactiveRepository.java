package co.com.bancolombia.r2dbc.repository;

import co.com.bancolombia.r2dbc.data.ProductData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ProductReactiveRepository extends R2dbcRepository<ProductData, Long> {
}
