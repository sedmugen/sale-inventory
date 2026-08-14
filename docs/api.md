# REST API Specification: Sale & Inventory Management System

The **Sale & Inventory Backend** exposes a standardized, synchronous JSON REST API over HTTP.

- **Base URL (Local Development):** `http://localhost:8080/api`
- **Content-Type:** `application/json`
- **Accept:** `application/json`

---

## Standard Error Response Format

All error responses adhere to a consistent error schema:

```json
{
  "timestamp": "2026-08-15T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "path": "/api/products",
  "validationErrors": {
    "code": "Product code is required",
    "unitPrice": "Unit price must be non-negative"
  }
}
```

| HTTP Status Code | Meaning |
|---|---|
| `200 OK` | Request succeeded; response payload contains requested entity/list. |
| `201 CREATED` | Resource successfully created. |
| `204 NO CONTENT` | Resource successfully deleted or updated without return payload. |
| `400 BAD REQUEST` | Validation failure or business rule violation (e.g. insufficient stock). |
| `404 NOT FOUND` | Requested resource ID or code does not exist. |
| `409 CONFLICT` | Unique constraint conflict (e.g. duplicate email or product code). |
| `500 INTERNAL ERROR` | Unhandled server exception. |

---

## 1. Products API (`/api/products`)

### `GET /api/products`
Retrieves lightweight list of all products.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "code": "P-1001",
    "name": "Wireless Mouse",
    "unitPrice": 25.99,
    "currentStock": 48,
    "active": true,
    "category": "Electronics",
    "brand": "Logitech"
  }
]
```

---

### `GET /api/products/{id}`
Retrieves complete product record including embedded metadata details.

**Response:** `200 OK`
```json
{
  "id": 1,
  "code": "P-1001",
  "name": "Wireless Mouse",
  "unitPrice": 25.99,
  "currentStock": 48,
  "active": true,
  "productDetail": {
    "id": 1,
    "brand": "Logitech",
    "category": "Electronics",
    "description": "Ergonomic wireless mouse with 2.4GHz receiver",
    "minStockLevel": 10,
    "taxRate": 17.0
  }
}
```

---

### `POST /api/products`
Creates a new product with optional specification details.

**Request Body:**
```json
{
  "code": "P-1006",
  "name": "Noise Cancelling Headphones",
  "unitPrice": 149.99,
  "currentStock": 15,
  "active": true,
  "productDetail": {
    "brand": "Sony",
    "category": "Audio",
    "description": "Wireless over-ear noise-cancelling headphones",
    "minStockLevel": 5,
    "taxRate": 17.0
  }
}
```

**Response:** `201 CREATED` (Returns created `ProductResponseDTO`)

---

### `PUT /api/products/{id}`
Updates existing product information.

**Request Body:** Same schema as `POST /api/products`.  
**Response:** `200 OK`

---

### `DELETE /api/products/{id}`
Performs a soft delete by marking `active = false`.

**Response:** `204 NO CONTENT`

---

### `GET /api/products/low-stock`
Retrieves products operating below their configured minimum safety stock threshold.

**Query Parameters:**
- `limit` *(optional, default: 5)*: Maximum number of records to return.

**Response:** `200 OK`
```json
[
  {
    "id": 5,
    "code": "P-1005",
    "name": "Webcam HD",
    "unitPrice": 65.00,
    "currentStock": 3,
    "minStockLevel": 5,
    "category": "Electronics",
    "active": true,
    "stockDeficit": 2
  }
]
```

---

### `GET /api/products/search`
Searches products by name (case-insensitive substring match).

**Query Parameters:**
- `name` *(string)*: Product name query.

---

## 2. Customers API (`/api/customers`)

### `GET /api/customers`
Retrieves all customer records.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "+92-300-1234567",
    "address": "123 Main Street, Lahore",
    "blocked": false
  }
]
```

---

### `POST /api/customers`
Registers a new customer.

**Request Body:**
```json
{
  "name": "Alice Johnson",
  "email": "alice.j@example.com",
  "phone": "+92-311-5551234",
  "address": "789 Blue Ridge Ave",
  "blocked": false
}
```
**Response:** `201 CREATED`

---

### `PATCH /api/customers/{id}/toggle-block`
Toggles customer blocked/active status.

**Response:** `200 OK`

---

### `GET /api/customers/{id}/sales`
Retrieves full sales transaction history for the specified customer.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "date": "2026-08-10T14:30:00",
    "quantity": 2,
    "unitPrice": 25.99,
    "totalPrice": 51.98,
    "status": "CONFIRMED",
    "product": {
      "id": 1,
      "code": "P-1001",
      "name": "Wireless Mouse",
      "currentStock": 48
    },
    "customer": {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com"
    }
  }
]
```

---

## 3. Suppliers API (`/api/suppliers`)

### `GET /api/suppliers`
Retrieves list of all suppliers.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Tech Distributors Inc",
    "email": "contact@techdist.com",
    "phone": "+92-42-11111111",
    "companyName": "Tech Distributors International",
    "address": "Industrial Area, Lahore",
    "active": true
  }
]
```

---

### `POST /api/suppliers`
Registers a new supplier.

**Request Body:**
```json
{
  "name": "Global Components",
  "email": "supply@globalcomp.com",
  "phone": "+92-51-8889999",
  "companyName": "Global Components Ltd",
  "address": "Export Zone, Karachi",
  "active": true
}
```
**Response:** `201 CREATED`

---

### `GET /api/suppliers/{id}/purchases`
Retrieves purchase orders received from the specified supplier.

**Response:** `200 OK`

---

## 4. Sales API (`/api/sales`)

### `POST /api/sales`
Executes an outbound customer sale and automatically decrements inventory.

**Business Rules Enforced:**
- Product must exist, be active, and have sufficient available stock.
- Customer must exist and not be blocked.
- Quantity must be >= 1.

**Request Body:**
```json
{
  "productId": 1,
  "customerId": 1,
  "quantity": 2,
  "unitPrice": 25.99
}
```

**Response:** `201 CREATED`
```json
{
  "id": 10,
  "date": "2026-08-15T10:15:30",
  "quantity": 2,
  "unitPrice": 25.99,
  "totalPrice": 51.98,
  "status": "CONFIRMED",
  "product": {
    "id": 1,
    "code": "P-1001",
    "name": "Wireless Mouse",
    "currentStock": 46
  },
  "customer": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com"
  }
}
```

---

## 5. Purchases API (`/api/purchases`)

### `POST /api/purchases`
Logs an inbound supplier purchase order and automatically increments inventory.

**Business Rules Enforced:**
- Product must exist.
- Supplier must exist and be active.
- Quantity must be >= 1.
- Unit cost must be >= 0.

**Request Body:**
```json
{
  "productId": 1,
  "supplierId": 1,
  "quantity": 50,
  "unitCost": 18.00
}
```

**Response:** `201 CREATED`
```json
{
  "id": 15,
  "date": "2026-08-15T11:00:00",
  "quantity": 50,
  "unitCost": 18.00,
  "totalCost": 900.00,
  "status": "RECEIVED",
  "product": {
    "id": 1,
    "code": "P-1001",
    "name": "Wireless Mouse",
    "currentStock": 96
  },
  "supplier": {
    "id": 1,
    "name": "Tech Distributors Inc",
    "companyName": "Tech Distributors International"
  }
}
```
