# Digital Pharmacy Management System

## 📋 Project Overview

The **Digital Pharmacy Management System** is a Java-based desktop application designed to automate essential pharmacy workflows. It replaces manual record-keeping with a secure digital platform that manages medicine inventory, customer records, prescriptions, and sales transactions.

**Developed as a Final Year University Project (2025-2026)**

---

## ✨ Features

| Module | Capabilities |
|---|---|
| **Login System** | Secure authentication with role-based access (Admin/Pharmacist) |
| **Dashboard** | Real-time summary cards — medicines, low stock, expiring, sales |
| **Medicine Management** | Full CRUD — Add, Edit, Delete, Search medicines |
| **Stock Monitor** | Color-coded alerts — Low Stock (amber), Expiring Soon (orange), Expired (red) |
| **Customer Management** | Full CRUD for customer records with search |
| **Prescriptions** | Create prescriptions with multiple medicine items, view history |
| **Sales / Billing** | Cart-based billing with auto-totals, stock decrement, and receipt generation |
| **Reports** | Daily sales, monthly revenue, stock reports, expiry reports |

---

## 🛠 Technology Stack

| Component | Technology |
|---|---|
| Language | Java 8+ |
| GUI Framework | Java Swing |
| Database | MySQL 5.7+ |
| JDBC Driver | mysql-connector-java |
| IDE | NetBeans 8.2+ |
| DB Management | SQLyog / phpMyAdmin |

---

## 📂 Project Structure

```
DigitalPharmacy/
├── src/
│   ├── model/              # POJO classes
│   │   ├── User.java
│   │   ├── Medicine.java
│   │   ├── Customer.java
│   │   ├── Prescription.java
│   │   ├── PrescriptionItem.java
│   │   ├── Sale.java
│   │   └── SaleItem.java
│   ├── dao/                 # Database access layer
│   │   ├── DBConnection.java
│   │   ├── UserDAO.java
│   │   ├── MedicineDAO.java
│   │   ├── CustomerDAO.java
│   │   ├── PrescriptionDAO.java
│   │   └── SaleDAO.java
│   ├── gui/                 # Swing GUI forms
│   │   ├── LoginForm.java       (Entry Point)
│   │   ├── DashboardForm.java
│   │   ├── MedicineForm.java
│   │   ├── StockMonitorPanel.java
│   │   ├── CustomerForm.java
│   │   ├── PrescriptionForm.java
│   │   ├── SalesForm.java
│   │   └── ReportsPanel.java
│   └── util/                # Utility classes
│       ├── TableUtil.java
│       └── ValidationUtil.java
├── sql/
│   └── pharmacy_db.sql      # Database schema + sample data
├── docs/
│   ├── CODE_DOCUMENTATION.md
│   └── SETUP_INSTRUCTIONS.md
├── lib/
│   └── (mysql-connector-java-8.x.jar)
└── README.md
```

---

## 🚀 Quick Start

### Prerequisites
- JDK 8 or later
- MySQL Server 5.7+
- NetBeans IDE (recommended) or any Java IDE
- MySQL Connector/J JAR file

### Setup Steps

1. **Clone/Download** this project
2. **Import** the database: Run `sql/pharmacy_db.sql` in MySQL
3. **Configure** database credentials in `src/dao/DBConnection.java`
4. **Add** `mysql-connector-java-8.x.jar` to your project libraries
5. **Run** `src/gui/LoginForm.java` as the main class

### Default Login Credentials

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | Admin |
| `pharmacist1` | `pharma123` | Pharmacist |

---

## 📖 Documentation

- **[Setup Instructions](docs/SETUP_INSTRUCTIONS.md)** — Step-by-step Windows setup guide
- **[Code Documentation](docs/CODE_DOCUMENTATION.md)** — Detailed explanation of all classes

---

## 📊 Database Schema

The system uses **7 MySQL tables**:

- `users` — Login credentials and user roles
- `medicines` — Medicine inventory (50 sample entries included)
- `customers` — Customer profiles (10 samples)
- `prescriptions` — Prescription headers
- `prescription_items` — Prescription line items
- `sales` — Sales transaction headers
- `sale_items` — Sales line items

---

## 👨‍💻 Author

Final Year Project — Digital Pharmacy Management System  
Academic Year: 2025-2026

---

## 📝 License

This project is developed for academic/educational purposes.
