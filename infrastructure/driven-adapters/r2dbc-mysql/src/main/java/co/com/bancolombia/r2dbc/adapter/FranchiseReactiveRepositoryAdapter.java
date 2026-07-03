package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.r2dbc.data.FranchiseData;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.repository.FranchiseReactiveRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FranchiseReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<Franchise, FranchiseData, Long, FranchiseReactiveRepository>
        implements FranchiseRepository {

    public FranchiseReactiveRepositoryAdapter(FranchiseReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Franchise.class));
    }
}
