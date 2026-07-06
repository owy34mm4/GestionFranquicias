package co.com.gestorfranquicia.model.product.gateways;

import co.com.gestorfranquicia.model.enums.CreationCheck;
import co.com.gestorfranquicia.model.enums.RenameCheck;
import co.com.gestorfranquicia.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> save(Product product);
    Mono<Product> findById(Long id);
    Flux<Product> findAll();
    Mono<CreationCheck> validateForCreation(String name, Long branchId);
    Mono<Boolean> existsByIdAndBranchId(Long id, Long branchId);
    Mono<Void> deleteById(Long id);
    Mono<Product> findByIdAndBranchId(Long id, Long branchId);
    Mono<Void> updateStock(Long id, Integer stock);
    Mono<RenameCheck> validateForRename(String newName, Long branchId, Long id);
    Mono<Void> updateName(Long id, String name);
}
