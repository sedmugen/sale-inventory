# System Architecture: Sale & Inventory Management System

This document outlines the architectural blueprint, structural diagrams, relational schema, and transaction execution flows for the **Sale & Inventory Management System**.

---

## 1. System Overview & Tier Architecture

The system is organized as a decoupled multi-tier enterprise web application adhering to clean layered architectural principles.

```mermaid
graph TD
    Client["React 18 Single Page Application<br>(MUI v5, DataGrid, Axios)"]
    
    subgraph SpringBootApp["Spring Boot 3.5.7 Backend Application"]
        subgraph PresentationLayer["Controller Layer"]
            PC["ProductController<br>/api/products"]
            CC["CustomerController & Sales<br>/api/customers"]
            SC["SupplierController & Purchases<br>/api/suppliers"]
            TxC["PurchaseController & SaleController<br>/api/purchases, /api/sales"]
        end

        subgraph ServiceLayer["Service Layer (@Transactional)"]
            PS["ProductService"]
            CS["CustomerService"]
            SS["SupplierService"]
            PurS["PurchaseService"]
            SaleS["SaleService"]
        end

        subgraph PersistenceLayer["Data Access Layer (Spring Data JPA)"]
            PR["ProductRepository"]
            PDR["ProductDetailRepository"]
            CR["CustomerRepository"]
            SR["SupplierRepository"]
            PurR["PurchaseRepository"]
            SaleR["SaleRepository"]
        end
    end

    subgraph DatabaseLayer["Relational Storage"]
        MySQL[("MySQL 8.0+<br>sales-inventory-db")]
    end

    Client -->|REST / JSON over HTTP| PresentationLayer
    PresentationLayer --> ServiceLayer
    ServiceLayer --> PersistenceLayer
    PersistenceLayer -->|Hibernate ORM / JDBC| MySQL
```

---

## 2. Relational Entity-Relationship (ER) Diagram

The persistence schema consists of 6 normalized relational tables with enforced foreign key constraints, column validations, and cascade rules.

```mermaid
erDiagram
    PRODUCT ||--o| PRODUCT_DETAIL : "has metadata"
    PRODUCT ||--o{ PURCHASE : "received in"
    PRODUCT ||--o{ SALE : "sold in"
    SUPPLIER ||--o{ PURCHASE : "supplies"
    CUSTOMER ||--o{ SALE : "places"

    PRODUCT {
        bigint id PK "Auto Increment"
        varchar code UK "Unique Product Code (e.g., P-1001)"
        varchar name "Product Name"
        decimal unit_price "Selling Price (>= 0)"
        int current_stock "Available Quantity (>= 0)"
        boolean active "Soft-delete status flag"
    }

    PRODUCT_DETAIL {
        bigint id PK "Auto Increment"
        bigint product_id FK,UK "References PRODUCT(id)"
        varchar brand "Manufacturer Brand"
        varchar category "Product Category"
        varchar description "Detailed Description"
        int min_stock_level "Safety Threshold"
        double tax_rate "Tax Rate Percentage"
    }

    CUSTOMER {
        bigint id PK "Auto Increment"
        varchar name "Customer Full Name"
        varchar email UK "Unique Email Address"
        varchar phone "Contact Number"
        varchar address "Shipping/Billing Address"
        boolean blocked "Credit/Status Block Flag"
    }

    SUPPLIER {
        bigint id PK "Auto Increment"
        varchar name "Contact Person"
        varchar email UK "Unique Email Address"
        varchar phone "Contact Phone"
        varchar company_name "Corporate Name"
        varchar address "Warehouse/Office Address"
        boolean active "Active Supplier Flag"
    }

    PURCHASE {
        bigint id PK "Auto Increment"
        bigint product_id FK "References PRODUCT(id)"
        bigint supplier_id FK "References SUPPLIER(id)"
        datetime date "Transaction Timestamp"
        int quantity "Units Inbound (>= 1)"
        decimal unit_cost "Cost Per Unit (>= 0)"
        decimal total_cost "Calculated: quantity * unit_cost"
        varchar status "RECEIVED | CANCELLED"
    }

    SALE {
        bigint id PK "Auto Increment"
        bigint product_id FK "References PRODUCT(id)"
        bigint customer_id FK "References CUSTOMER(id)"
        datetime date "Transaction Timestamp"
        int quantity "Units Outbound (>= 1)"
        decimal unit_price "Custom/Catalog Price"
        decimal total_price "Calculated: quantity * unit_price"
        varchar status "CONFIRMED | CANCELLED"
    }
```

