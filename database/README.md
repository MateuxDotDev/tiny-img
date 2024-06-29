# Database

![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)

This project is responsible for storing and managing data for TinyImg users.

## How to run

First, populate a `.env` file with the keys that can be found on [`.env.example`](./.env.example).

Then, run the following command:

```bash
docker compose up
```

And you're good to go!

## Entity Relationship Diagram (ERD)

```mermaid
erDiagram
    user {
        serial id
        varchar(255) username
        varchar(255) email
        text password
        text salt
        timestamp created_at
        timestamp updated_at
    }
    image {
        serial id
        uuid image_id
        varchar(255) path
        int user_id
        parent_id int
        timestamp created_at
        timestamp updated_at
    }
    image ||--o{ image: "has"
    user ||--o{ image : "has"
    share_link {
        serial id
        varchar(255) link
        int image_id
        int usage_count
        int max_usage_count
        timestamp created_at
        timestamp updated_at
    }
    image ||--o| share_link : "has"
```

## ADRs

- [Postgres as Database](./adr/ADR1.md)
- [Entity Relationship Diagram (ERD) for the Image Processing Application](./adr/ADR2.md)