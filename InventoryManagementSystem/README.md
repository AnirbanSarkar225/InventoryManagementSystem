# 📦 Inventory Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Swing](https://img.shields.io/badge/Java%20Swing-UI-blue?style=for-the-badge&logo=java&logoColor=white)
![License](https://img.shields.io/badge/License-Academic-green?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=for-the-badge)

**A full-featured desktop Inventory Management System built with Java Swing and SQLite.**  
Designed as a college project demonstrating real-world software engineering — layered architecture, DAO pattern, service layer, exception handling, and unit testing.

[Features](#-features) • [Getting Started](#-getting-started) • [Architecture](#-architecture) • [Tech Stack](#-tech-stack)

</div>

---

## ✨ Features

### 🔐 User Authentication & Role Management
- Secure login with **SHA-256 hashed passwords**
- Three role levels: **Admin**, **Manager**, **Staff**
- Admins can create, edit, deactivate, and delete users
- Role-based tab visibility — User Management is Admin-only

### 📦 Product Management
- Full **CRUD** for all products
- Fields: Name, Category, Quantity, Price, Supplier, Expiry Date, Reorder Level
- Double-click any row to edit instantly
- Real-time search by name, category, or supplier
- Auto status tags — **OK**, **LOW STOCK**, **EXPIRED**

### 📊 Stock Management
- **Stock In** — Record incoming stock with quantity, price, remarks
- **Stock Out** — Record outgoing stock with available quantity validation
- **Adjust Stock** — Manually correct levels with reason tracking
- Every operation is logged automatically to transaction history
- Prevents overselling with `InsufficientStockException`

### 🏭 Supplier Management
- Full CRUD for suppliers with contact person, phone, email, address
- Search by name, contact, or email

### 🔄 Transaction History
- Complete immutable audit trail of every stock movement
- Filter by type: **Stock In**, **Stock Out**, **Adjustment**
- Shows quantity, unit price, total value, timestamp, and who performed it

### 🗂️ Category Management
- Create and manage product categories
- Pre-seeded with 5 default categories on first launch

### 📈 Dashboard
- 4 live metric cards: Total Products, Inventory Value, Low Stock Alerts, Expired Items
- Alerts table for low-stock and expired products
- Recent Transactions table (last 10 movements)

### 📋 Reports & Export
- **Full Inventory CSV** — all products with status
- **Transaction History CSV** — complete transaction log
- **Inventory Summary TXT** — formatted plain-text report with totals
- **Low Stock Report** — view and export items needing restock

### 👥 User Management *(Admin Only)*
- Add, edit, delete users
- Change passwords, toggle active/inactive status

---

## 🚀 Getting Started

### Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 21+ | [adoptium.net](https://adoptium.net) |
| Apache Maven | 3.6+ | [maven.apache.org](https://maven.apache.org) |

### Clone & Run

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/InventoryManagementSystem.git
cd InventoryManagementSystem

# Build the fat JAR
mvn clean package

# Run
java -jar target/InventoryManagementSystem.jar
```

### Run with VS Code

1. Open the project folder in VS Code
2. Install **Extension Pack for Java** by Microsoft
3. Open `src/main/java/com/inventory/Main.java`
4. Click **▶ Run** above the `main` method

### Default Login

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin` | ADMIN |

> The SQLite database (`inventory.db`) is auto-created in the working directory on first launch.

### Run Tests

```bash
mvn test
```

---

## 🏗️ Architecture

```
┌─────────────────────────────────────┐
│           UI Layer (Swing)          │
│  LoginFrame, MainFrame, Panels...   │
├─────────────────────────────────────┤
│        Service Layer (Logic)        │
│  ProductService, UserService...     │
├─────────────────────────────────────┤
│         DAO Layer (Database)        │
│  ProductDAO, TransactionDAO...      │
├─────────────────────────────────────┤
│         SQLite Database             │
│         inventory.db                │
└─────────────────────────────────────┘
```

---

## 📁 Project Structure

```
InventoryManagementSystem/
├── src/
│   ├── main/java/com/inventory/
│   │   ├── Main.java
│   │   ├── model/        # Product, Supplier, Transaction, User, Category
│   │   ├── dao/          # Interfaces + JDBC implementations
│   │   ├── service/      # Business logic layer
│   │   ├── ui/           # Swing panels and frames
│   │   ├── util/         # DB connection, password, validation, export
│   │   └── exception/    # Custom exceptions
│   └── test/java/com/inventory/   # JUnit 5 tests
├── pom.xml
└── README.md
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| UI | Java Swing |
| Database | SQLite 3 |
| Build | Apache Maven |
| Testing | JUnit 5 |
| Security | SHA-256 |

---

## 🧪 Tests

| Test Class | Coverage |
|------------|----------|
| `ProductServiceTest` | CRUD, stock in/out, adjust, low stock |
| `UserServiceTest` | Login, logout, roles, duplicate check |
| `SupplierServiceTest` | Full CRUD, search |
| `ValidationUtilTest` | Email, phone, numeric validation |
| `PasswordUtilTest` | Hashing, verification |

---

## 👨‍💻 Author

**Anirban Sarkar**

---

<div align="center">
  <strong>⭐ If you found this useful, give it a star! ⭐</strong>
</div>
