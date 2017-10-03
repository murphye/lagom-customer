# Introduction

This is a sample Lagom/Java application that has a Customer service that allows you, via REST, to:

1. Add a customer
2. Get a customer
3. List customers (via read-side view)
4. Disable a customer (acting as a soft delete)

This implementation uses JDBC and PostgreSQL for both the write-side and read-side.
It uses JPA and Hibernate for the read-side, per Lagom documentation.

Ideally, write-side and read-side would go to different databases, but there is a defect in Lagom (see below).
I have stubbed in configuration that should make this functional after the defect is fixed.

# Lagom Documentation References

1. https://www.lagomframework.com/documentation/1.3.x/java/ServiceDescriptors.html
2. https://www.lagomframework.com/documentation/1.3.x/java/PersistentEntityRDBMS.html
3. https://www.lagomframework.com/documentation/1.3.x/java/ReadSideRDBMS.html
4. https://www.lagomframework.com/documentation/1.3.x/java/ReadSideJPA.html

# Setup

1. Install PostgreSQL > 9.4. On Mac, https://postgresapp.com/ is a good choice.
2. Create database *customer*
3. Do *mvn lagom:runAll* which will connect to your local PostgreSQL instance

# Example curl commands

1. curl -H "Content-Type: application/json" -X POST -d '{"name": "Eric Murphy", "city": "San Francisco", "state": "CA", "zipCode": "94105"}' http://localhost:9000/customer
2. curl http://localhost:65199/customer/51c25a39-39b8-4937-b56b-5cca7f79acc1
3. curl http://localhost:65199/customer
4. curl -X DELETE http://localhost:65199/customer/51c25a39-39b8-4937-b56b-5cca7f79acc1

# Known Lagom JDBC issues

1. Cannot separate JDBC write-side from read-side. Even if you use JDBC for both, you need to store them in same database or you will get errors. 
This even prevents you from using Cassandra on the write-side and JDBC on the read-side.
https://github.com/lagom/lagom/issues/720
2. Cannot run integration tests with JDBC
https://github.com/lagom/lagom/issues/304