package lightbend.customer.impl.entity;

import lightbend.customer.api.Customer;
import lombok.Value;
import lombok.NonNull;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;

import akka.Done;

public interface CustomerCommand extends Jsonable {

    @Value
    final class AddCustomer implements CustomerCommand, PersistentEntity.ReplyType<Done> {
        @NonNull private final Customer customer;
    }

    enum GetCustomer implements CustomerCommand, PersistentEntity.ReplyType<CustomerState> {
        INSTANCE
    }

    enum DisableCustomer implements CustomerCommand, PersistentEntity.ReplyType<Done> {
        INSTANCE
    }
}
