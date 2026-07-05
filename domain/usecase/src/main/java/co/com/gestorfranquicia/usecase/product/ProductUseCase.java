package co.com.gestorfranquicia.usecase.product;

import co.com.gestorfranquicia.model.product.Product;
import co.com.gestorfranquicia.model.product.gateways.ProductRepository;
import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductUseCase {

    private final ProductRepository productRepository;

    public Mono<Product> create(String name, Integer stock, Long branchId) {
        return productRepository.validateForCreation(name, branchId)
                .flatMap(check -> switch (check) {
                    case PARENT_NOT_FOUND -> Mono.<Product>error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND));
                    case ALREADY_EXISTS -> Mono.<Product>error(new BusinessException(TechnicalMessage.PRODUCT_ALREADY_EXISTS));
                    case ALLOWED -> productRepository.save(Product.create(name, stock, branchId));
                });
    }

    public Mono<Void> delete(Long productId, Long branchId) {
        return productRepository.existsByIdAndBranchId(productId, branchId)
                .filter(exists -> exists)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND)))
                .then(productRepository.deleteById(productId));
    }

    public Mono<Product> updateStock(Long productId, Long branchId, Integer newStock) {
        return productRepository.findByIdAndBranchId(productId, branchId)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND)))
                .flatMap(product -> Mono.fromCallable(() -> product.changeStock(newStock)))
                .flatMap(updated -> productRepository.updateStock(updated.getId(), updated.getStock()).thenReturn(updated));
    }
}
