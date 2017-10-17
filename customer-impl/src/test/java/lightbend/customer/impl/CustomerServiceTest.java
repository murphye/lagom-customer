package lightbend.customer.impl;

import akka.Done;
import com.google.common.collect.ImmutableList;
import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import lightbend.customer.api.Customer;
import lightbend.customer.api.CustomerService;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.startServer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.JVM)
public class CustomerServiceTest {

    private static ServiceTest.TestServer server;

    private static CustomerService service;

    private static String uuid;

    @BeforeClass
    public static void setUp() {
        server = startServer(defaultSetup().withCassandra(true));
        service = server.client(CustomerService.class);
        uuid = UUID.randomUUID().toString();
    }

    @AfterClass
    public static void tearDown() {
        server.stop();
        server = null;
    }

    @Test
    public void integrationTest() throws Exception {
        Customer customer = new Customer(uuid, "Eric Murphy", "San Francisco", "CA", "94105");

        CompletionStage<Done> msg1 = service.addCustomer().invoke(customer);
        Done addCustomerResponse = msg1.toCompletableFuture().get(5, SECONDS);

        CompletionStage<Customer> msg2 = service.getCustomer(uuid).invoke();
        Customer customerResponse = msg2.toCompletableFuture().get(5, SECONDS);

        assertThat("Eric Murphy").isEqualTo(customerResponse.getName());

        readSideShouldUpdate(); // Separating into distinct @Test introduces unknown instability
    }

    public void readSideShouldUpdate() throws Exception {
        Thread.sleep(5000); // Wait for read-side to update
        CompletionStage<ImmutableList<Customer>> msg3 = service.getCustomers().invoke();
        ImmutableList<Customer> customersResponse = msg3.toCompletableFuture().get(5, SECONDS);
        // Added record is now visible on read-side
        assertThat(customersResponse.size()).isEqualTo(1);

        customerShouldBeDisabled();
    }

    public void customerShouldBeDisabled() throws Exception {
        CompletionStage<Done> msg4 = service.disableCustomer(uuid).invoke();
        Done disableCustomerResponse = msg4.toCompletableFuture().get(5, SECONDS);

        try {
            CompletionStage<Customer> msg5 = service.getCustomer(uuid).invoke();
            Customer customerResponse = msg5.toCompletableFuture().get(5, SECONDS);
            Assert.fail("Customer should be disabled, and exception thrown");
        } catch (Exception notFound) {
            assertThat(notFound.getMessage()).contains("Customer is disabled");
        }
    }
}
