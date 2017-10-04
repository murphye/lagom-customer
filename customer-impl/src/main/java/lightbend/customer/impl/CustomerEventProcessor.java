package lightbend.customer.impl;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import lightbend.customer.api.Customer;
import lightbend.customer.impl.entity.CustomerEvent;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Implement the CassandraReadSide processor to capture the CustomerEvents and either
 * add or remove customers from the read-side view.
 */
public class CustomerEventProcessor extends ReadSideProcessor<CustomerEvent> {

    private final CassandraSession session;
    private final CassandraReadSide readSide;

    private PreparedStatement writeCustomer;
    private PreparedStatement deleteCustomer;

    @Inject
    public CustomerEventProcessor(final CassandraSession session, final CassandraReadSide readSide) {
        this.session = session;
        this.readSide = readSide;
    }

    @Override
    public PSequence<AggregateEventTag<CustomerEvent>> aggregateTags() {
        return TreePVector.singleton(CustomerEvent.TAG);
    }

    @Override
    public ReadSideHandler<CustomerEvent> buildHandler() {
        return readSide.<CustomerEvent>builder("customer_offset")
                .setGlobalPrepare(this::createTable)
                .setPrepare(evtTag -> prepareWriteCustomer()
                        .thenCombine(prepareDeleteCustomer(), (writeDone, deleteDone) -> Done.getInstance())
                )
                .setEventHandler(CustomerEvent.CustomerAdded.class, this::processPostAdded)
                .setEventHandler(CustomerEvent.CustomerDisabled.class, this::processPostDeleted)
                .build();
    }

    private CompletionStage<Done> createTable() {
        return session.executeCreateTable("CREATE TABLE IF NOT EXISTS customer ( " +
                        "id TEXT, name TEXT, city TEXT, state TEXT, zipcode TEXT, PRIMARY KEY (id))"
        );
    }

    private CompletionStage<Done> prepareWriteCustomer() {
        return session.prepare("INSERT INTO customer (id, name, city, state, zipcode) VALUES (?, ?, ?, ?, ?)").thenApply(ps -> {
            setWriteCustomer(ps);
            return Done.getInstance();
        });
    }

    private void setWriteCustomer(PreparedStatement statement) {
        this.writeCustomer = statement;
    }

    private CompletionStage<List<BoundStatement>> processPostAdded(CustomerEvent.CustomerAdded customerAddedEvent) {
        Customer customer = customerAddedEvent.getCustomer();

        BoundStatement bindWriteCustomer = writeCustomer.bind(
                customer.getId(),
                customer.getName(),
                customer.getCity(),
                customer.getState(),
                customer.getZipCode());

        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteCustomer));
    }

    private CompletionStage<Done> prepareDeleteCustomer() {
        return session.prepare("DELETE FROM customer WHERE id=?").thenApply(ps -> {
            setDeleteCustomer(ps);
            return Done.getInstance();
        });
    }

    private void setDeleteCustomer(PreparedStatement deleteCustomer) {
        this.deleteCustomer = deleteCustomer;
    }

    private CompletionStage<List<BoundStatement>> processPostDeleted(CustomerEvent.CustomerDisabled customerDisabledEvent) {
        BoundStatement bindWriteCustomer = deleteCustomer.bind();
        bindWriteCustomer.setString("id", customerDisabledEvent.getCustomer().getId());
        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteCustomer));
    }
}
