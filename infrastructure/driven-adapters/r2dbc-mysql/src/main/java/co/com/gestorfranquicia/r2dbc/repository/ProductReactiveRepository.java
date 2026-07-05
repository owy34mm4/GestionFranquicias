package co.com.gestorfranquicia.r2dbc.repository;

import co.com.gestorfranquicia.r2dbc.data.ProductData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

public interface ProductReactiveRepository extends R2dbcRepository<ProductData, Long> {

    @Query("SELECT CASE " +
            "WHEN NOT EXISTS (SELECT 1 FROM branch WHERE id = :branchId) THEN 'PARENT_NOT_FOUND' " +
            "WHEN EXISTS (SELECT 1 FROM product WHERE name = :name AND branch_id = :branchId) THEN 'ALREADY_EXISTS' " +
            "ELSE 'ALLOWED' END")
    Mono<String> validateForCreation(@Param("name") String name, @Param("branchId") Long branchId);
}
