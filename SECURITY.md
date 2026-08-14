# Security Policy

## Supported Versions

We actively maintain and provide security updates for the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

---

## Reporting a Vulnerability

The **Sale & Inventory Management System** team takes security issues seriously. If you discover a security vulnerability, please do NOT create a public GitHub issue.

### Reporting Process:
1. Send an email to the project maintainer at `saad.mughal@example.com` (or submit a confidential security advisory through GitHub).
2. Include the following details in your report:
   - Description of the vulnerability and its potential impact.
   - Step-by-step reproduction steps or proof-of-concept (PoC).
   - Component affected (`server/`, `client/`, or configuration).
   - Suggested mitigation or fix, if available.

### Response Timeline:
- **Initial Acknowledgment:** Within 48 hours of receipt.
- **Vulnerability Assessment:** Within 5 business days.
- **Fix & Disclosure Coordination:** A patch will be prepared and published in a security release, with credit given to the reporter upon public disclosure.

---

## Security Best Practices for Deployments

- Always replace default database passwords in `.env` before deploying to production.
- Use TLS/HTTPS termination reverse proxies (e.g. Nginx, Cloudflare) in front of the application.
- Restrict database port access (`3306`) strictly to the application host.
