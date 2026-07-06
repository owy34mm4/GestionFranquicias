package co.com.gestorfranquicia.model.franchise;

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
public class Franchise {
    private Long id;
    private String name;

    public static Franchise create(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(TechnicalMessage.FRANCHISE_NAME_REQUIRED);
        }
        return Franchise.builder()
                .name(name.trim())
                .build();
    }

    public static Franchise reconstitute(Long id, String name) {
        return Franchise.builder()
                .id(id)
                .name(name)
                .build();
    }

    public Franchise changeName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new BusinessException(TechnicalMessage.FRANCHISE_NAME_REQUIRED);
        }
        return this.toBuilder().name(newName.trim()).build();
    }
}
