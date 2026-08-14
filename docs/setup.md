# Environment Setup & Installation Guide

This guide provides step-by-step instructions for provisioning local development environments for the **Sale & Inventory Management System**.

---

## 1. System Requirements

Ensure the following tools are installed and accessible in your system's `PATH`:

| Requirement | Minimum Version | Recommended Version | Verification Command |
|---|---|---|---|
| **Java Development Kit (JDK)** | 17 LTS | OpenJDK 17 or 21 | `java -version` |
| **Node.js** | 18 LTS | Node.js 18 or 20 | `node -v` |
| **npm** | 9.0+ | 10.0+ | `npm -v` |
| **MySQL Server** | 8.0+ | 8.0+ / 8.4 LTS | `mysql --version` |
| **Git** | 2.30+ | Latest | `git --version` |

---

## 2. Database Provisioning

### A. Local MySQL Instance
1. Start your local MySQL server service.
2. Connect to MySQL via CLI or GUI (e.g. MySQL Workbench, DBeaver):
   ```bash
   mysql -u root -p
   ```
3. Execute the schema initialization statement:
   ```sql
   CREATE DATABASE IF NOT EXISTS `sales-inventory-db`
     CHARACTER SET utf8mb4
     COLLATE utf8mb4_unicode_ci;
   ```

### B. Verification
Verify the database exists:
```sql
SHOW DATABASES LIKE 'sales-inventory-db';
```

---

## 3. Environment Variables Configuration

Copy the sample environment file to configure your local runtime:

```bash
# From repository root
cp .env.example .env
```

### Environment Configuration Reference

| Variable | Default Value | Description |
|---|---|---|
| `DB_HOST` | `localhost` | Hostname of the MySQL database server |
| `DB_PORT` | `3306` | MySQL connection port |
| `DB_NAME` | `sales-inventory-db` | Database schema name |
| `DB_USERNAME` | `root` | MySQL user account |
| `DB_PASSWORD` | *(empty)* | MySQL user password |
| `SERVER_PORT` | `8080` | Spring Boot backend HTTP port |
| `REACT_APP_API_BASE_URL` | `http://localhost:8080/api` | Base URL used by the React Axios client |

---

## 4. Backend Setup (Spring Boot)

The backend utilizes the Maven Wrapper (`mvnw`), eliminating the requirement for a global Maven installation.

### A. Run via Maven Wrapper
```bash
# Navigate to the backend directory
cd server

# Linux / macOS:
./mvnw clean spring-boot:run

# Windows (PowerShell / Command Prompt):
.\mvnw.cmd clean spring-boot:run
```

### B. Startup Behavior
- On initial startup, Hibernate validates and automatically provisions relational tables (`product`, `product_detail`, `customer`, `supplier`, `purchase`, `sale`).
- The `DataLoader` component detects if the database is empty and auto-seeds sample products, customers, suppliers, and historical transactions.
- Health endpoint / root API available at: `http://localhost:8080/api`

---

## 5. Frontend Setup (React 18)

### A. Install Dependencies
```bash
# Navigate to the frontend directory
cd client

# Install NPM dependencies
npm install
```

### B. Launch Development Server
```bash
npm start
```
The React development server compiles the bundle and automatically opens `http://localhost:3000` in your default browser.

---

## 6. Verifying the Full Installation

1. Open `http://localhost:3000/products` in your browser.
2. Confirm the **Product Inventory** DataGrid displays seeded records (e.g., *Wireless Mouse*, *Mechanical Keyboard*).
3. Test a quick sale:
   - Click **Sell** on any active product.
   - Select a customer from the dropdown, choose a quantity, and submit.
   - Observe real-time stock deduction in the grid.
4. Navigate to `/dashboard` and select the customer to confirm the sale appears in their transaction ledger.

---

## 7. Troubleshooting Common Issues

### Issue: `The JAVA_HOME environment variable is not defined correctly`
**Solution:** Ensure `JAVA_HOME` points to your JDK directory (e.g., `C:\Program Files\Java\jdk-17` on Windows, or `/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home` on macOS).

### Issue: `Communications link failure / Connection refused (MySQL)`
**Solution:**
1. Verify MySQL service status (`sudo systemctl status mysql` or Windows Services).
2. Confirm credentials in `.env` match your local MySQL root password.

### Issue: `Port 8080 or Port 3000 already in use`
**Solution:**
- Backend: Change `SERVER_PORT=8081` in your `.env` and adjust `REACT_APP_API_BASE_URL=http://localhost:8081/api`.
- Frontend: Accept the prompt to run on alternative port (e.g., `3001`) or set `PORT=3001 npm start`.
