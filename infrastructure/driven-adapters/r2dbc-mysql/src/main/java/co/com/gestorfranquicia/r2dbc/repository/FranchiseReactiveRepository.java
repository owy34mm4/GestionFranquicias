package co.com.gestorfranquicia.r2dbc.repository;

import co.com.gestorfranquicia.r2dbc.data.FranchiseData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface FranchiseReactiveRepository extends R2dbcRepository<FranchiseData, Long> {
    Mono<Boolean> existsByName(String name);
}
