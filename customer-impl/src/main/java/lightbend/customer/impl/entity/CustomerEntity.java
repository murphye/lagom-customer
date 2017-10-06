package lightbend.customer.impl.entity;

import java.util.Optional;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import akka.Done;

/**
 * The CustomerEntity handles commands and events while maintaining state through event sourcing.
 *
 * For commands, we want to be able to handle adding, disabling, and getting customers. Disabling effectively means
 * no behaviors will affect the state.
 */
public class CustomerEntity extends PersistentEntity<CustomerCommand, CustomerEvent, CustomerState> {

    @Override
    public Behavior initialBehavior(Optional<CustomerState> snapshotState) {

        BehaviorBuilder b = newBehaviorBuilder(snapshotState.orElse(CustomerState.newCustomer()));

        b.setCommandHandler(CustomerCommand.AddCustomer.class, (cmd, ctx) ->
                ctx.thenPersist(new CustomerEvent.CustomerAdded(cmd.getCustomer()), evt ->
                        ctx.reply(Done.getInstance())));

        b.setEventHandlerChangingBehavior(CustomerEvent.CustomerAdded.class, evt ->
                becomeCustomerAdded(CustomerState.addedCustomer(evt.getCustomer()))
        );

        return b.build();
    }

    private Behavior becomeCustomerAdded(CustomerState customerState) {
        BehaviorBuilder b = newBehaviorBuilder(customerState);

        b.setReadOnlyCommandHandler(CustomerCommand.GetCustomer.class, (cmd, ctx) ->
                ctx.reply(state()));

        b.setCommandHandler(CustomerCommand.DisableCustomer.class, (cmd, ctx) ->
                ctx.thenPersist(new CustomerEvent.CustomerDisabled(customerState.getCustomer().get()), evt ->
                        ctx.reply(Done.getInstance()))
        );

        b.setEventHandlerChangingBehavior(CustomerEvent.CustomerDisabled.class, evt ->
                becomeCustomerDisabled(CustomerState.disabledCustomer(evt.getCustomer()))
        );

        return b.build();
    }

    private Behavior becomeCustomerDisabled(CustomerState customerState) {
        BehaviorBuilder b = newBehaviorBuilder(customerState);
        // No current behavior for disabled customers
        return b.build();
    }
}
