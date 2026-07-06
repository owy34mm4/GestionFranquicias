package co.com.gestorfranquicia.model.branch;

import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.BusinessException;
import co.com.gestorfranquicia.model.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BranchTest {

    @Test
    void shouldCreateBranchWithTrimmedName() {
        Branch branch = Branch.create("  Downtown  ", 1L);

        assertThat(branch.getId()).isNull();
        assertThat(branch.getName()).isEqualTo("Downtown");
        assertThat(branch.getFranchiseId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenCreatingWithBlankName() {
        assertThatThrownBy(() -> Branch.create("   ", 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((DomainException) e).getTechnicalMessage())
                .isEqualTo(TechnicalMessage.BRANCH_NAME_REQUIRED);
    }

    @Test
    void shouldReconstituteBranchWithAllFields() {
        Branch branch = Branch.reconstitute(1L, "Downtown", 2L);

        assertThat(branch.getId()).isEqualTo(1L);
        assertThat(branch.getName()).isEqualTo("Downtown");
        assertThat(branch.getFranchiseId()).isEqualTo(2L);
    }

    @Test
    void shouldChangeNameKeepingSameIdAndFranchiseId() {
        Branch branch = Branch.reconstitute(1L, "Downtown", 2L);

        Branch renamed = branch.changeName("  New Branch  ");

        assertThat(renamed.getId()).isEqualTo(1L);
        assertThat(renamed.getName()).isEqualTo("New Branch");
        assertThat(renamed.getFranchiseId()).isEqualTo(2L);
    }

    @Test
    void shouldThrowWhenChangingToBlankName() {
        Branch branch = Branch.reconstitute(1L, "Downtown", 2L);

        assertThatThrownBy(() -> branch.changeName(null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((DomainException) e).getTechnicalMessage())
                .isEqualTo(TechnicalMessage.BRANCH_NAME_REQUIRED);
    }
}
