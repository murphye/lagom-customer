package lightbend.customer.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import lightbend.customer.api.CustomerService;

/**
 * Guice module to bind the service interface to its concrete implementation.
 */
public class CustomerModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindService(CustomerService.class, CustomerServiceImpl.class);
    }
}

