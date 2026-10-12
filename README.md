# Hotel Management System

A desktop hotel management application developed in Java using JavaFX for the graphical user interface and MySQL for persistent data storage.

The project demonstrates object-oriented programming, database integration, layered application architecture, and the management of common hotel operations.

Originally developed as a team programming project, it is being published as part of my personal GitHub portfolio.

## Features

- **Receptionist login:** Authenticate staff using a staff ID and password.
- **Dashboard:** Provide a central interface for accessing hotel management functions.
- **Guest management:** Store and manage guest information.
- **Room management:** View room information, room types, and room availability status.
- **Reservation management:** Create and manage reservations and reservation statuses.
- **Billing and invoices:** Manage invoice information and calculate or display billing totals.
- **Payment records:** Represent payment methods and payment statuses.
- **Database integration:** Persist hotel records in a MySQL database.
- **Graphical interface:** Use JavaFX and FXML to build desktop screens and connect them to application controllers.

## Technologies Used

- **Java** — application logic and object-oriented design
- **JavaFX** — desktop graphical user interface
- **FXML** — declarative UI layouts
- **MySQL** — relational database
- **JDBC** — database connectivity
- **MySQL Connector/J** — JDBC driver
- **IntelliJ IDEA** — development environment

## Architecture

The project separates its responsibilities into packages to improve organization and maintainability.

```text
src/
└── hotel/
    ├── dao/                    # Data access interfaces
    ├── dao_implementation/     # SQL and database operations
    ├── model/                 # Domain entities
    │   └── enums/             # Room, payment, and reservation states
    ├── service/               # Service interfaces
    ├── service_implementation/# Business logic implementations
    ├── ui/                    # Main application and FXML layouts
    ├── ui_controller/         # JavaFX event and screen controllers
    └── util/                  # Database connection utilities
```

### Main Components

- **Model layer:** Represents guests, rooms, reservations, invoices, payments, and receptionists.
- **DAO layer:** Defines data access operations and implements database queries.
- **Service layer:** Separates business operations from direct database access.
- **UI layer:** Contains the JavaFX application entry point and FXML layouts.
- **Controller layer:** Handles user interactions and coordinates UI actions with the service layer.
- **Database utility:** Establishes JDBC connections to MySQL.

## Database

The application uses a MySQL database named `hotel`.

The database supports entities such as:

- Guests
- Rooms
- Reservations
- Receptionists
- Invoices
- Payments

The application uses JDBC to retrieve and modify records. The DAO implementations use prepared statements for database queries.

A database schema and sanitized sample-data script should be included in the repository so other developers can set up their own local database.

## Getting Started

### Prerequisites

- A compatible Java Development Kit (JDK).
- JavaFX SDK compatible with your JDK and project configuration.
- MySQL Server.
- MySQL Connector/J.
- IntelliJ IDEA or another Java IDE.

### Database Setup

1. Install and start MySQL Server.
2. Create a database named `hotel`.
3. Import the sanitized SQL schema and optional sample data supplied with the repository.
4. Configure the database connection using your own local settings.
5. Ensure the configured MySQL account has the necessary permissions.

Never commit real database passwords or sensitive records to GitHub.

### Configure the Application

Configure the database URL, username, and password outside the source code, for example through environment variables.

The database configuration should use local environment-specific values rather than credentials embedded in `DBConnection.java`.

Ensure that JavaFX and MySQL Connector/J are configured in your IDE before running the application.

### Run the Application

1. Clone the repository:

   ```bash
   git clone https://github.com/Hamburger4455/Hotel-Management-System.git
   ```

2. Open the project in IntelliJ IDEA.
3. Configure the JDK and JavaFX libraries.
4. Configure the MySQL database and connection settings.
5. Run `hotel.ui.MainApp`.
6. Sign in using a valid receptionist account in your local database.

The repository should include sanitized setup instructions and test credentials only if those credentials are deliberately created for local demonstration.

## Security Considerations

This is an educational desktop application, not a production-ready hotel management system.

Before deployment, review:

- Database credentials and configuration management.
- Password storage and authentication practices.
- Database permissions and error handling.
- Validation of user input.
- Authorization for sensitive operations.
- Handling of personal guest and payment-related data.

Do not publish real credentials or private customer information.

## Potential Improvements

- Add Maven or Gradle for dependency management.
- Improve input validation and error reporting.
- Add automated tests for services and database operations.
- Improve authentication and authorization.
- Add transaction management for multi-step reservation and billing operations.
- Improve reporting, searching, and filtering.
- Separate environment-specific database settings from source code.
- Provide sanitized SQL setup scripts and clear installation instructions.

## Learning Outcomes

This project provided experience with Java object-oriented programming, JavaFX UI development, FXML, JDBC, MySQL, layered architecture, and collaborative software development.

## Project Context

This project was developed as a team programming exercise and is being published as part of my personal GitHub portfolio. It documents an earlier application-development project and the concepts explored during its development.
