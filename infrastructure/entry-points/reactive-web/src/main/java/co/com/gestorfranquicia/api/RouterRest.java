package co.com.gestorfranquicia.api;

import co.com.gestorfranquicia.api.dto.BranchResponse;
import co.com.gestorfranquicia.api.dto.CreateBranchRequest;
import co.com.gestorfranquicia.api.dto.CreateFranchiseRequest;
import co.com.gestorfranquicia.api.dto.CreateProductRequest;
import co.com.gestorfranquicia.api.dto.FranchiseResponse;
import co.com.gestorfranquicia.api.dto.ProductResponse;
import co.com.gestorfranquicia.api.dto.UpdateStockRequest;
import co.com.gestorfranquicia.api.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
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
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/branches",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createBranch",
                    operation = @Operation(
                            operationId = "createBranch",
                            summary = "Add a branch to a franchise",
                            parameters = {
                                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true)
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = CreateBranchRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Branch created",
                                            content = @Content(schema = @Schema(implementation = BranchResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid request",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "409", description = "Branch already exists",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "503", description = "Service unavailable",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/products",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createProduct",
                    operation = @Operation(
                            operationId = "createProduct",
                            summary = "Add a product to a branch",
                            parameters = {
                                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true)
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = CreateProductRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Product created",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid request",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Branch not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "409", description = "Product already exists",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "503", description = "Service unavailable",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/products/{productId}",
                    method = RequestMethod.DELETE,
                    beanClass = Handler.class,
                    beanMethod = "deleteProduct",
                    operation = @Operation(
                            operationId = "deleteProduct",
                            summary = "Delete a product from a branch",
                            parameters = {
                                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true),
                                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Product deleted"),
                                    @ApiResponse(responseCode = "404", description = "Product not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "503", description = "Service unavailable",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/products/{productId}/stock",
                    method = RequestMethod.PATCH,
                    beanClass = Handler.class,
                    beanMethod = "updateProductStock",
                    operation = @Operation(
                            operationId = "updateProductStock",
                            summary = "Update the stock of a product",
                            parameters = {
                                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true),
                                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true)
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateStockRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Stock updated",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid request",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Product not found",
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
                .andRoute(POST("/api/branches/{branchId}/products"), handler::createProduct)
                .andRoute(DELETE("/api/branches/{branchId}/products/{productId}"), handler::deleteProduct)
                .andRoute(PATCH("/api/branches/{branchId}/products/{productId}/stock"), handler::updateProductStock);
    }
}
