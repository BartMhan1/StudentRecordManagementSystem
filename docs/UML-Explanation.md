# 📊 UML Class Diagram Explanation

## 📌 Introduction

This document explains the UML (Unified Modeling Language) Class Diagram for the **Student Record Management System**.

The UML Class Diagram provides a visual representation of the software's structure. It shows the classes in the system, the important attributes and methods of each class, and the relationships between the classes.

Unlike a flowchart, which shows the sequence of execution, a UML Class Diagram focuses on how the system is designed.

---

# ⚖️ Difference Between a UML Class Diagram and a Flowchart

| 📊 UML Class Diagram | 📝 Flowchart |
| :--- | :--- |
| Shows the structure of the program | Shows the execution flow of the program |
| Focuses on classes and relationships | Focuses on program logic and decision making |
| Used during software design | Used to describe algorithms or processes |

---

# 🌐 Overview of the System

The Student Record Management System consists of **eight main classes**.

They work together to provide:
*   🔑 User authentication
*   📂 Student record management
*   🧮 Performance calculations
*   💾 File storage
*   🔒 Role-based access control

Each class has a specific responsibility, making the program easier to understand, maintain, and extend.

---

# 🧩 Class Descriptions

## 1. Person (Abstract Class)
`Person` is the parent class that stores information common to every person in the system.

### Attributes
*   `fullName`
*   `dateOfBirth`
*   `gender`

### Purpose
Instead of storing these fields repeatedly inside different classes, they are placed in one parent class.

This demonstrates **Abstraction** because `Person` represents the general characteristics of a person without creating objects directly.

---

## 2. Student
`Student` inherits from `Person`.

In addition to the inherited personal information, it stores academic information.

### Attributes
*   `studentId`
*   `course`
*   `scores` (Subject Scores)

### Purpose
The Student class represents an individual student in the system.

It validates:
*   Student ID
*   Scores
*   Course information

This class demonstrates:
*   Inheritance
*   Encapsulation

---

## 3. User (Abstract Class)
`User` is the parent class for every system user.

### Attributes
*   `username`
*   `password`

### Purpose
Rather than creating separate login logic for every user type, the common authentication functionality is stored here.

This class is abstract because the system never creates a generic User. Instead, it creates specific user types.

---

## 4. AdminUser
`AdminUser` extends `User`.

### Responsibilities
An administrator can:
*   Add students
*   Update students
*   Delete students
*   View reports
*   Search students
*   View all students

The class overrides methods such as:
*   `canModify()`
*   `getRoleName()`

This demonstrates **Polymorphism**.

---

## 5. TeacherUser
`TeacherUser` also extends `User`.

### Responsibilities
A teacher can:
*   View students
*   Search students

A teacher cannot:
*   Add students
*   Update students
*   Delete students
*   Generate reports

Like `AdminUser`, this class overrides the inherited permission methods.

---

## 6. StudentRepository
This is the core business class of the application. It manages all student records.

### Responsibilities
*   Add students
*   Search students
*   Update students
*   Delete students
*   Sort students
*   Load CSV files
*   Save CSV files
*   Generate statistics

Instead of placing all this logic inside `Main`, it is grouped into one dedicated class. This makes the program easier to maintain.

---

## 7. GradeCalculator
`GradeCalculator` is a utility class. It performs calculations but does not store student information.

### Responsibilities
*   Calculate average score
*   Calculate highest score
*   Calculate lowest score
*   Determine grade classification

Keeping calculations in a separate class follows the principle of separating responsibilities.

---

## 8. Main
`Main` is the entry point of the application. It controls the overall execution of the system.

### Responsibilities
*   Display menus
*   Handle user input
*   Validate input
*   Authenticate users
*   Call StudentRepository methods
*   Display results

Notice that `Main` does **not** store student records. It simply coordinates communication between the user and the other classes.

---

# 🔗 Relationships Between Classes

## 🌿 Inheritance
The system uses inheritance to reduce code duplication.

```text
  Person
     ▲
  Student
```

A Student **is a** Person. Therefore, Student automatically inherits:
*   Full Name
*   Date of Birth
*   Gender

Similarly,

```text
    User
   ▲    ▲
   │    │
 Admin Teacher
```

Both AdminUser and TeacherUser inherit common login functionality from User.

---

## 📦 Aggregation

```text
StudentRepository
        │
        │ manages
        ▼
     Student
```

StudentRepository maintains a collection of Student objects. The repository is responsible for managing these records throughout the application's lifetime.

---

## 🔌 Dependency

```text
  Main
   │
   ├── StudentRepository
   └── User
```

The Main class depends on StudentRepository to perform student operations and uses User to manage the current login session.

Similarly,

```text
StudentRepository
        │
        ▼
GradeCalculator
```

StudentRepository depends on GradeCalculator whenever student statistics are required.

---

# 🧱 Object-Oriented Programming Concepts

## 🔒 Encapsulation
Encapsulation protects data by making class fields private. Data is accessed through getters and setters, where validation is performed before changes are accepted.

*   **Examples**:
    *   `Person`
    *   `Student`

---

## 🧩 Abstraction
Abstraction hides unnecessary implementation details while exposing only the important behaviour.

*   **Examples**:
    *   `Person`
    *   `User`

*Note: Both classes are abstract and cannot be instantiated directly.*

---

## 🌿 Inheritance
Inheritance allows child classes to reuse attributes and methods from parent classes.

*   **Examples**:
    *   `Student` extends `Person`
    *   `AdminUser` extends `User`
    *   `TeacherUser` extends `User`

---

## 🎭 Polymorphism
Polymorphism allows different classes to respond differently to the same method call.

*   **Examples**:
    *   `canModify()`
    *   `getRoleName()`

*Note: Although both `AdminUser` and `TeacherUser` inherit these methods from `User`, each class provides its own implementation.*

---

## 🏁 Conclusion

The UML Class Diagram illustrates the overall architecture of the Student Record Management System.

It demonstrates how the application applies Object-Oriented Programming principles to produce a modular, maintainable, and well-organized solution.

Understanding the relationships shown in the diagram makes it easier to understand how the classes collaborate to perform the required tasks within the system.
