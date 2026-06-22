# Emras — Architecture Guide

## Architecture Style: Modular Monolith (Facade Pattern)

Emras is built as a **Modular Monolith** — a single deployable Spring Boot application
where each business domain is internally isolated and communicates with other domains
only through defined **Facade interfaces**.

This gives us the simplicity of a monolith today with a clear, low-cost path to
microservices in the future.

---

## The Golden Rule

> **A domain's internal classes (Service, Repository, Entity) are private to that domain.
> Other domains may ONLY import from the `facade` and `dto` packages of another domain.**

### ✅ Allowed

```java
// CartService importing from the product domain — through the facade
import com.emras.product.facade.ProductFacade;
import com.emras.product.dto.response.ProductSummaryResponse;
```

### ❌ Forbidden

```java
// CartService reaching into product internals — NEVER allowed
import com.emras.product.service.ProductService;
import com.emras.product.repository.ProductRepository;
import com.emras.product.model.Product;
```

---

## Domain Structure

Every domain follows this identical structure:

```
com.emras.<domain>/
  ├── facade/         ← PUBLIC: interface that other domains import
  ├── dto/
  │   ├── request/    ← PUBLIC: input DTOs (controllers + facades)
  │   └── response/   ← PUBLIC: output DTOs (facades + controllers)
  ├── controller/     ← INTERNAL: HTTP layer, calls own service only
  ├── service/        ← INTERNAL: business logic, implements facade interface
  ├── repository/     ← INTERNAL: data access, never imported externally
  └── model/          ← INTERNAL: JPA entities, never passed across domains
```

---

## Cross-Domain Dependency Map

```
                    ┌─────────────┐
                    │  AI domain  │
                    └──────┬──────┘
                           │ uses facades of:
           ┌───────────────┼───────────────┐
           ▼               ▼               ▼
    ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
    │   Product   │ │    Cart     │ │    Order    │
    │   Facade    │ │   Facade    │ │   Facade    │
    └─────────────┘ └──────┬──────┘ └──────┬──────┘
                           │               │
                    uses   │        uses   │
              ProductFacade│   CartFacade  │
                           │   ProductFacade│
                           ▼               ▼
                    ┌─────────────┐ ┌─────────────┐
                    │    Cart     │ │    Order    │
                    │   Service   │ │   Service   │
                    └─────────────┘ └──────┬──────┘
                                          │ uses
                                   PaymentFacade
                                          ▼
                                   ┌─────────────┐
                                   │   Payment   │
                                   │   Service   │
                                   └─────────────┘
```

### Who calls whom (allowed cross-domain calls)

| Domain | May call facades of |
|--------|---------------------|
| `cart` | `ProductFacade` |
| `order` | `CartFacade`, `ProductFacade`, `PaymentFacade`, `UserFacade` |
| `payment` | `OrderFacade` |
| `ai` | `ProductFacade`, `CartFacade`, `OrderFacade`, `PaymentFacade` |
| `product` | `CategoryFacade` |
| `category` | _(none)_ |
| `user` | _(none)_ |

---

## How Facade Implementations Work

The facade is an interface. The service in the same domain implements it.
Spring injects the implementation wherever the facade interface is declared.

```java
// product/facade/ProductFacade.java  (interface — public)
public interface ProductFacade {
    boolean hasSufficientStock(Long variantId, int quantity);
}

// product/service/ProductServiceImpl.java  (implementation — internal)
@Service
public class ProductServiceImpl implements ProductService, ProductFacade {

    @Override
    public boolean hasSufficientStock(Long variantId, int quantity) {
        // real implementation
    }
}

// cart/service/CartServiceImpl.java  (consumer — only knows the interface)
@Service
public class CartServiceImpl implements CartService, CartFacade {

    private final ProductFacade productFacade;  // ✅ interface only

    public CartServiceImpl(ProductFacade productFacade) {
        this.productFacade = productFacade;
    }
}
```

---

## Future Microservices Migration Path

When a domain needs to be extracted into its own microservice:

1. Create a new Spring Boot project for that domain
2. Implement the facade interface as an HTTP/gRPC client in the monolith
3. Register the client as a `@Bean` in place of the local service implementation
4. All calling domains continue to use `ProductFacade` — **zero changes**

```java
// Today (monolith) — auto-wired by Spring from ProductServiceImpl
@Bean ProductFacade productFacade(ProductServiceImpl impl) { return impl; }

// Tomorrow (microservice) — just swap the bean
@Bean ProductFacade productFacade(ProductHttpClient client) { return client; }
```

---

## Shared Package Rules

The `shared` package is the only cross-cutting concern allowed:

| Class | Where it lives | Who may use it |
|-------|---------------|----------------|
| `ApiResponse<T>` | `shared.model` | All domains |
| `PagedResponse<T>` | `shared.model` | All domains |
| `BaseEntity` | `shared.model` | All domain models |
| `ErrorMessages` | `shared.constant` | All domains |
| `SuccessMessages` | `shared.constant` | All domains |
| `ApiConstants` | `shared.constant` | All domains |
| `EmrasException` | `shared.exception` | All domains |
| `GlobalExceptionHandler` | `shared.exception` | Framework only |
| `TraceUtil` | `shared.util` | All domains |

---

## Enforcing These Rules (Future)

Add **ArchUnit** to `pom.xml` (test scope) and write architecture tests that fail
the build if any domain imports another domain's internal classes:

```java
@ArchTest
static final ArchRule no_cross_domain_service_imports =
    noClasses()
        .that().resideInAPackage("com.emras.cart..")
        .should().dependOnClassesThat()
        .resideInAPackage("com.emras.product.service..");
```

This turns the golden rule into a compile-time safety net — planned for Phase 4.
