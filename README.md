# 🎓 Student Record Management System

Our **Student Record Management System** is a Java 11 console app we built for our group project to help a school manage their student records. Instead of keeping everything on paper, this app lets admins and teachers log in to add, search, update, delete, and look at student information using a simple menu.

We made this project for our Java programming assignment to practice **Object-Oriented Programming (OOP) principles**, making sure the data entered is valid, and learning how to read and write from files.

---

## 🎯 Project Objectives

The main objectives of this project are to:
*   📂 **Store** student records digitally instead of manually.
*   🚫 **Prevent** duplicate student records.
*   🔍 **Allow** quick searching and retrieval of student information.
*   📊 **Generate** student performance statistics.
*   🔒 **Restrict** system operations using user roles.
*   💡 **Demonstrate** the practical application of Object-Oriented Programming concepts.

---

## ✨ Features

The system provides the following features:

### 🔑 User Login System
*   **Administrator** (Full Access)
*   **Teacher** (View & Search Only)

### 📂 Student Management
*   📥 Add new students
*   📋 View all students
*   🔍 Search students by Student ID or Full Name
*   🔄 Update student information
*   ❌ Delete student records

### 📈 Student Performance
*   🧮 Calculate average score
*   🥇 Display highest score
*   🥉 Display lowest score
*   🏷️ Grade classification
*   📊 Generate class performance report
*   🏆 Display top three performing students
*   🔢 Count students who passed or failed

### 💾 File Persistence
*   🗃️ Automatically saves records to a CSV file
*   🔄 Automatically reloads records when the application starts

### 🛡️ Input Validation
*   🆔 Student ID validation
*   🚫 Duplicate ID prevention
*   📅 Date validation
*   💯 Score validation
*   ❓ Confirmation prompts before updates and deletion
*   ⚠️ Exception handling to prevent program crashes

---

## 💻 Technologies Used

*   **Language**: `Java 11`
*   **Methodology**: `Object-Oriented Programming (OOP)`
*   **Data Structures**: `Java Collections Framework`
*   **Storage**: `Java File I/O` & `CSV File Storage`
*   **IDE**: `IntelliJ IDEA`

---

## 📁 Project Structure

```text
StudentRecordManagementSystem/
│
├── src/
│   ├── Main.java
│   ├── Person.java
│   ├── Student.java
│   ├── User.java
│   ├── AdminUser.java
│   ├── TeacherUser.java
│   ├── GradeCalculator.java
│   └── StudentRepository.java
│
├── data/
│   └── students.csv
│
├── docs/
│   ├── uml-class-diagram.puml
│   └── UML-Explanation.md
│
├── run.bat
├── README.md
└── .gitignore
```

---

## 🧱 Object-Oriented Programming Concepts Used

This project demonstrates the four fundamental principles of Object-Oriented Programming.

### 🔒 Encapsulation
Sensitive data is stored in `private` variables and accessed through `public` getters and setters. Validation logic is also performed inside setters and constructors to ensure data integrity.

*   **Examples**:
    *   `Student.java`
    *   `Person.java`

---

### 🧩 Abstraction
Common characteristics are defined using `abstract` classes.

*   **Examples**:
    *   `Person`
    *   `User`

*Note: These classes cannot be instantiated directly but provide a blueprint for their subclasses.*

---

### 🌿 Inheritance
Inheritance reduces code duplication by allowing subclasses to inherit attributes and methods from parent classes.

*   **Examples**:
    *   `Student` extends `Person`
    *   `AdminUser` extends `User`
    *   `TeacherUser` extends `User`

---

### 🎭 Polymorphism
Different subclasses provide different implementations of the same methods.

*   **Examples**:
    *   `canModify()`
    *   `getRoleName()`

*Note: Both `AdminUser` and `TeacherUser` override these methods to implement different permission levels.*

---

## 👥 User Roles

### 👑 Administrator
The administrator has full access to the system.

*   **Allowed operations**:
    *   Add Student
    *   View Students
    *   Search Students
    *   Update Student
    *   Delete Student
    *   Generate Reports
    *   Logout
    *   Exit

---

### 📝 Teacher
The teacher has limited access.

*   **Allowed operations**:
    *   View Students
    *   Search Students
    *   Logout
    *   Exit

*Note: Teachers cannot modify student records or generate reports.*

---

## 🆔 Student ID Format

Each student must have a unique Student ID using the following format:
```text
01244946B
```

**Rules**:
*   Exactly **8 digits**
*   Followed by **one uppercase letter**
*   Duplicate IDs are **not allowed**

---

## 💾 Data Storage

Student records are stored in:
```text
data/students.csv
```

The system automatically:
*   🔄 **Loads** existing records during startup.
*   💾 **Saves** changes after successful Add, Update, or Delete operations.

---

## 📚 UML Documentation

The UML Class Diagram and its explanation are available in the `docs` folder.

*   **Files**:
    *   `uml-class-diagram.puml`
    *   `UML-Explanation.md`

*Note: These documents describe the structure of the system and the relationships between classes.*

---

## 🚀 How to Run the Project

### 📋 Requirements
*   Java 11 or later
*   Windows Operating System

### 🛠️ Steps
1.  Open the project folder.
2.  Double-click `run.bat`.

Alternatively, compile and run manually:
```bash
javac --release 11 -d bin src\*.java
java -cp bin Main
```

---

## 🔮 Future Improvements

Possible future enhancements include:
*   🖥️ Graphical User Interface (GUI)
*   🗄️ Database integration (MySQL or PostgreSQL)
*   🔐 Password encryption
*   👥 Multiple administrator accounts
*   👨‍🎓 Student portal
*   📝 Teacher grade submission
*   📄 Export reports to PDF

---

## 👥 Contributors

This project was developed as a group assignment. Contributors include:

*   **Lordina Oforiwaa Amoako**
*   **Eleazer Kofi Enam Tamakloe**
*   **Antwi Festus**
*   **Dey Richmond**
*   **Sowah Nicholas Okpoti Junior**
*   **Prince Ayaata Awenanyame Kwame**
*   **Eklu Fabris**
*   **Adosibe Kennedy Awine**
*   **Kelvin Osei**
*   **Narter-Tawiah Isaac Yohanes**
*   **Mensah Emmanuel Ankomah**
*   **Zakaria Abdul Ganiyu**
*   **Dominic Gyimah**
*   **Osam Theodora**
*   **Darko-Ameyaw Joel**
*   **Arthur George Nana Siw**
*   **Success Annan**
*   **Darkwah Ohene Kofi Michael**
*   **Ahiave Bismark**

This project was developed for our Java group assignment.

---

## 📄 Disclaimer

This project was developed for educational purposes only for our university assignment.
