# ADR 2: Swagger as API documentation tool

Swagger/OpenAPI is a powerful tool for documenting APIs. It provides a standard way to describe RESTful APIs and generate interactive documentation. It is a great choice for the image processing application because it is well-suited for building microservices and has a strong ecosystem of libraries and tools.

## Decision 

Swagger will be used as the API documentation tool for the image processing application.

## Rationale 

NestJS already comes with a built-in Swagger module that makes it easy to generate API documentation. Swagger provides a standard way to describe RESTful APIs and generate interactive documentation. It is well-suited for building microservices and has a strong ecosystem of libraries and tools that will help us build a secure and scalable image processing application.

## Status
Proposed

## Consequences

- All APIs will be documented using Swagger/OpenAPI.
- Interactive documentation will be generated automatically.
- Developers will have an easy way to explore and test the APIs.