package co.com.gestorfranquicia.api.config;

import co.com.gestorfranquicia.api.Handler;
import co.com.gestorfranquicia.api.RouterRest;
import co.com.gestorfranquicia.api.validation.RequestValidator;
import co.com.gestorfranquicia.usecase.branch.BranchUseCase;
import co.com.gestorfranquicia.usecase.franchise.FranchiseUseCase;
import co.com.gestorfranquicia.usecase.product.ProductUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
@TestPropertySource(properties = "spring.test.webtestclient.timeout=30s")
class ConfigTest {

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

    @Test
    void corsConfigurationShouldAllowOrigins() {
        when(franchiseUseCase.topStockPerBranch(1L)).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/franchise/1/top-stock-product")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}