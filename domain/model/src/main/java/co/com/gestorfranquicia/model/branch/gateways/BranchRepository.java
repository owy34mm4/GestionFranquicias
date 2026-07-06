package co.com.gestorfranquicia.model.branch.gateways;

import co.com.gestorfranquicia.model.branch.Branch;
import co.com.gestorfranquicia.model.enums.CreationCheck;
import co.com.gestorfranquicia.model.enums.RenameCheck;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository {
    Mono<Branch> save(Branch branch);
    Mono<Branch> findById(Long id);
    Flux<Branch> findAll();
    Mono<CreationCheck> validateForCreation(String name, Long franchiseId);
    Mono<RenameCheck> validateForRename(String newName, Long id);
    Mono<Void> updateName(Long id, String name);
}
