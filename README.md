# ShortenUrl Project

This project implements a URL shortening service.

## Structure & Architecture

The project follows hexagonal clean architecture, separating concerns into distinct modules:

*   **`domain`**: Contains the core business logic, entities, and value objects, independent of any infrastructure concerns.
*   **`application`**: Houses application services, use cases, and orchestrates interactions between the domain and infrastructure layers.
*   **`infrastructure`**: Provides implementations for external concerns such as persistence, caching, and external APIs.

## Advanced Patterns & Concepts

*   **Clean Architecture**: Enforces separation of concerns, making the codebase more maintainable, testable, and scalable.
*   **Dependency Injection**: Managed by Spring, facilitating loose coupling between components.
*   **Caching**: Implemented `TwoTierCacheAdapter` in the `infrastructure` layer.
