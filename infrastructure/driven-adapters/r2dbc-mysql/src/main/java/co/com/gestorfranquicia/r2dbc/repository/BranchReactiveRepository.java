package co.com.gestorfranquicia.r2dbc.repository;

import co.com.gestorfranquicia.r2dbc.data.BranchData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface BranchReactiveRepository extends R2dbcRepository<BranchData, Long> {
}
