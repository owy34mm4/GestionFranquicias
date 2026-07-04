package co.com.gestorfranquicia.r2dbc.repository;

import co.com.gestorfranquicia.r2dbc.data.ProductData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ProductReactiveRepository extends R2dbcRepository<ProductData, Long> {
}
