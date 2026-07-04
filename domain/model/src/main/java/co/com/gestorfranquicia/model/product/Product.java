package co.com.gestorfranquicia.model.product;

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
public class Product {
    private Long id;
    private String name;
    private Integer stock;
    private Long branchId;

    public static Product create(String name, Integer stock, Long branchId) {
        return Product.builder()
                .name(name)
                .stock(stock)
                .branchId(branchId)
                .build();
    }

    public static Product reconstitute(Long id, String name, Integer stock, Long branchId) {
        return Product.builder()
                .id(id)
                .name(name)
                .stock(stock)
                .branchId(branchId)
                .build();
    }
}
