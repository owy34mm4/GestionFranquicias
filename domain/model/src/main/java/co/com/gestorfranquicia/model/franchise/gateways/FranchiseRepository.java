package co.com.gestorfranquicia.model.franchise.gateways;

import co.com.gestorfranquicia.model.franchise.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(Long id);
    Flux<Franchise> findAll();
}
