package co.com.gestorfranquicia.api;

import co.com.gestorfranquicia.api.dto.CreateBranchRequest;
import co.com.gestorfranquicia.api.dto.CreateFranchiseRequest;
import co.com.gestorfranquicia.api.dto.CreateProductRequest;
import co.com.gestorfranquicia.api.dto.UpdateNameRequest;
import co.com.gestorfranquicia.api.dto.UpdateStockRequest;
import co.com.gestorfranquicia.api.exception.GlobalExceptionHandler;
import co.com.gestorfranquicia.api.validation.RequestValidator;
import co.com.gestorfranquicia.model.branch.Branch;
import co.com.gestorfranquicia.model.branchtopproduct.BranchTopProduct;
import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.BusinessException;
import co.com.gestorfranquicia.model.franchise.Franchise;
import co.com.gestorfranquicia.model.product.Product;
import co.com.gestorfranquicia.usecase.branch.BranchUseCase;
import co.com.gestorfranquicia.usecase.franchise.FranchiseUseCase;
import co.com.gestorfranquicia.usecase.product.ProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, GlobalExceptionHandler.class})
@WebFluxTest
@TestPropertySource(properties = "spring.test.webtestclient.timeout=30s")
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FranchiseUseCase franchiseUseCase;

    @MockitoBean
    private BranchUseCase branchUseCase;

    @MockitoBean
    private ProductUseCase productUseCase;

    @MockitoBean
    private RequestValidator requestValidator;

    @BeforeEach
    void setUp() {
        when(requestValidator.validate(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
    }

    @Test
    void shouldCreateFranchise() {
        when(franchiseUseCase.create("Coffee Shop")).thenReturn(Mono.just(Franchise.reconstitute(1L, "Coffee Shop")));

        webTestClient.post()
                .uri("/api/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateFranchiseRequest("Coffee Shop"))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/api/franchise/1")
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Coffee Shop");
    }

    @Test
    void shouldFailToCreateFranchiseWhenAlreadyExists() {
        when(franchiseUseCase.create("Coffee Shop"))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_ALREADY_EXISTS)));

        webTestClient.post()
                .uri("/api/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateFranchiseRequest("Coffee Shop"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.code").isEqualTo("409")
                .jsonPath("$.message").isEqualTo("Franchise already exists");
    }

    @Test
    void shouldCreateBranch() {
        when(branchUseCase.create("Downtown", 1L)).thenReturn(Mono.just(Branch.reconstitute(2L, "Downtown", 1L)));

        webTestClient.post()
                .uri("/api/franchise/1/branch")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateBranchRequest("Downtown"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(2)
                .jsonPath("$.franchiseId").isEqualTo(1);
    }

    @Test
    void shouldFailToCreateBranchWhenFranchiseNotFound() {
        when(branchUseCase.create("Downtown", 1L))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND)));

        webTestClient.post()
                .uri("/api/franchise/1/branch")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateBranchRequest("Downtown"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("404");
    }

    @Test
    void shouldCreateProduct() {
        when(productUseCase.create("Espresso", 10, 2L)).thenReturn(Mono.just(Product.reconstitute(3L, "Espresso", 10, 2L)));

        webTestClient.post()
                .uri("/api/branch/2/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateProductRequest("Espresso", 10))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.stock").isEqualTo(10);
    }

    @Test
    void shouldFailToCreateProductWhenBranchNotFound() {
        when(productUseCase.create("Espresso", 10, 2L))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND)));

        webTestClient.post()
                .uri("/api/branch/2/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateProductRequest("Espresso", 10))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("404");
    }

    @Test
    void shouldDeleteProduct() {
        when(productUseCase.delete(3L, 2L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/branch/2/product/3")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldFailToDeleteProductWhenNotFound() {
        when(productUseCase.delete(3L, 2L))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND)));

        webTestClient.delete()
                .uri("/api/branch/2/product/3")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("404");
    }

    @Test
    void shouldUpdateProductStock() {
        when(productUseCase.updateStock(3L, 2L, 20)).thenReturn(Mono.just(Product.reconstitute(3L, "Espresso", 20, 2L)));

        webTestClient.patch()
                .uri("/api/branch/2/product/3/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateStockRequest(20))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stock").isEqualTo(20);
    }

    @Test
    void shouldFailToUpdateProductStockWhenNotFound() {
        when(productUseCase.updateStock(3L, 2L, 20))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND)));

        webTestClient.patch()
                .uri("/api/branch/2/product/3/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateStockRequest(20))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("404");
    }

    @Test
    void shouldGetTopStockPerBranch() {
        BranchTopProduct topProduct = BranchTopProduct.reconstitute(2L, "Downtown", 3L, "Espresso", 10);
        when(franchiseUseCase.topStockPerBranch(1L)).thenReturn(Flux.just(topProduct));

        webTestClient.get()
                .uri("/api/franchise/1/top-stock-product")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].productName").isEqualTo("Espresso");
    }

    @Test
    void shouldFailToGetTopStockPerBranchWhenFranchiseNotFound() {
        when(franchiseUseCase.topStockPerBranch(1L))
                .thenReturn(Flux.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND)));

        webTestClient.get()
                .uri("/api/franchise/1/top-stock-product")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("404");
    }

    @Test
    void shouldUpdateFranchiseName() {
        when(franchiseUseCase.updateName(1L, "New Name")).thenReturn(Mono.empty());

        webTestClient.patch()
                .uri("/api/franchise/1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameRequest("New Name"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldFailToUpdateFranchiseNameWhenNotFound() {
        when(franchiseUseCase.updateName(1L, "New Name"))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND)));

        webTestClient.patch()
                .uri("/api/franchise/1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameRequest("New Name"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("404");
    }

    @Test
    void shouldUpdateBranchName() {
        when(branchUseCase.updateName(2L, "New Name")).thenReturn(Mono.empty());

        webTestClient.patch()
                .uri("/api/branch/2/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameRequest("New Name"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldFailToUpdateBranchNameWhenAlreadyExists() {
        when(branchUseCase.updateName(2L, "New Name"))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.BRANCH_ALREADY_EXISTS)));

        webTestClient.patch()
                .uri("/api/branch/2/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameRequest("New Name"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.code").isEqualTo("409");
    }

    @Test
    void shouldUpdateProductName() {
        when(productUseCase.updateName(3L, 2L, "New Name")).thenReturn(Mono.empty());

        webTestClient.patch()
                .uri("/api/branch/2/product/3/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameRequest("New Name"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldFailToUpdateProductNameWhenNotFound() {
        when(productUseCase.updateName(3L, 2L, "New Name"))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND)));

        webTestClient.patch()
                .uri("/api/branch/2/product/3/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameRequest("New Name"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("404");
    }
}
