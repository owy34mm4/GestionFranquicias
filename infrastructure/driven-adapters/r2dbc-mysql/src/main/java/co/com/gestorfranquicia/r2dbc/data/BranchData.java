package co.com.gestorfranquicia.r2dbc.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("branch")
public class BranchData {
    @Id
    private Long id;
    @Column("name")
    private String name;
    @Column("franchise_id")
    private Long franchiseId;
}
