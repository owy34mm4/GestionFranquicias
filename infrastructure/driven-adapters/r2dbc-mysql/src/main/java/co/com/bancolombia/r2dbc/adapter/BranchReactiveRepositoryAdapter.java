package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.r2dbc.data.BranchData;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.repository.BranchReactiveRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class BranchReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<Branch, BranchData, Long, BranchReactiveRepository>
        implements BranchRepository {

    public BranchReactiveRepositoryAdapter(BranchReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Branch.class));
    }
}
