package lightbend.customer.impl.entity;

import lightbend.customer.api.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Value;

/**
 * Customer state object used for event sourcing. Stored as binary data in the journal because of CompressedJsonable.
 */
@Value
@AllArgsConstructor
public final class CustomerState implements CompressedJsonable {

    public static final CustomerState EMPTY = new CustomerState();

    /**
     * Reference to the customer object.
     */
    private Customer customer;

    /**
     * Whether the customer is enabled or not; acts like a soft delete.
     */
    private boolean enabled;

    /**
     * Used for initial state.
     */
    private CustomerState() {
        this.customer = null;
        this.enabled = false;
    }
}
