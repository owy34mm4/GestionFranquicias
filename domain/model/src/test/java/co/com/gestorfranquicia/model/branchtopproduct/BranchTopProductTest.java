package co.com.gestorfranquicia.model.branchtopproduct;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BranchTopProductTest {

    @Test
    void shouldReconstituteBranchTopProductWithAllFields() {
        BranchTopProduct branchTopProduct = BranchTopProduct.reconstitute(1L, "Downtown", 2L, "Espresso", 10);

        assertThat(branchTopProduct.getBranchId()).isEqualTo(1L);
        assertThat(branchTopProduct.getBranchName()).isEqualTo("Downtown");
        assertThat(branchTopProduct.getProductId()).isEqualTo(2L);
        assertThat(branchTopProduct.getProductName()).isEqualTo("Espresso");
        assertThat(branchTopProduct.getStock()).isEqualTo(10);
    }
}
