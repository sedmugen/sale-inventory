# Contributing to Sale & Inventory Management System

Thank you for your interest in contributing to the **Sale & Inventory Management System**! This document provides guidelines and workflows for contributing code, documentation, and bug fixes.

---

## Code of Conduct

We are committed to providing a welcoming, inclusive, and harassment-free experience for everyone. Please be respectful, constructive, and professional in all interactions.

---

## Git Workflow

### 1. Branch Naming Conventions
All branch names must follow the kebab-case pattern with an approved category prefix:

```text
<category>/<short-description>
```

**Approved Categories:**
- `feature/` — New features or enhancements (e.g., `feature/export-csv-reports`)
- `bugfix/` — Bug fixes (e.g., `bugfix/stock-decrement-race-condition`)
- `hotfix/` — Urgent production patches
- `refactor/` — Code restructuring without feature alterations
- `docs/` — Documentation additions and corrections
- `test/` — Adding or improving automated tests
- `chore/` — Build scripts, dependencies, tooling updates

### 2. Commit Message Standards (Conventional Commits)
We enforce the **Conventional Commits** specification. Commit messages must be structured as:

```text
<type>(optional-scope): <imperative description>
```

**Approved Types:**
- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation changes
- `style`: Formatting, missing semicolons, no code change
- `refactor`: Code changes that neither fix a bug nor add a feature
- `perf`: Performance improvements
- `test`: Adding or correcting tests
- `build`: Changes affecting the build system or dependencies
- `ci`: Changes to CI configuration files and scripts
- `chore`: General repository maintenance

**Rules:**
- Use the imperative mood in the subject line (e.g., `feat(sales): add customer filter` not `feat: added customer filter`).
- Keep the first line under 72 characters.
- Keep commits atomic: one logical change per commit.

---

## Local Development & Setup

### Prerequisites
- **Java JDK 17+**
- **Node.js 18+ LTS** and **npm**
- **MySQL 8.0+** running locally on port `3306`

### Backend Setup
```bash
cd server
# Copy environment configuration
cp ../.env.example .env
# Run Spring Boot backend
./mvnw clean spring-boot:run
```

### Frontend Setup
```bash
cd client
# Install dependencies
npm install
# Start React development server
npm start
```

---

## Running Tests

Before submitting a Pull Request, verify that all test suites pass:

```bash
# Run backend test suite
cd server
./mvnw test

# Run frontend test suite
cd client
npm test -- --watchAll=false
```

---

## Submitting a Pull Request (PR)

1. Fork the repository and create your branch from `main`.
2. Ensure your code passes all linting, formatting, and unit tests.
3. Write clean, readable code with descriptive comments where necessary.
4. Open a Pull Request against the `main` branch with a clear title and description explaining the changes made.
