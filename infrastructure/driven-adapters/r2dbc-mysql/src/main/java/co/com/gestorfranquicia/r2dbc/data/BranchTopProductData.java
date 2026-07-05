package co.com.gestorfranquicia.r2dbc.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BranchTopProductData {
    @Column("branch_id")
    private Long branchId;
    @Column("branch_name")
    private String branchName;
    @Column("product_id")
    private Long productId;
    @Column("product_name")
    private String productName;
    @Column("stock")
    private Integer stock;
}
