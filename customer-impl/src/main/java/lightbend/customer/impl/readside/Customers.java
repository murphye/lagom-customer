package lightbend.customer.impl.readside;

import com.google.common.collect.ImmutableMap;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.jpa.JpaReadSide;
import com.lightbend.lagom.javadsl.persistence.jpa.JpaSession;
import lightbend.customer.api.Customer;
import lightbend.customer.impl.entity.CustomerEvent;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;
import java.util.concurrent.CompletionStage;

@Singleton
public class Customers {
    private static final String SELECT_ALL_QUERY =
            "SELECT NEW lightbend.customer.api.Customer(c.id, c.name, c.city, c.state, c.zipCode) FROM CustomerRecord c " +
                    "ORDER BY c.state, c.city, c.zipCode, c.name";

    private final JpaSession jpaSession;

    @Inject
    Customers(JpaSession jpaSession, ReadSide readSide) {
        this.jpaSession = jpaSession;
        readSide.register(CustomerRecordWriter.class);
    }

    public CompletionStage<PSequence<Customer>> all() {
        return jpaSession.withTransaction(this::selectAllCustomers).thenApply(TreePVector::from);
    }

    private List<Customer> selectAllCustomers(EntityManager entityManager) {
        return entityManager.createQuery(SELECT_ALL_QUERY, Customer.class).getResultList();
    }

    static class CustomerRecordWriter extends ReadSideProcessor<CustomerEvent> {
        private final JpaReadSide jpaReadSide;

        @Inject
        CustomerRecordWriter(JpaReadSide jpaReadSide) {
            this.jpaReadSide = jpaReadSide;
        }

        @Override
        public ReadSideHandler<CustomerEvent> buildHandler() {
            return jpaReadSide.<CustomerEvent>builder("CustomerRecordWriter")
                    .setGlobalPrepare(entityManager -> createSchema())
                    .setEventHandler(CustomerEvent.CustomerAdded.class, this::processCustomerAdded)
                    .setEventHandler(CustomerEvent.CustomerDisabled.class, this::processCustomerDisabled)
                    .build();
        }

        private void createSchema() {

            Persistence.generateSchema("default", // TODO: Change to "readside"
                    ImmutableMap.of("hibernate.hbm2ddl.auto", "update") // TODO: May not want to use for production software
            );                                                                  // (i.e. Use something like Flyway instead)
        }

        private void processCustomerAdded(EntityManager entityManager, CustomerEvent.CustomerAdded customerAddedEvent) {
            Customer customer = customerAddedEvent.getCustomer();
            CustomerRecord record = new CustomerRecord(
                    customer.getId(),
                    customer.getName(),
                    customer.getCity(),
                    customer.getState(),
                    customer.getZipCode());

            entityManager.persist(record);
        }

        private void processCustomerDisabled(EntityManager entityManager, CustomerEvent.CustomerDisabled customerDisabledEvent) {
            CustomerRecord record = entityManager.find(CustomerRecord.class, customerDisabledEvent.getCustomer().getId());
            if(record != null) {
                entityManager.remove(record);
                entityManager.flush();
            }
        }

        @Override
        public PSequence<AggregateEventTag<CustomerEvent>> aggregateTags() {
            return TreePVector.singleton(CustomerEvent.TAG);
        }
    }
}
