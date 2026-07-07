package co.com.gestorfranquicia.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {

    INTERNAL_ERROR("500", "Something went wrong, please try again", ""),
    SERVICE_UNAVAILABLE("503", "Service temporarily unavailable, please try again", ""),
    INVALID_REQUEST("400", "Bad Request, please verify data", ""),
    INVALID_PARAMETERS(INVALID_REQUEST.getCode(), "Bad Parameters, please verify data", ""),
    FRANCHISE_NAME_REQUIRED("400", "Franchise name is required", "name"),
    FRANCHISE_ALREADY_EXISTS("409", "Franchise already exists", "name"),
    FRANCHISE_CREATED("201", "Franchise created successfully", ""),
    BRANCH_NAME_REQUIRED("400", "Branch name is required", "name"),
    FRANCHISE_NOT_FOUND("404", "Franchise not found", "franchiseId"),
    BRANCH_ALREADY_EXISTS("409", "Branch already exists in this franchise", "name"),
    PRODUCT_NAME_REQUIRED("400", "Product name is required", "name"),
    PRODUCT_STOCK_INVALID("400", "Product stock must be zero or positive", "stock"),
    BRANCH_NOT_FOUND("404", "Branch not found", "branchId"),
    PRODUCT_ALREADY_EXISTS("409", "Product already exists in this branch", "name"),
    PRODUCT_NOT_FOUND("404", "Product not found", "productId");

    private final String code;
    private final String message;
    private final String param;
}
