package lightbend.customer.impl.entity;

import java.util.Optional;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import akka.Done;
import lightbend.customer.api.Customer;

/**
 * The CustomerEntity handles commands and events while maintaining state through event sourcing.
 *
 * For commands, we want to be able to handle adding, disabling, and getting customers. Disabling effectively means
 * soft deleting where an enabled flag is stored in the state.
 */
public class CustomerEntity extends PersistentEntity<CustomerCommand, CustomerEvent, CustomerState> {

    @Override
    public Behavior initialBehavior(Optional<CustomerState> snapshotState) {

        BehaviorBuilder b = newBehaviorBuilder(snapshotState.orElse(CustomerState.EMPTY));

        b.setCommandHandler(CustomerCommand.AddCustomer.class, (cmd, ctx) ->
                ctx.thenPersist(new CustomerEvent.CustomerAdded(cmd.getCustomer()), evt ->
                        ctx.reply(Done.getInstance())));

        b.setReadOnlyCommandHandler(CustomerCommand.GetCustomer.class, (cmd, ctx) ->
                ctx.reply(state()));

        b.setCommandHandler(CustomerCommand.DisableCustomer.class, (cmd, ctx) ->
                ctx.thenPersist(new CustomerEvent.CustomerDisabled(state().getCustomer()), evt ->
                        ctx.reply(Done.getInstance()))
        );

        b.setEventHandler(CustomerEvent.CustomerAdded.class, evt -> {
            Customer newCustomer = evt.getCustomer();
            return new CustomerState(newCustomer, true);
        });

        b.setEventHandler(CustomerEvent.CustomerDisabled.class, evt ->
                new CustomerState(evt.getCustomer(), false)
        );

        return b.build();
    }
}
