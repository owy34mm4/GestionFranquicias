package co.com.gestorfranquicia.r2dbc.repository;

import co.com.gestorfranquicia.r2dbc.data.BranchTopProductData;
import co.com.gestorfranquicia.r2dbc.data.FranchiseData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseReactiveRepository extends R2dbcRepository<FranchiseData, Long> {
    Mono<Boolean> existsByName(String name);

    @Query("SELECT branch_id, branch_name, product_id, product_name, stock FROM ( " +
            "SELECT b.id AS branch_id, b.name AS branch_name, p.id AS product_id, p.name AS product_name, p.stock AS stock, " +
            "ROW_NUMBER() OVER (PARTITION BY b.id ORDER BY p.stock DESC, p.id ASC) AS rn " +
            "FROM branch b JOIN product p ON p.branch_id = b.id " +
            "WHERE b.franchise_id = :franchiseId) ranked " +
            "WHERE ranked.rn = 1 ORDER BY branch_id")
    Flux<BranchTopProductData> findTopStockPerBranch(@Param("franchiseId") Long franchiseId);
}
