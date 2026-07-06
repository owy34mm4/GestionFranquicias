package co.com.gestorfranquicia.r2dbc.repository;

import co.com.gestorfranquicia.r2dbc.data.ProductData;
import org.springframework.data.r2dbc.repository.Modifying;
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

    Mono<Boolean> existsByIdAndBranchId(Long id, Long branchId);

    Mono<ProductData> findByIdAndBranchId(Long id, Long branchId);

    @Modifying
    @Query("UPDATE product SET stock = :stock WHERE id = :id")
    Mono<Void> updateStock(@Param("id") Long id, @Param("stock") Integer stock);

    @Query("SELECT CASE " +
            "WHEN NOT EXISTS (SELECT 1 FROM product WHERE id = :id AND branch_id = :branchId) THEN 'NOT_FOUND' " +
            "WHEN EXISTS (SELECT 1 FROM product WHERE name = :name AND branch_id = :branchId AND id <> :id) THEN 'ALREADY_EXISTS' " +
            "ELSE 'ALLOWED' END")
    Mono<String> validateForRename(@Param("name") String name, @Param("branchId") Long branchId, @Param("id") Long id);

    @Modifying
    @Query("UPDATE product SET name = :name WHERE id = :id")
    Mono<Void> updateName(@Param("id") Long id, @Param("name") String name);
}
