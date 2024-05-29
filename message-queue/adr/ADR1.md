# ADR 1: RabbitMQ as Message Queue 

RabbitMQ is a robust and mature message queue system that is well-suited for building scalable and reliable applications. It is a great choice for the image processing application because it is open-source, has strong support for message queuing and routing, and has a rich ecosystem of extensions and tools.

## Decision 

RabbitMQ will be used as the message queue for the image processing application.

## Rationale 

Simple to use, with a friendly API and lots of libraries and tools to help you get started. It is a great choice for the image processing application because it is open-source, has strong support for message queuing and routing, and has a rich ecosystem of extensions and tools that will help us build a secure and scalable message queue for the image processing application.

## Status
Proposed

## Consequences

- All message queue related traffic will be handled by RabbitMQ.
- All services must be able to connect to the RabbitMQ message queue.
