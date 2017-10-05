package lightbend.customer.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import java.util.List;

/**
 * Service interface to add, disable, get, and return all customers.
 */
public interface CustomerService extends Service {

    ServiceCall<Customer, Done> addCustomer();

    ServiceCall<NotUsed, Customer> getCustomer(String id);

    ServiceCall<NotUsed, Done> disableCustomer(String id);

    ServiceCall<NotUsed, List<Customer>> getCustomers();

    /**
     * Service descriptor to setup the routes to the service endpoints.
     */
    default Descriptor descriptor() {
        return named("customer").withCalls(
                restCall(Method.POST,   "/customer", this::addCustomer),
                restCall(Method.GET,    "/customer/:customerId", this::getCustomer),
                restCall(Method.POST, "/customer/disable/:customerId", this::disableCustomer),
                restCall(Method.GET,    "/customer", this::getCustomers)
        ).withAutoAcl(true); // Used to setup ACLs for the API Gateway
    }
}
