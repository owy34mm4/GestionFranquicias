package co.com.gestorfranquicia.model.branchtopproduct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BranchTopProduct {
    private Long branchId;
    private String branchName;
    private Long productId;
    private String productName;
    private Integer stock;

    public static BranchTopProduct reconstitute(Long branchId, String branchName, Long productId, String productName, Integer stock) {
        return BranchTopProduct.builder()
                .branchId(branchId)
                .branchName(branchName)
                .productId(productId)
                .productName(productName)
                .stock(stock)
                .build();
    }
}
