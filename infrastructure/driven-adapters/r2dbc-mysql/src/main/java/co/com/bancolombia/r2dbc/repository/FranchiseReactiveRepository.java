package co.com.bancolombia.r2dbc.repository;

import co.com.bancolombia.r2dbc.data.FranchiseData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface FranchiseReactiveRepository
        extends ReactiveCrudRepository<FranchiseData, Long>, ReactiveQueryByExampleExecutor<FranchiseData> {
}
