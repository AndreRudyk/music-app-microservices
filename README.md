# Music App

### Short description

This is a microservices-based application developed as a part of **Introduction to Microservices** course.

The Resource Service implements CRUD operations for processing MP3 files. When uploading an MP3 file, the service:

* Stores the MP3 file in the database
* Extracts the MP3 file tags (metadata) using Apache Tika library
* Invokes the Song Service to save the MP3 file tags (metadata)

The Song Service implements CRUD operations for managing song metadata records. The service uses the Resource ID to uniquely identify each metadata record, establishing a direct one-to-one relationship between resources and their metadata.

### Guides

Manually install the domain module before running the services: `mvn clean install -DskipTests` 

In order to launch the app locally, run `docker-compose up -d` command in the root folder.

For API testing, update the default base paths:
Change / in Postman to:

* /resource-service/api/v1

* /song-service/api/v1

## Testing Strategy

To ensure stability and comprehensive coverage for the Music App microservices, we employ a multi-layered testing approach:

### 1. Unit Tests
- **Purpose:** Validate individual classes/methods in isolation.
- **Tools:** JUnit, Mockito
- **Scope:** Service, utility, and validation logic.
- **Goal:** High coverage, fast feedback, catch regressions early.

### 2. Integration Tests
- **Purpose:** Test business scenarios at the API/component level.
- **Tools:** JUnit, Mockito, Test Containers
- **Scope:** Database, Feign clients, messaging integrations.
- **Goal:** Ensure modules work together as expected.

### 3. Component Tests
- **Purpose:** Verify interactions between components (e.g., service and repository).
- **Tools:** JUnit,Mockito
- **Scope:** Endpoints, business flows, edge cases.
- **Goal:** Validate business requirements in natural language.

### 4. Contract Tests
- **Purpose:** Ensure service contracts are honored between producers and consumers.
- **Tools:** Pact
- **Scope:** HTTP APIs, messaging (RabbitMQ)
- **Goal:** Prevent breaking changes, enable stub propagation for consumer-driven contracts.

### 5. End-to-End Tests
- **Purpose:** Validate complete user flows across services.
- **Tools:** Cucumber
- **Scope:** API layer, cross-service scenarios.
- **Goal:** Ensure system works as a whole from a user perspective.

### Approach Summary
- **Combination:** I use all five types, balancing speed and coverage. Unit and integration tests form the foundation, while component, contract, and end-to-end tests ensure business and communication integrity.
- **Benefits:** Early bug detection, stable releases, confidence in refactoring, and clear documentation of requirements.

