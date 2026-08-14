# Architectural Decision Records (ADRs)

This document chronicles the fundamental architectural design choices, context, tradeoffs, and outcomes adopted in the **Sale & Inventory Management System**.

---

## ADR-001: Monorepo Architecture with Decoupled Client and Server

- **Status:** Accepted
- **Context:** The system consists of an API backend (`server/`) and a single-page web client (`client/`). Managing multiple independent repositories for tightly coupled domains increases administrative overhead, synchronization drift, and onboarding complexity.
- **Decision:** Consolidate the client and server into a single monorepo with distinct subfolders (`client/`, `server/`, `docs/`, `assets/`), each maintaining its own dependencies, build configurations, and tests.
- **Consequences:**
  - *Positive:* Single source of truth; unified versioning, documentation, and CI/CD pipelines.
  - *Negative:* Requires configuring independent root scripts or directory-specific execution.

---

## ADR-002: Declarative Transaction Boundaries & Atomic Stock Deductions

- **Status:** Accepted
- **Context:** In multi-user environments, race conditions during order placement or inventory arrivals could result in phantom stock counts or negative inventory levels.
- **Decision:** Enforce Spring declarative transactions (`@Transactional`) across all service mutation methods (`SaleService.createSale`, `PurchaseService.createPurchase`). All operations—product lookup, stock deduction/replenishment, and sale/purchase recording—execute within a single atomic database transaction. If any validation fails, the transaction rolls back completely.
- **Consequences:**
  - *Positive:* ACID compliance guaranteed; zero risk of partial updates where stock decreases without an order record.
  - *Negative:* Database row-level locking during high concurrency transaction windows.

---

## ADR-003: Soft Deletion Strategy for Products

- **Status:** Accepted
- **Context:** When a product is discontinued, hard-deleting the record (`DELETE FROM product WHERE id = ...`) would cascade or violate foreign key constraints across historical `purchase` and `sale` transaction tables.
- **Decision:** Implement a soft-deletion pattern via an `active` boolean flag. Deleting a product sets `active = false`. Inactive products are retained for transaction audit lookups and customer dashboard displays but are excluded from active sales flows and default product picker dropdowns.
- **Consequences:**
  - *Positive:* Full preservation of financial ledger history and relational referential integrity.
  - *Negative:* Requires explicit filtering (`findByActiveTrue()`) in queries where only active goods should appear.

---

## ADR-004: Strict DTO Encapsulation vs Domain Entity Exposure

- **Status:** Accepted
- **Context:** Directly exposing JPA entities to REST endpoints causes serialization loops (bidirectional relationships), over-exposes internal database schema details, and prevents tailored request validations.
- **Decision:** Maintain separate, dedicated Request DTOs (`ProductRequestDTO`, `SaleRequestDTO`) and Response DTOs (`ProductResponseDTO`, `ProductListDTO`, `SaleResponseDTO`). Controllers consume and return DTOs exclusively; entity conversion is handled deterministically in the service layer.
- **Consequences:**
  - *Positive:* Decoupled API contracts from database schema; prevents mass-assignment vulnerabilities; supports lightweight response representations (e.g. `ProductListDTO` vs full `ProductResponseDTO`).
  - *Negative:* Requires mapping logic between entities and DTOs.

---

## ADR-005: Material-UI (MUI v5) and DataGrid for Presentation

- **Status:** Accepted
- **Context:** Business inventory applications require dense, scannable tabular interfaces, real-time sorting, pagination, and predictable component behavior.
- **Decision:** Utilize Material-UI v5 paired with `@mui/x-data-grid` as the primary UI design system, supported by a central theme palette (`theme.js`).
- **Consequences:**
  - *Positive:* Out-of-the-box accessible keyboard navigation, virtualized data rendering for large catalogs, and cohesive aesthetic styling.
  - *Negative:* Increases frontend client bundle size compared to vanilla HTML/CSS.
