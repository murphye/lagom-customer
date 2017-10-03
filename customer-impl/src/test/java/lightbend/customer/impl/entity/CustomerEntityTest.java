package lightbend.customer.impl.entity;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver.Outcome;
import lightbend.customer.api.Customer;
import org.junit.*;
import org.junit.rules.TestName;
import java.util.Collections;
import static org.assertj.core.api.Assertions.assertThat;


public class CustomerEntityTest {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("CustomerEntityTest");
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Rule
    public TestName testName = new TestName();

    private PersistentEntityTestDriver<CustomerCommand, CustomerEvent, CustomerState> driver;

    @Before
    public void setUp() throws Exception {
        // given a default BlogEntity
        driver = new PersistentEntityTestDriver<>(
                system, new CustomerEntity(), testName.getMethodName());
    }

    @Test
    public void initialStateShouldBeEmpty() throws Exception {
        // when we send a GetPost command
        final Outcome<CustomerEvent, CustomerState> getPostOutcome = driver.run(CustomerCommand.GetCustomer.INSTANCE);

        // then no events should have been created
        assertThat(getPostOutcome.events()).isEmpty();

        // and the state should still be empty
        assertThat(getPostOutcome.state().getCustomer()).isNull();
    }


    @Test
    public void testCustomers() {

        Customer customer = new Customer("69c118f0-d5b1-4c4b-9e92-6cc1a0810a25", "Eric Murphy", "San Francisco", "CA", "94105");

        // Add the customer
        PersistentEntityTestDriver<CustomerCommand, CustomerEvent, CustomerState> driver =
                new PersistentEntityTestDriver<>(system, new CustomerEntity(), customer.getId());
        Outcome<CustomerEvent, CustomerState> outcome1 = driver.run(new CustomerCommand.AddCustomer(customer));

        // Check that customer was added
        assertThat(Collections.emptyList()).isEqualTo(outcome1.issues());
        assertThat("Eric Murphy").isEqualTo(outcome1.state().getCustomer().getName());

        // Disable the customer
        Outcome<CustomerEvent, CustomerState> outcome2 = driver.run(CustomerCommand.DisableCustomer.INSTANCE);
        assertThat(Collections.emptyList()).isEqualTo(outcome2.issues());

        // Validate customer was disabled
        Outcome<CustomerEvent, CustomerState> outcome3 = driver.run(CustomerCommand.GetCustomer.INSTANCE);
        assertThat(Collections.emptyList()).isEqualTo(outcome3.issues());
        assertThat(false).isEqualTo(outcome3.state().isEnabled());

    }
}
