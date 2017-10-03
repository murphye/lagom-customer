package lightbend.customer.impl.entity;

import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import lightbend.customer.api.Customer;
import lombok.Data;
import lombok.NonNull;
import com.lightbend.lagom.serialization.Jsonable;

public interface CustomerEvent extends Jsonable, AggregateEvent<CustomerEvent> {

    /*
     * AggregateEventTag is used by Akka Cluster Sharding to setup read-side processing of persistent events
     */
    AggregateEventTag<CustomerEvent> TAG = AggregateEventTag.of(CustomerEvent.class);

    @Override
    default AggregateEventTagger<CustomerEvent> aggregateTag() {
        return AggregateEventTag.sharded(CustomerEvent.class,
                4); // numShards should not change in the future; see Akka Cluster docs for guidance
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
