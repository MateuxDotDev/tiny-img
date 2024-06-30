# ADR 1: Quarkus as Backend Framework

Quarkus was created to enable Java developers to create applications for a modern, cloud-native world. Quarkus is a Kubernetes-native Java framework tailored for GraalVM and HotSpot, crafted from best-of-breed Java libraries and standards. The goal is to make Java the leading platform in Kubernetes and serverless environments while offering developers a framework to address a wider range of distributed application architectures.

## Decision 

Use Quarkus as the backend framework for the auth service.

## Rationale 

Simple, fast, lightweight and able to generate native executables. Quarkus is a great choice for building the backend of the auth service because it is well-suited for building scalable and reliable applications.

## Status

Accepted

## Consequences

- Quarkus will be used for building the backend of the auth service.
- Extensions must be compatible with Quarkus.