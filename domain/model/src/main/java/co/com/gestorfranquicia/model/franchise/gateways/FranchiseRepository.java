package co.com.gestorfranquicia.model.franchise.gateways;

import co.com.gestorfranquicia.model.branchtopproduct.BranchTopProduct;
import co.com.gestorfranquicia.model.enums.RenameCheck;
import co.com.gestorfranquicia.model.franchise.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(Long id);
    Flux<Franchise> findAll();
    Mono<Boolean> existsByName(String name);
    Mono<Boolean> existsById(Long id);
    Flux<BranchTopProduct> findTopStockPerBranch(Long franchiseId);
    Mono<RenameCheck> validateForRename(String newName, Long id);
    Mono<Void> updateName(Long id, String name);
}
