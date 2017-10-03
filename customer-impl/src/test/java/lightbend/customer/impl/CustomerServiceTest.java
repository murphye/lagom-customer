package lightbend.customer.impl;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import lightbend.customer.api.Customer;
import org.junit.Test;

import lightbend.customer.api.CustomerService;

import java.util.concurrent.CompletionStage;

public class CustomerServiceTest {

  /* TODO JDBC Integration Testing: https://github.com/lagom/lagom/issues/304
  @Test
  public void shouldStoreCustomer() throws Exception {
    withServer(defaultSetup().withCassandra(false), server -> {
      CustomerService service = server.client(CustomerService.class);

        Customer customer = new Customer("69c118f0-d5b1-4c4b-9e92-6cc1a0810a25", "Eric Murphy", "San Francisco", "CA", "94105");

        CompletionStage<Customer> msg1 = service.addCustomer().invoke(customer);
        Customer customerResponse = msg1.toCompletableFuture().get(5, SECONDS);

        assertEquals("Eric Murphy", customerResponse);

    });
  }
  */
}
