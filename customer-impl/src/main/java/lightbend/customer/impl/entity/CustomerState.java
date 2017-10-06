package lightbend.customer.impl.entity;

import lightbend.customer.api.Customer;

import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Value;

import java.util.Optional;

/**
 * Customer state object used for event sourcing. Stored as binary data in the journal because of CompressedJsonable.
 */
@Value
public class CustomerState implements CompressedJsonable {

    private final Optional<Customer> customer;
    private final CustomerStatus status;

    public static CustomerState newCustomer() {
        return new CustomerState(Optional.empty(), CustomerStatus.NEW);
    }

    public static CustomerState addedCustomer(Customer customer) {
        return new CustomerState(Optional.of(customer), CustomerStatus.ADDED);
    }

    public static CustomerState disabledCustomer(Customer customer) {
        return new CustomerState(Optional.of(customer), CustomerStatus.DISABLED);
    }
}
