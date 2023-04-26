# Architecture

## Requirements

Current requirements:
1. Build an API for querying data as workload in a Kubernetes cluster; data can be queried by year, movie name, cast member or genre
1. API must be available 24/7 with zero downtime
1. API must return response time within 5ms
1. Choose a data storage solution that is fit for purpose
1. Prepare a diagram describing the chosen architecture
1. Implementation can be in any programming language
1. Assume data format is that of the provided data set; see resources below.

The following has to take into consideration while design:
1. Implementing API in multiple regions
1. Identifying the source of incoming API requests
1. Audit changes to data
1. Different providers accessing the S3 bucket to write data updates
1. CICD of the full stack: infrastructure resources and application code
1. Choice of data storage layer and factors that went into the decision
1. Encrypting data at rest and in transit

## AWS considerations

### Buckets

An AWS S3 Bucket can be:
- polled, or
- it can send notifications about its own [changes](https://docs.aws.amazon.com/AmazonS3/latest/userguide/NotificationHowTo.html).

Buckets can send notifications through the following medium:
- Amazon Simple Notification Service (Amazon SNS) topics
- Amazon Simple Queue Service (Amazon SQS) queues
- AWS Lambda function

In order to a) minimize the development cost, b) rely on the AWS environment c) ensure no data loss d) ensure later scalability Amazon SQS will be used.

This solution is acceptable, since there's no time limit about the data population to the service, but to the response time (queries).
This design decision can be considered as eventual consistency introduced to the system (the Data Provider think the data should be appear after a successful bucket update), but this solution embrace eventual consistency.
Furthermore strict consistency and 24/7 uptime are contradictory requirements would be.

### Database

According to the requirement there's a fairly complex way to query the DB, however, no complex schema is needed to serve the requests.
This opens the possibilities, since it is possible to choose a Key-Value store, Columnar or a "standard" SQL based service.
In case of a Key-Value store, based on the current requirements, and the potential number of records, even an in-memory key store could be considered with a batch value store.

Furthermore, from DB PoV the following most important questions has to be considered:
- SQL: uptime, fault tolerance,
- NoSql: query complexity, consistency vs uptime.

For the PoC SQL was chosen, however, based on the interfaces, which are implemented, application is open for changing the DB.

## General Design

The application must be up 24/7 and produce a 5ms response time.
Following the KISS principle and considering the time it has to be delivered:
- the service is stateless,
- using an SQL server in order to make queries simple (considering the size of the DB),
- using standard tool to persistent layer connection (JDBC) to prepare the change to more reliable DB in the future,
- using messaging in order to avoid message loss.

### Architecture

#### API definition / HTTP

The service has only one API, that is defined by the delivery team: the query API.

The query is an HTTP API, open to changed to gRPC easily.
The query can be send through the `/movies` endpoint, whit query parameters.
There are three different ways to send a query to a service through HTTP request: Path, Body, Query.
Path has been chosen, because of simplicity, however, the solution is open for requests in the Body.

4 query parameter can be added, in arbitrary order:
- years: int values between 1880 and now; separated by `,` character.
- titles: string values separated by `,` character.
- cast: string values separated by `,` character.
- genres: provided genres as strings, separated by `,` character.

The response can be either a Success or a Failure, that is described in the body and the response code.

#### Solution Architecture

The main components are:
- AWS S3 Bucket: an "interface" between the service and the Data Providers. Changes in the Bucket are published to a messaging service (below).
- Amazon SQS: stores the changes in the Bucket. This layer provides a reliable storage for the messages, if the service would be down.
- Movie Svc: the service itself,
- PostgreSQL: SQL DB for PoC,
- Kubernetes: orchestration service. 

The Movie Service expects the SQS queue is created (that has to be part of the configuration / set up of the system).

#### Application Architecture

Application fundamentally separates the read and write side of the application (in CQRS style).

Interfaces: 
- read: HTTP
- write: Amazon SQS

To be open for further changes, and take leverage of the IDL, the service's messages are described in proto.

In tune with the interfaces, the application has two major parts:
- QueryService - that serves the queries, and
- DataProviderService - that ensure reading data from the Amazon SQS and write it into the DB in a reliable fashion.
Considering the currently used (SQL) server's ACID properties, reading from the SQS writing in the DB and ACK the message happens in one transaction.
If during this time the service would go down, message would not be lost, therefore the operation would be repeated.
Considering that this updates a Movie data in the DB, this would not cause any issue (time invariant / idempotent operation).

The DB is a shared resources between the two sides, but this could not cause issues, since only one is writing into the DB.

#### Consistency considerations

The write side write data in ACID manner to the DB.
The read side query the DB for every request - caching happens on the DB level.
As such, though there's a delay between update the DB and having the data in the S3 Bucket, but the read operation is all the time consistent.

#### Reliability considerations

Up-time in the application's case can be defined as:
- uptime of the write interfaces (S3),
- uptime of the query interfaces (Movie Service).

The uptime of the write side is equivalent with the Amazon's S3 uptime - that is part of the specification.

The uptime of the query interface - therefore the Movie Service - can be as good as the DB's uptime.
There are a few parameters, that should be measure over time, but as a first estimation (H0) it is the DB's uptime.

Further parameters, MTBF (mean time between failures) can be measured in production.

# Known issues / Tasks

- Application shutdown: currently there's no mechanism to stop the Http thread.
- RESTful API: 
  - the API right now only the query API, and therefore `If-Match` `If-None-Match` and `ETag` is not handled.
  - response codes does not necessary reflects the errors.
  - no popular content type supporting (like XML)
  - no API versioning.
  - no Authorization.
  - HTTP spec version 6[RFC3513] and later allows '\[' and '\]' characters in the URL, that is not used.
  - parameters arguably can be changed to payload (body) - system is prepared, but not implemented.
- Healthcheck: the healthcheck does not really reflects the readiness of the application.
- Thread Pooling: separated context for the IO operations (especially for the different type of the IO operations) would be necessary for production.
- Service(s) start-up is not fault tolerant
- Set up logging
- Configure Kubernetes
- Implement missing test cases with fixtures
- Add Jooq support
- Configure AWS environment S3 and SQS