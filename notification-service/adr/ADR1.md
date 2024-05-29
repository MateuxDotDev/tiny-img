# ADR 1: Socket.io as WebSocket Library 

Socket.io is performant, reliable, and has a rich ecosystem of libraries and tools that will help us build a secure and scalable WebSocket server.

## Decision 

Socket.io will be used as the WebSocket library for the image processing application.

## Rationale 

Socket.io became the industry standard for building real-time applications.

## Status
Proposed

## Consequences

- Clients must use Socket.io to connect to the WebSocket server.
- All WebSocket related tasks will be done using Socket.io.