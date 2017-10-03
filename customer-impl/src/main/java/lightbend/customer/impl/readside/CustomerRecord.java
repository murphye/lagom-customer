package lightbend.customer.impl.readside;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer")
public final class CustomerRecord {
    @Id
    @NonNull private String id;
    @NonNull private String name;
    @NonNull private String city;
    @NonNull private String state;
    @NonNull private String zipCode;
}
