package lightbend.customer.api;

import com.lightbend.lagom.serialization.Jsonable;
import lombok.*;

import java.util.UUID;

@Value // Makes object immutable
@Builder
public final class Customer implements Jsonable {
    @NonNull private String id;
    @NonNull private String name;
    @NonNull private String city;
    @NonNull private String state;
    @NonNull private String zipCode;

    @java.beans.ConstructorProperties({"id", "name", "city", "state", "zipCode"})
    public Customer(String id, String name, String city, String state, String zipCode) {
        this.id = ((id == null) ? UUID.randomUUID().toString() : id);
        this.name = name;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }
}
