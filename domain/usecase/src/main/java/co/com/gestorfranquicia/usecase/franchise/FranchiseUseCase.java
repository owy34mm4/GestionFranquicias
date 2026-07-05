package co.com.gestorfranquicia.usecase.franchise;

import co.com.gestorfranquicia.model.branchtopproduct.BranchTopProduct;
import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.BusinessException;
import co.com.gestorfranquicia.model.franchise.Franchise;
import co.com.gestorfranquicia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> create(String name) {
        return franchiseRepository.existsByName(name)
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_ALREADY_EXISTS)))
                .then(Mono.fromCallable(() -> Franchise.create(name)))
                .flatMap(franchiseRepository::save);
    }

    public Flux<BranchTopProduct> topStockPerBranch(Long franchiseId) {
        return franchiseRepository.existsById(franchiseId)
                .filter(exists -> exists)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND)))
                .thenMany(franchiseRepository.findTopStockPerBranch(franchiseId));
    }
}
