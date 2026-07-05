package co.com.gestorfranquicia.api;

import co.com.gestorfranquicia.api.dto.CreateFranchiseRequest;
import co.com.gestorfranquicia.api.dto.FranchiseResponse;
import co.com.gestorfranquicia.api.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @RouterOperations({
            @RouterOperation(
                    path = "/api/franchises",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Create a franchise",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = CreateFranchiseRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Franchise created",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid request",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "409", description = "Franchise already exists",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "503", description = "Service unavailable",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            )
    })
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET("/api/usecase/path"), handler::listenGETUseCase)
                .andRoute(POST("/api/usecase/otherpath"), handler::listenPOSTUseCase)
                .and(route(GET("/api/otherusercase/path"), handler::listenGETOtherUseCase))
                .andRoute(POST("/api/franchises"), handler::createFranchise)
                .andRoute(POST("/api/franchises/{franchiseId}/branches"), handler::createBranch)
                .andRoute(POST("/api/branches/{branchId}/products"), handler::createProduct);
    }
}
