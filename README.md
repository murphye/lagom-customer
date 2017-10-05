# Introduction

This is a sample Lagom/Java application that has a Customer service that allows you, via REST, to:

1. Add a customer
2. Get a customer
3. List customers (via read-side view)
4. Disable a customer (acting as a soft delete)

This implementation uses Cassandra for both the write-side and read-side.

# Example curl commands

1. curl -H "Content-Type: application/json" -X POST -d '{"name": "Eric Murphy", "city": "San Francisco", "state": "CA", "zipCode": "94105"}' http://localhost:9000/customer
2. curl http://localhost:65199/customer/51c25a39-39b8-4937-b56b-5cca7f79acc1
3. curl http://localhost:65199/customer
4. curl -X POST http://localhost:65199/customer/disable/51c25a39-39b8-4937-b56b-5cca7f79acc1
5. curl http://localhost:65199/customer/51c25a39-39b8-4937-b56b-5cca7f79acc1 (run again to check disabled)

# Relevant Lagom Issues

1. Remove @Immutable in favor of Lombok
https://github.com/lagom/lagom/issues/592
2. Set cassandra-query-journal.eventual-consistency-delay to 0s
 https://github.com/lagom/lagom/issues/497
3. Delay as related to integration tests
https://github.com/lagom/lagom/issues/634
 
# Relevant Lombok Links

1. https://projectlombok.org/features/Value
2. https://projectlombok.org/features/NonNull
3. https://projectlombok.org/features/constructor
4. https://projectlombok.org/features/Builder
5. http://awhitford.github.io/lombok.maven/lombok-maven-plugin/faq.html#needed