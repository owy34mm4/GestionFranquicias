package co.com.gestorfranquicia.model.product.gateways;

import co.com.gestorfranquicia.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> save(Product product);
    Mono<Product> findById(Long id);
    Flux<Product> findAll();
}
