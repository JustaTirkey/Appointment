# University Appointment Scheduler

A web application built with Java Spring Boot and a simple HTML/JavaScript frontend. It allows students to view professors' schedules and request appointments for available time slots. The system supports two user roles: Student and Professor, with role-based redirection upon login.

---

## 🚀 Features

* **User Authentication:** Secure login for both students and professors.
* **Role-Based Access:** Users are redirected to their respective profile pages (student or professor) after logging in.
* **Profile Viewing:** Both students and professors can view their basic profile information.
* **Schedule Viewing:** Students can look up a professor by their ID to see their weekly calendar.
* **Appointment Requests:** Students can request an open time slot from a professor's schedule.
* **Dynamic UI Updates:** The interface provides real-time feedback when an appointment is successfully requested.

---

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot, Spring Web
* **Frontend:** HTML, JavaScript (using Fetch API for AJAX calls)
* **Database:** MySQL
* **Build Tool:** Apache Maven

---

## ⚙️ Setup and Installation

Follow these steps to get the project running on your local machine.

### Prerequisites

* Git
* Java Development Kit (JDK) 17 or later
* Apache Maven
* MySQL Server

### Steps

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/JustaTirkey/Appointment.git](https://github.com/JustaTirkey/Appointment.git)
    cd Appointment
    ```

2.  **Database Setup:**
    * Create a MySQL database named `university_db`.
    * Run the necessary SQL scripts to create the `instructor`, `student`, `professor_calendar`, and `meeting_request` tables.
    * Configure your database credentials in the `application.properties` file located at:
        `src/main/resources/application.properties`

        ```properties
        # Example Configuration
        spring.datasource.url=jdbc:mysql://localhost:3306/university_db
        spring.datasource.username=root
        spring.datasource.password=YourPasswordHere
        spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
        ```

3.  **Build the Project:**
    Use Maven to compile the project and download dependencies.
    ```bash
    mvn clean install
    ```

4.  **Run the Application:**
    You can run the application using the Spring Boot Maven plugin:
    ```bash
    mvn spring-boot:run
    ```
    Alternatively, you can run the main application class (`AppointmentApplication.java`) from your IDE.

5.  **Access the Application:**
    Open your web browser and navigate to the login page:
    [http://localhost:8080/index.html](http://localhost:8080/index.html)

---

##  API Endpoints

The application exposes the following RESTful endpoints:

| Method | Endpoint                                 | Description                                  |
| :----- | :--------------------------------------- | :------------------------------------------- |
| `POST` | `/login`                                 | Authenticates a user and returns their role. |
| `GET`  | `/api/student/{username}`                | Fetches profile data for a specific student. |
| `GET`  | `/api/professor/{username}`              | Fetches profile data for a specific professor. |
| `GET`  | `/api/professor/{profId}/schedule`       | Retrieves the calendar schedule for a professor. |
| `POST` | `/api/professor/{profId}/requestSlot`    | Allows a student to request an appointment slot. |

---

## Usage

1.  Navigate to the login page to start.
2.  Log in with valid student or professor credentials.
3.  **As a Student:** You will be redirected to your profile. You can enter a professor's ID to view their schedule. If a time slot is marked as "free", a "Request" button will appear. Clicking it will book the slot.
4.  **As a Professor:** You will be redirected to your profile, where you can view your schedule.
5.  Use the **Logout** button on either profile page to return to the login screen.
