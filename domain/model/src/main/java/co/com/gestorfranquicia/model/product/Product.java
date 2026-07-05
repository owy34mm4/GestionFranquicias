package co.com.gestorfranquicia.model.product;

import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.BusinessException;
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
        if (name == null || name.isBlank()) {
            throw new BusinessException(TechnicalMessage.PRODUCT_NAME_REQUIRED);
        }
        if (stock == null || stock < 0) {
            throw new BusinessException(TechnicalMessage.PRODUCT_STOCK_INVALID);
        }
        return Product.builder()
                .name(name.trim())
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
