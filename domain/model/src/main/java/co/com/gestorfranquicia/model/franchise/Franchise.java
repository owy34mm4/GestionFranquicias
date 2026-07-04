package co.com.gestorfranquicia.model.franchise;

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
        return Franchise.builder()
                .name(name)
                .build();
    }

    public static Franchise reconstitute(Long id, String name) {
        return Franchise.builder()
                .id(id)
                .name(name)
                .build();
    }
}
