package co.com.gestorfranquicia.r2dbc.repository;

import co.com.gestorfranquicia.r2dbc.data.BranchData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

public interface BranchReactiveRepository extends R2dbcRepository<BranchData, Long> {

    @Query("SELECT CASE " +
            "WHEN NOT EXISTS (SELECT 1 FROM franchise WHERE id = :franchiseId) THEN 'PARENT_NOT_FOUND' " +
            "WHEN EXISTS (SELECT 1 FROM branch WHERE name = :name AND franchise_id = :franchiseId) THEN 'ALREADY_EXISTS' " +
            "ELSE 'ALLOWED' END")
    Mono<String> validateForCreation(@Param("name") String name, @Param("franchiseId") Long franchiseId);
}
