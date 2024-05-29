# ADR 1: Postgres as Database 

Postgres is a robust and mature database system that is well-suited for building scalable and reliable applications. It is a great choice for the image processing application because it is open-source, has strong support for SQL and JSON data types, and has a rich ecosystem of extensions and tools.

## Decision 

Postgres will be used as the database for the image processing application.

## Rationale 

Modern, fast, easy to use and open-source database system that is well-suited for building scalable and reliable applications. It has strong support for SQL and JSON data types, and has a rich ecosystem of extensions and tools that will help us build a secure and scalable database for the image processing application.

## Status
Proposed

## Consequences

- All data will be stored in a Postgres database.
- All services must be able to connect to the Postgres database.
