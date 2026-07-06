package co.com.gestorfranquicia.model.branch;

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
public class Branch {
    private Long id;
    private String name;
    private Long franchiseId;

    public static Branch create(String name, Long franchiseId) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(TechnicalMessage.BRANCH_NAME_REQUIRED);
        }
        return Branch.builder()
                .name(name.trim())
                .franchiseId(franchiseId)
                .build();
    }

    public static Branch reconstitute(Long id, String name, Long franchiseId) {
        return Branch.builder()
                .id(id)
                .name(name)
                .franchiseId(franchiseId)
                .build();
    }

    public Branch changeName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new BusinessException(TechnicalMessage.BRANCH_NAME_REQUIRED);
        }
        return this.toBuilder().name(newName.trim()).build();
    }
}
