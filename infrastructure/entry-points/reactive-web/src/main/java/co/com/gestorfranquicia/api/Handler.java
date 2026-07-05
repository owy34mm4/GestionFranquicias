package co.com.gestorfranquicia.api;

import co.com.gestorfranquicia.api.dto.BranchResponse;
import co.com.gestorfranquicia.api.dto.CreateBranchRequest;
import co.com.gestorfranquicia.api.dto.CreateFranchiseRequest;
import co.com.gestorfranquicia.api.dto.CreateProductRequest;
import co.com.gestorfranquicia.api.dto.FranchiseResponse;
import co.com.gestorfranquicia.api.dto.ProductResponse;
import co.com.gestorfranquicia.api.dto.UpdateStockRequest;
import co.com.gestorfranquicia.api.validation.RequestValidator;
import co.com.gestorfranquicia.usecase.branch.BranchUseCase;
import co.com.gestorfranquicia.usecase.franchise.FranchiseUseCase;
import co.com.gestorfranquicia.usecase.product.ProductUseCase;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class Handler {
//private  final UseCase useCase;
//private  final UseCase2 useCase2;

    private final FranchiseUseCase franchiseUseCase;
    private final BranchUseCase branchUseCase;
    private final ProductUseCase productUseCase;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
        // useCase2.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    @CircuitBreaker(name = "franchiseCreate")
    public Mono<ServerResponse> createFranchise(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateFranchiseRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(request -> franchiseUseCase.create(request.name()))
                .flatMap(franchise -> ServerResponse
                        .created(URI.create("/api/franchises/" + franchise.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new FranchiseResponse(franchise.getId(), franchise.getName())));
    }

    @CircuitBreaker(name = "branchCreate")
    public Mono<ServerResponse> createBranch(ServerRequest serverRequest) {
        Long franchiseId = Long.valueOf(serverRequest.pathVariable("franchiseId"));
        return serverRequest.bodyToMono(CreateBranchRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(request -> branchUseCase.create(request.name(), franchiseId))
                .flatMap(branch -> ServerResponse
                        .created(URI.create("/api/branches/" + branch.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new BranchResponse(branch.getId(), branch.getName(), branch.getFranchiseId())));
    }

    @CircuitBreaker(name = "productCreate")
    public Mono<ServerResponse> createProduct(ServerRequest serverRequest) {
        Long branchId = Long.valueOf(serverRequest.pathVariable("branchId"));
        return serverRequest.bodyToMono(CreateProductRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(request -> productUseCase.create(request.name(), request.stock(), branchId))
                .flatMap(product -> ServerResponse
                        .created(URI.create("/api/products/" + product.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ProductResponse(product.getId(), product.getName(), product.getStock(), product.getBranchId())));
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest serverRequest) {
        Long branchId = Long.valueOf(serverRequest.pathVariable("branchId"));
        Long productId = Long.valueOf(serverRequest.pathVariable("productId"));
        return productUseCase.delete(productId, branchId)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest serverRequest) {
        Long branchId = Long.valueOf(serverRequest.pathVariable("branchId"));
        Long productId = Long.valueOf(serverRequest.pathVariable("productId"));
        return serverRequest.bodyToMono(UpdateStockRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(request -> productUseCase.updateStock(productId, branchId, request.stock()))
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ProductResponse(product.getId(), product.getName(), product.getStock(), product.getBranchId())));
    }
}
