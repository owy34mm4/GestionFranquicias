package co.com.gestorfranquicia.model.franchise;

import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.BusinessException;
import co.com.gestorfranquicia.model.exception.ProcessorException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FranchiseTest {

    @Test
    void shouldCreateFranchiseWithTrimmedName() {
        Franchise franchise = Franchise.create("  Coffee Shop  ");

        assertThat(franchise.getId()).isNull();
        assertThat(franchise.getName()).isEqualTo("Coffee Shop");
    }

    @Test
    void shouldThrowWhenCreatingWithBlankName() {
        assertThatThrownBy(() -> Franchise.create("   "))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((ProcessorException) e).getTechnicalMessage())
                .isEqualTo(TechnicalMessage.FRANCHISE_NAME_REQUIRED);
    }

    @Test
    void shouldReconstituteFranchiseWithAllFields() {
        Franchise franchise = Franchise.reconstitute(1L, "Coffee Shop");

        assertThat(franchise.getId()).isEqualTo(1L);
        assertThat(franchise.getName()).isEqualTo("Coffee Shop");
    }

    @Test
    void shouldChangeNameKeepingSameId() {
        Franchise franchise = Franchise.reconstitute(1L, "Coffee Shop");

        Franchise renamed = franchise.changeName("  New Name  ");

        assertThat(renamed.getId()).isEqualTo(1L);
        assertThat(renamed.getName()).isEqualTo("New Name");
    }

    @Test
    void shouldThrowWhenChangingToBlankName() {
        Franchise franchise = Franchise.reconstitute(1L, "Coffee Shop");

        assertThatThrownBy(() -> franchise.changeName(""))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((ProcessorException) e).getTechnicalMessage())
                .isEqualTo(TechnicalMessage.FRANCHISE_NAME_REQUIRED);
    }
}