---

## 3. Transactional Execution Flows

### A. Sales Order Processing (`SaleService.createSale`)

The sales workflow ensures strict atomicity and validates all domain invariants before decrementing inventory:

```mermaid
sequenceDiagram
    autonumber
    actor User as User / POS Cashier
    participant UI as React Client (SaleForm)
    participant API as SaleController
    participant Service as SaleService (@Transactional)
    participant PRepo as ProductRepository
    participant CRepo as CustomerRepository
    participant SRepo as SaleRepository
    participant DB as MySQL Database

    User->>UI: Submit Sale (productId, customerId, quantity, unitPrice)
    UI->>API: POST /api/sales
    API->>Service: createSale(SaleRequestDTO)
    
    Service->>PRepo: findById(productId)
    PRepo-->>Service: Product entity (Stock: N, Active: true/false)
    
    alt Product Inactive or Out of Stock
        Service-->>API: Throw BusinessRuleViolationException
        API-->>UI: HTTP 400 Bad Request
    end

    alt Requested Quantity > Available Stock
        Service-->>API: Throw BusinessRuleViolationException
        API-->>UI: HTTP 400 Bad Request ("Insufficient stock")
    end

    Service->>CRepo: findById(customerId)
    CRepo-->>Service: Customer entity (Blocked: true/false)

    alt Customer Blocked
        Service-->>API: Throw BusinessRuleViolationException
        API-->>UI: HTTP 400 Bad Request ("Customer is blocked")
    end

    Note over Service,DB: Atomic Stock Deduction & Sale Persist
    Service->>PRepo: save(Product with Stock: N - quantity)
    Service->>SRepo: save(Sale with CONFIRMED status)
    SRepo->>DB: INSERT INTO sale ... & UPDATE product ...
    DB-->>SRepo: OK
    
    Service-->>API: SaleResponseDTO
    API-->>UI: HTTP 201 Created (SaleResponseDTO)
    UI-->>User: Refresh Product Grid & Display Success Notification
```

---

### B. Purchase Order Fulfillment (`PurchaseService.createPurchase`)

```mermaid
sequenceDiagram
    autonumber
    actor User as Warehouse Manager
    participant UI as React Client (PurchaseForm)
    participant API as PurchaseController
    participant Service as PurchaseService (@Transactional)
    participant PRepo as ProductRepository
    participant SupRepo as SupplierRepository
    participant PurRepo as PurchaseRepository
    participant DB as MySQL Database

    User->>UI: Submit Purchase (productId, supplierId, quantity, unitCost)
    UI->>API: POST /api/purchases
    API->>Service: createPurchase(PurchaseRequestDTO)

    Service->>PRepo: findById(productId)
    PRepo-->>Service: Product entity (Stock: N)

    Service->>SupRepo: findById(supplierId)
    SupRepo-->>Service: Supplier entity (Active: true/false)

    alt Supplier Inactive
        Service-->>API: Throw BusinessRuleViolationException ("Supplier is inactive")
        API-->>UI: HTTP 400 Bad Request
    end

    Note over Service,DB: Stock Increment & Purchase Inflow Record
    Service->>PRepo: save(Product with Stock: N + quantity)
    Service->>PurRepo: save(Purchase with RECEIVED status)
    PurRepo->>DB: INSERT INTO purchase ... & UPDATE product ...
    DB-->>PurRepo: OK

    Service-->>API: PurchaseResponseDTO
    API-->>UI: HTTP 201 Created (PurchaseResponseDTO)
    UI-->>User: Refresh Product Grid & Display Success Notification
```

---

## 4. Query Optimization & Performance Design

To prevent the **N+1 Query Problem** inherent in relational ORM mappings when retrieving collections with associated lazy-loaded entities (`Purchase` -> `Product`, `Supplier`; `Sale` -> `Product`, `Customer`), custom repository methods employ explicit JPQL `JOIN FETCH` queries:

```java
@Query("""
    SELECT s FROM Sale s 
    JOIN FETCH s.product 
    JOIN FETCH s.customer 
    ORDER BY s.date DESC
""")
List<Sale> findAllWithDetails();
```

This single-query fetch strategy reduces database round trips from `1 + 2N` to exactly `1` query per list invocation, keeping latency minimal even under high record volume.
