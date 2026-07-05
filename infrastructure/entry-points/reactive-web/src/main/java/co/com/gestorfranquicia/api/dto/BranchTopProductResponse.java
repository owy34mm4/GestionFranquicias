package co.com.gestorfranquicia.api.dto;

public record BranchTopProductResponse(Long branchId, String branchName, Long productId, String productName, Integer stock) {
}
