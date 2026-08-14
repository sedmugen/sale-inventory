# Developer & Architecture Contribution Guide

This guide details code structure, design patterns, testing strategies, and guidelines for extending the **Sale & Inventory Management System**.

---

## 1. Project Organization

```text
sale-inventory/
├── .github/                  # GitHub Actions CI, Dependabot, and Issue/PR templates
│   ├── workflows/ci.yml      # CI workflow for backend & frontend test matrix
│   ├── dependabot.yml        # Weekly dependency update schedule
│   └── ISSUE_TEMPLATE/       # Structured bug & feature request forms
├── assets/                   # Visual demonstration assets & recordings
│   ├── images/               # UI screenshots and architecture graphics
│   ├── gifs/                 # Workflow animations
│   └── videos/               # End-to-end video demonstrations
├── docs/                     # Technical documentation suite
│   ├── README.md             # Documentation hub & navigation
│   ├── architecture.md       # C4 diagrams, ER schema, transaction flows
│   ├── api.md                # Complete REST API reference
│   ├── decisions.md          # Architectural Decision Records (ADRs 001-005)
│   ├── setup.md              # Local installation & database provisioning
│   ├── usage.md              # Operational guide & POS workflows
│   └── development.md        # Code guidelines & extension instructions
├── client/                   # React 18 frontend single-page application
│   ├── public/               # Static assets & HTML root
│   └── src/
│       ├── __tests__/        # Frontend component unit & smoke tests
│       ├── components/       # UI views, forms, dialogs, and reusable alerts
│       ├── services/         # Axios API clients
│       └── theme.js          # Material-UI custom theme palette
├── server/                   # Spring Boot 3 backend application
│   └── src/
│       ├── main/java/...     # Controllers, Services, Repositories, Entities, DTOs
│       └── test/java/...     # Unit & service layer test suites (JUnit 5 + Mockito)
├── .env.example              # Environment variables template
├── .gitignore                # Complete build, IDE, OS, and local ignore rules
├── CHANGELOG.md              # Semantic version release history
├── CONTRIBUTING.md           # Contribution workflow & commit conventions
├── LICENSE                   # Standard MIT License
└── README.md                 # 10-section standardized repository overview
```

---

## 2. Backend Design Patterns & Coding Standards

### A. Layer Responsibilities
1. **Controller Layer (`controller/`):**
   - Pure request/response mapping and parameter validation (`@Valid`).
   - Delegates business operations exclusively to service interfaces.
   - Never interacts directly with database repositories.
2. **Service Layer (`service/`):**
   - Encapsulates all transactional invariants (`@Transactional`).
   - Maps database entities to DTO responses.
   - Throws domain exceptions (`ProductNotFoundException`, `BusinessRuleViolationException`).
3. **Data Access Layer (`repository/`):**
   - Extends Spring Data `JpaRepository`.
   - Uses explicit `JOIN FETCH` queries to eliminate N+1 latency.
4. **Data Transfer Objects (`dto/`):**
   - Separates inbound mutable payloads (`request/`) from read models (`response/`).

### B. Exception Handling Pattern
Domain exceptions must extend `RuntimeException` and are captured by `GlobalExceptionHandler`:

```java
@ExceptionHandler(SaleNotFoundException.class)
public ResponseEntity<ErrorResponse> handleSaleNotFound(SaleNotFoundException ex, HttpServletRequest request) {
    log.error("Sale not found: {}", ex.getMessage());
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
}
```

---

## 3. Frontend Architecture & State Management

- **API Layer (`client/src/services/`):** Encapsulates all backend REST calls through a configured Axios singleton (`api.js`).
- **Component Design:** Stateless presentation components with lifted dialog state.
- **Form Handling:** Controlled inputs with immediate numeric sanitization (`parseInt(..., 10)` / `parseFloat(...)`).

---

## 4. Adding a New Feature: Step-by-Step

### Adding a New Entity (e.g. `DiscountCoupon`):
1. **Entity Definition:** Create `DiscountCoupon.java` in `server/src/main/java/.../entity/`.
2. **Repository:** Create `DiscountCouponRepository.java` in `repository/`.
3. **DTOs:** Create `CouponRequestDTO` and `CouponResponseDTO` in `dto/`.
4. **Service:** Create `CouponService` with `@Transactional` business logic.
5. **Controller:** Create `CouponController` with `@Valid` input parameters.
6. **Tests:** Write unit test in `server/src/test/java/.../service/CouponServiceTest.java`.
7. **Frontend Service & View:** Add `couponService.js` and corresponding React UI components in `client/src/`.

---

## 5. Running Automated Test Suites

### Backend Unit Tests
```bash
cd server
# Run all unit tests with Maven Wrapper
./mvnw test

# Run a specific test class
./mvnw test -Dtest=ProductServiceTest
```

### Frontend Component Tests
```bash
cd client
npm test -- --watchAll=false
```

---

## 6. Commit & Branching Hygiene

- Follow **Conventional Commits**: `feat:`, `fix:`, `docs:`, `chore:`, `refactor:`, `test:`, `ci:`.
- All PRs require green CI builds and passing unit test suites.
