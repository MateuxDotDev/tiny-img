# ADR 1: Golang as Language 

Golang is a robust and mature programming language that is well-suited for building scalable and reliable applications. It is a great choice for the image processing application because it is fast, efficient, and has a rich ecosystem of libraries and tools.

## Decision 

Go will be used as the programming language for the image processing application.

## Rationale 

Fast, efficient, out-of-the-box support for concurrency, and a rich ecosystem of libraries and tools that will help us build a secure and scalable image processing application.

## Status
Proposed

## Consequences

- Image processing will be done using Go.
- FFmpeg will be used for image processing using `Exec` command.