# Sale & Inventory Management — Frontend Client

The frontend client for the **Sale & Inventory Management System**, built with React 18 and Material-UI (MUI v5).

---

## Features
- **Product Inventory Grid:** Powered by `@mui/x-data-grid` for sortable, paginated, and real-time filtered stock management.
- **Interactive Point-of-Sale Dialogs:** Dynamic stock deduction modal (`SaleForm`) and supplier restocking modal (`PurchaseForm`).
- **Customer Sales Analytics Dashboard:** Customer order history, status filter (`CONFIRMED` / `CANCELLED`), and aggregate revenue metric cards.
- **Feedback & Notifications:** Contextual error alerts (`ErrorAlert`), progress indicators (`LoadingSpinner`), and transient toast notifications (`SuccessSnackbar`).

---

## Quickstart

```bash
# Install dependencies
npm install

# Run local development server (defaults to port 3000)
npm start

# Run unit tests
npm test -- --watchAll=false

# Build production bundle
npm run build
```

---

## Environment Configuration

Configure the API base URL in your `.env` or `.env.local` file:

```env
REACT_APP_API_BASE_URL=http://localhost:8080/api
```
