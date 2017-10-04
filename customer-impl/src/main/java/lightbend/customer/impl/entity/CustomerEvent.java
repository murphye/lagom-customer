package lightbend.customer.impl.entity;

import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import lightbend.customer.api.Customer;
import lombok.Data;
import lombok.NonNull;
import com.lightbend.lagom.serialization.Jsonable;

public interface CustomerEvent extends Jsonable, AggregateEvent<CustomerEvent> {

    AggregateEventTag<CustomerEvent> TAG = AggregateEventTag.of(CustomerEvent.class);

    @Override
    default AggregateEventTagger<CustomerEvent> aggregateTag() {
        return TAG;
    }

    @Data
    public final class CustomerAdded implements CustomerEvent {
        @NonNull final Customer customer;
    }

    @Data
    public final class CustomerDisabled implements CustomerEvent {
        @NonNull final Customer customer;
    }
}
