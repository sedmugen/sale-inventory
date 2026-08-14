# Changelog

All notable changes to the **Sale & Inventory Management System** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-08-15

### Added
- **Core Inventory Domain:** Full CRUD management for products, product specifications, minimum stock thresholds, and tax rates.
- **Inbound Logistics & Purchases:** Supplier management and automated purchase recording with automatic stock replenishment.
- **Outbound Logistics & Sales:** Retail customer sales pipeline with real-time stock deductions, unit price overrides, and blocked account validation.
- **Customer Sales Analytics Dashboard:** Interactive customer order history, status filtering (`CONFIRMED` / `CANCELLED`), and aggregate revenue metric cards.
- **Low Stock Monitoring:** Backend and UI alerting for products falling below safety thresholds.
- **API Documentation & Architecture Specifications:** Comprehensive system architecture, C4 diagrams, ER diagrams, and endpoint specifications.
- **Automated Service Unit Testing:** Comprehensive unit tests for `ProductService`, `SaleService`, `PurchaseService`, and `CustomerService`.
- **Global Exception Handling:** Standardized `ErrorResponse` schema and domain-specific not-found exceptions.
- **Seed Data Pipeline:** Automatic population of representative products, customers, suppliers, purchases, and sales on initial startup.

### Changed
- Parameterized database connection credentials in `application.properties` with environment variable support.
- Configured dynamic API URL resolution in frontend Axios client.
- Hardened numeric inputs and validation states in sales and purchase transaction dialogs.

### Security
- Excluded sensitive environment properties and local configuration from version control.
- Hardened exception reporting using SLF4J structured logging.
