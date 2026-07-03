package co.com.bancolombia.r2dbc.repository;

import co.com.bancolombia.r2dbc.data.FranchiseData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FranchiseReactiveRepository extends R2dbcRepository<FranchiseData, Long> {
}
