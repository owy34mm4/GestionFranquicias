package co.com.gestorfranquicia.model.product;

import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.BusinessException;
import co.com.gestorfranquicia.model.exception.ProcessorException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void shouldCreateProductWithTrimmedName() {
        Product product = Product.create("  Espresso  ", 10, 1L);

        assertThat(product.getId()).isNull();
        assertThat(product.getName()).isEqualTo("Espresso");
        assertThat(product.getStock()).isEqualTo(10);
        assertThat(product.getBranchId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenCreatingWithBlankName() {
        assertThatThrownBy(() -> Product.create("   ", 10, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((ProcessorException) e).getTechnicalMessage())
                .isEqualTo(TechnicalMessage.PRODUCT_NAME_REQUIRED);
    }

    @Test
    void shouldThrowWhenCreatingWithNegativeStock() {
        assertThatThrownBy(() -> Product.create("Espresso", -1, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((ProcessorException) e).getTechnicalMessage())
                .isEqualTo(TechnicalMessage.PRODUCT_STOCK_INVALID);
    }

    @Test
    void shouldThrowWhenCreatingWithNullStock() {
        assertThatThrownBy(() -> Product.create("Espresso", null, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((ProcessorException) e).getTechnicalMessage())
                .isEqualTo(TechnicalMessage.PRODUCT_STOCK_INVALID);
    }

    @Test
    void shouldReconstituteProductWithAllFields() {
        Product product = Product.reconstitute(1L, "Espresso", 10, 2L);

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Espresso");
        assertThat(product.getStock()).isEqualTo(10);
        assertThat(product.getBranchId()).isEqualTo(2L);
    }

    @Test
    void shouldChangeStockKeepingOtherFields() {
        Product product = Product.reconstitute(1L, "Espresso", 10, 2L);

        Product updated = product.changeStock(20);

        assertThat(updated.getStock()).isEqualTo(20);
        assertThat(updated.getId()).isEqualTo(1L);
        assertThat(updated.getName()).isEqualTo("Espresso");
    }

    @Test
    void shouldThrowWhenChangingToNegativeStock() {
        Product product = Product.reconstitute(1L, "Espresso", 10, 2L);

        assertThatThrownBy(() -> product.changeStock(-5))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((ProcessorException) e).getTechnicalMessage())
                .isEqualTo(TechnicalMessage.PRODUCT_STOCK_INVALID);
    }

    @Test
    void shouldChangeNameKeepingOtherFields() {
        Product product = Product.reconstitute(1L, "Espresso", 10, 2L);

        Product renamed = product.changeName("  Latte  ");

        assertThat(renamed.getName()).isEqualTo("Latte");
        assertThat(renamed.getStock()).isEqualTo(10);
    }

    @Test
    void shouldThrowWhenChangingToBlankName() {
        Product product = Product.reconstitute(1L, "Espresso", 10, 2L);

        assertThatThrownBy(() -> product.changeName(" "))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((ProcessorException) e).getTechnicalMessage())
                .isEqualTo(TechnicalMessage.PRODUCT_NAME_REQUIRED);
    }
}
