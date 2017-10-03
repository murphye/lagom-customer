package lightbend.customer.api;

import com.lightbend.lagom.serialization.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import javax.annotation.concurrent.Immutable;

@Data
@AllArgsConstructor
@Immutable
public final class Customer implements Jsonable {
    private String id; // Nullable for the POST method
    @NonNull private String name;
    @NonNull private String city;
    @NonNull private String state;
    @NonNull private String zipCode;
}
