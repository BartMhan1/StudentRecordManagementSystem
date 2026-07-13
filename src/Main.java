import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Class: Main
 *
 * Purpose:
 * Entry point and UI coordinator for the Student Record Management System.
 *
 * Responsibilities:
 * - Initialize StudentRepository database link pointing to data/students.csv
 * - Manage user sessions (Admin and Teacher roles) with authentication limits
 * - Present custom menu dashboards reflecting permissions of the logged-in role
 * - Enforce permission level checks before invoking mutating actions
 * - Collect, validate, and convert console text entries
 * - Handle thrown domain and system exceptions gracefully without crashing
 *
 * Relationships:
 * - User: Maintains a polymorphically-typed currentUser reference (AdminUser or TeacherUser)
 * - StudentRepository: Interacts with repository to fetch, search, filter, update, and remove students.
 *
 * OOP Concepts:
 * - Polymorphism: Exposing role permissions and role names at runtime through 
 *   currentUser.canModify() and currentUser.getRoleName() without casting.
 * - Encapsulation: Keeping inputs and UI loops contained inside utility methods.
 */
public class Main {

    private static final String CSV_PATH = "data/students.csv";
    private static StudentRepository repository;
    private static User currentUser;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("        STUDENT RECORD MANAGEMENT SYSTEM          ");
        System.out.println("==================================================");

        // Initialize repository and load persistent data
        repository = new StudentRepository(CSV_PATH);
        try {
            repository.loadStudents();
            System.out.println("Loaded student records successfully.");
        } catch (Exception e) {
            System.err.println("\nERROR: Student records could not be loaded.");
            System.err.println("Reason: " + e.getMessage());
            System.err.println("The application will close to prevent accidental data loss.");
            scanner.close();
            return;
        }

        // Program lifecycle loop
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                // Show login screen
                boolean loggedIn = runLoginFlow();
                if (!loggedIn) {
                    // Maximum failed login attempts reached, shut down
                    running = false;
                }
            } else {
                // User is authenticated, run role-based menu options
                running = runMenuFlow();
            }
        }

        // Clean up resources on exit
        scanner.close();
        System.out.println("\nThank you for using Student Record Management System. Goodbye!");
    }

    // =========================================================================
    // Authentication & Login System
    // =========================================================================

    /**
     * Executes the login authentication flow. Supports up to 3 failed attempts
     * before safely exiting the program.
     *
     * @return true if login succeeds, false if attempts exhausted
     */
    private static boolean runLoginFlow() {
        System.out.println("\n--- User Login ---");
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine(); // Password check must be case-sensitive

            // Authenticate Admin
            if (username.equals("admin")) {
                AdminUser admin = new AdminUser("admin", "admin123");
                if (admin.authenticate(password)) {
                    currentUser = admin;
                    System.out.println("Welcome, Admin! Login successful.");
                    return true;
                }
            }
            // Authenticate Teacher
            else if (username.equals("teacher")) {
                TeacherUser teacher = new TeacherUser("teacher", "teacher123");
                if (teacher.authenticate(password)) {
                    currentUser = teacher;
                    System.out.println("Welcome, Teacher! Login successful.");
                    return true;
                }
            }

            attempts++;
            int remaining = MAX_ATTEMPTS - attempts;
            System.out.println("Invalid credentials. Remaining attempts: " + remaining);
        }

        System.out.println("\nError: Too many failed login attempts.");
        return false;
    }

    // =========================================================================
    // Menu System Flow
    // =========================================================================

    /**
     * Renders the menu option list based on the active user role, reads the 
     * user selection, and routes to the appropriate handler method.
     *
     * @return true to continue running the program, false to exit
     */
    private static boolean runMenuFlow() {
        System.out.println("\n==============================================");
        System.out.println("Main Menu (" + currentUser.getRoleName() + " Dashboard)");
        System.out.println("==============================================");

        if (currentUser.canModify()) {
            // Display full administrative menu options
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("6. Generate Class Performance Report");
            System.out.println("7. Logout");
            System.out.println("8. Exit");
            System.out.println("==============================================");

            int choice = readIntegerInRange("Select an option (1-8): ", 1, 8);
            switch (choice) {
                case 1:
                    handleAddStudent();
                    break;
                case 2:
                    handleViewStudents();
                    break;
                case 3:
                    handleSearchStudents();
                    break;
                case 4:
                    handleUpdateStudent();
                    break;
                case 5:
                    handleDeleteStudent();
                    break;
                case 6:
                    handleClassReport();
                    break;
                case 7:
                    // Perform Logout
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                    break;
                case 8:
                    // Perform Exit
                    return false;
            }
        } else {
            // Display read-only teacher menu options
            System.out.println("1. View All Students");
            System.out.println("2. Search Student");
            System.out.println("3. Logout");
            System.out.println("4. Exit");
            System.out.println("==============================================");

            int choice = readIntegerInRange("Select an option (1-4): ", 1, 4);
            switch (choice) {
                case 1:
                    handleViewStudents();
                    break;
                case 2:
                    handleSearchStudents();
                    break;
                case 3:
                    // Perform Logout
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                    break;
                case 4:
                    // Perform Exit
                    return false;
            }
        }
        return true;
    }

    // =========================================================================
    // Handler Actions (CRUD & Reports)
    // =========================================================================

    /**
     * Prompts for new student details, applies syntax format validations,
     * and persists the record to the database repository.
     */
    private static void handleAddStudent() {
        if (!currentUser.canModify()) {
            System.out.println("Access Denied: You do not have permissions to perform this operation.");
            return;
        }

        System.out.println("\n--- Add New Student Record ---");

        // Loop until unique ID matching format is provided
        String studentId = "";
        while (true) {
            studentId = readNonEmptyString("Enter Student ID (e.g. 01244946B): ");
            // Student ID format is validated by Student class, but checked here for immediate feedback
            if (!studentId.matches("^\\d{8}[A-Z]$")) {
                System.out.println("Invalid format. Must be 8 digits followed by exactly 1 uppercase letter.");
                continue;
            }
            if (repository.studentIdExists(studentId)) {
                System.out.println("Error: Student ID '" + studentId + "' already exists in the system.");
                continue;
            }
            break;
        }

        String fullName = readNonEmptyString("Enter Full Name: ");
        LocalDate dateOfBirth = readDate("Enter Date of Birth (YYYY-MM-DD): ");
        String gender = readNonEmptyString("Enter Gender: ");
        String course = readNonEmptyString("Enter Course: ");

        // Read all 5 scores
        double[] scores = new double[Student.SUBJECT_COUNT];
        for (int i = 0; i < Student.SUBJECT_COUNT; i++) {
            scores[i] = readScore("Enter score for " + GradeCalculator.SUBJECT_NAMES[i] + " (0-100): ");
        }

        try {
            Student student = new Student(fullName, dateOfBirth, gender, studentId, course, scores);
            repository.addStudent(student);
            System.out.println("Success: Student record added and saved successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Validation Error: Could not add student. Reason: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Database Error: Failed to write to disk: " + e.getMessage());
        }
    }

    /**
     * Displays all loaded student records in a formatted console table,
     * sorted alphabetically by surname.
     */
    private static void handleViewStudents() {
        System.out.println("\n--- Student Directory ---");
        List<Student> students = repository.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }

        // Sort dynamically using surname comparator
        repository.sortStudentsBySurname();
        displayStudentTable(repository.getAllStudents());
    }

    /**
     * Performs a search on Student ID or Full Name using case-insensitive
     * partial matching and displays matches.
     */
    private static void handleSearchStudents() {
        System.out.println("\n--- Search Student Database ---");
        String query = readNonEmptyString("Enter search keyword (ID or Name): ");
        
        List<Student> matches = repository.searchStudents(query);
        if (matches.isEmpty()) {
            System.out.println("No matching student records found for '" + query + "'.");
            return;
        }

        System.out.println("\nMatches found: " + matches.size());
        displayStudentTable(matches);
    }

    /**
     * Allows Administrators to modify any field of a student record through
     * an interactive selection menu, verifying changes before saving.
     */
    private static void handleUpdateStudent() {
        if (!currentUser.canModify()) {
            System.out.println("Access Denied: You do not have permissions to perform this operation.");
            return;
        }

        System.out.println("\n--- Update Student Record ---");
        String originalId = readNonEmptyString("Enter Student ID to update: ");
        Student originalStudent = repository.findStudentById(originalId);

        if (originalStudent == null) {
            System.out.println("Error: Student with ID '" + originalId + "' not found.");
            return;
        }

        System.out.println("\nRecord found:");
        displayStudent(originalStudent);

        // Create temporary clone to edit
        Student tempStudent = new Student(originalStudent);
        boolean editing = true;

        while (editing) {
            System.out.println("\nSelect field to update:");
            System.out.println("1. Student ID (" + tempStudent.getStudentId() + ")");
            System.out.println("2. Full Name (" + tempStudent.getFullName() + ")");
            System.out.println("3. Date of Birth (" + tempStudent.getDateOfBirth() + ")");
            System.out.println("4. Gender (" + tempStudent.getGender() + ")");
            System.out.println("5. Course (" + tempStudent.getCourse() + ")");
            System.out.println("6. Mathematics Score (" + tempStudent.getScores()[0] + ")");
            System.out.println("7. English Score (" + tempStudent.getScores()[1] + ")");
            System.out.println("8. Science Score (" + tempStudent.getScores()[2] + ")");
            System.out.println("9. History Score (" + tempStudent.getScores()[3] + ")");
            System.out.println("10. Computer Science Score (" + tempStudent.getScores()[4] + ")");
            System.out.println("11. Finish Editing");
            System.out.println("12. Cancel");

            int fieldChoice = readIntegerInRange("Select option (1-12): ", 1, 12);
            try {
                switch (fieldChoice) {
                    case 1:
                        String newId = readNonEmptyString("Enter new Student ID (e.g. 01244946B): ");
                        if (!newId.matches("^\\d{8}[A-Z]$")) {
                            System.out.println("Invalid format. Must be 8 digits followed by 1 uppercase letter.");
                            break;
                        }
                        // Check if ID is taken by a different student record
                        if (!newId.equalsIgnoreCase(originalId) && repository.studentIdExists(newId)) {
                            System.out.println("Error: Student ID '" + newId + "' is already in use by another student.");
                            break;
                        }
                        tempStudent.setStudentId(newId);
                        break;
                    case 2:
                        tempStudent.setFullName(readNonEmptyString("Enter new Full Name: "));
                        break;
                    case 3:
                        tempStudent.setDateOfBirth(readDate("Enter new Date of Birth (YYYY-MM-DD): "));
                        break;
                    case 4:
                        tempStudent.setGender(readNonEmptyString("Enter new Gender: "));
                        break;
                    case 5:
                        tempStudent.setCourse(readNonEmptyString("Enter new Course: "));
                        break;
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                        int idx = fieldChoice - 6;
                        double score = readScore("Enter new score for " + GradeCalculator.SUBJECT_NAMES[idx] + " (0-100): ");
                        tempStudent.setScore(idx, score);
                        break;
                    case 11:
                        editing = false;
                        break;
                    case 12:
                        System.out.println("Update cancelled. Changes discarded.");
                        return;
                }
            } catch (Exception e) {
                System.out.println("Error applying update: " + e.getMessage());
            }
        }

        // Display before and after summary
        System.out.println("\n--- Review Updates ---");
        System.out.println("ORIGINAL RECORD:");
        displayStudent(originalStudent);
        System.out.println("\nPROPOSED UPDATED RECORD:");
        displayStudent(tempStudent);

        boolean confirm = readYesOrNo("Save changes? (Y/N): ");
        if (confirm) {
            try {
                repository.updateStudent(originalId, tempStudent);
                System.out.println("Success: Student record updated successfully.");
            } catch (Exception e) {
                System.out.println("Database Error: Failed to save changes: " + e.getMessage());
            }
        } else {
            System.out.println("Changes discarded.");
        }
    }

    /**
     * Prompts for Student ID, shows details, confirms deletion via Admin Password,
     * and deletes the student from memory and disk.
     */
    private static void handleDeleteStudent() {
        if (!currentUser.canModify()) {
            System.out.println("Access Denied: You do not have permissions to perform this operation.");
            return;
        }

        System.out.println("\n--- Delete Student Record ---");
        String studentId = readNonEmptyString("Enter Student ID to delete: ");
        Student student = repository.findStudentById(studentId);

        if (student == null) {
            System.out.println("Error: Student with ID '" + studentId + "' not found.");
            return;
        }

        System.out.println("\nRecord found:");
        displayStudent(student);

        // Security check: Verify Admin password
        System.out.print("Enter Admin Password to confirm: ");
        String enteredPassword = scanner.nextLine();

        if (!currentUser.authenticate(enteredPassword)) {
            System.out.println("Security Error: Incorrect password. Deletion rejected.");
            return;
        }

        // Final confirmation prompt
        boolean confirm = readYesOrNo("Are you sure you want to delete student " + studentId + "? (Y/N): ");
        if (confirm) {
            try {
                boolean deleted = repository.deleteStudent(studentId);
                if (deleted) {
                    System.out.println("Success: Student record removed successfully.");
                } else {
                    System.out.println("Error: Student record was missing at final removal.");
                }
            } catch (Exception e) {
                System.out.println("Database Error: Failed to save deletion: " + e.getMessage());
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /**
     * Generates and prints a detailed class performance report.
     */
    private static void handleClassReport() {
        if (!currentUser.canModify()) {
            System.out.println("Access Denied: You do not have permissions to perform this operation.");
            return;
        }

        System.out.println("\n--- Class Performance Report ---");
        List<Student> students = repository.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No student records found in the system. Cannot generate report.");
            return;
        }

        List<Student> topThree = repository.getTopThreeStudents();
        System.out.println("\nTop Academic Performers (Top 3):");
        System.out.printf("Rank | %-12s | %-25s | %7s | %-12s\n", "Student ID", "Full Name", "Average", "Grade");
        System.out.println("-----+--------------+---------------------------+---------+-------------");
        for (int i = 0; i < topThree.size(); i++) {
            Student s = topThree.get(i);
            double avg = GradeCalculator.calculateAverage(s.getScores());
            String grade = GradeCalculator.classifyGrade(avg);
            System.out.printf("%-4d | %-12s | %-25s | %7.2f | %-12s\n", 
                    (i + 1), s.getStudentId(), s.getFullName(), avg, grade);
        }

        System.out.println("\nSummary Statistics:");
        System.out.println("----------------------------------------------");
        System.out.printf("Total Passed Students: %d\n", repository.getPassCount());
        System.out.printf("Total Failed Students: %d\n", repository.getFailCount());
        System.out.printf("Total Class Size:      %d\n", students.size());
        System.out.println("----------------------------------------------");
    }

    // =========================================================================
    // Format Display Helpers
    // =========================================================================

    /**
     * Formats a single student record as a detailed console card.
     *
     * @param s The Student record to print
     */
    private static void displayStudent(Student s) {
        double[] scores = s.getScores();
        double avg = GradeCalculator.calculateAverage(scores);
        double max = GradeCalculator.calculateHighest(scores);
        double min = GradeCalculator.calculateLowest(scores);
        String grade = GradeCalculator.classifyGrade(avg);

        System.out.println("--------------------------------------------------");
        System.out.printf("Student ID: %s\n", s.getStudentId());
        System.out.printf("Full Name:  %s\n", s.getFullName());
        System.out.printf("DoB:        %s\n", s.getDateOfBirth());
        System.out.printf("Gender:     %s\n", s.getGender());
        System.out.printf("Course:     %s\n", s.getCourse());
        System.out.printf("Scores:     Math: %.1f, English: %.1f, Science: %.1f, History: %.1f, CS: %.1f\n",
                scores[0], scores[1], scores[2], scores[3], scores[4]);
        System.out.printf("Metrics:    Average: %.2f | Highest: %.1f | Lowest: %.1f | Grade: %s\n",
                avg, max, min, grade);
        System.out.println("--------------------------------------------------");
    }

    /**
     * Displays a list of students in a tabular format.
     *
     * @param list The list of students to print
     */
    private static void displayStudentTable(List<Student> list) {
        System.out.printf("\n%-12s | %-25s | %-12s | %-8s | %-20s | %7s | %7s | %7s | %-12s\n", 
                "Student ID", "Full Name", "DoB", "Gender", "Course", "Average", "Highest", "Lowest", "Grade");
        System.out.println("-------------+---------------------------+--------------+----------+----------------------+---------+---------+---------+-------------");
        
        for (Student s : list) {
            double[] scores = s.getScores();
            double avg = GradeCalculator.calculateAverage(scores);
            double max = GradeCalculator.calculateHighest(scores);
            double min = GradeCalculator.calculateLowest(scores);
            String grade = GradeCalculator.classifyGrade(avg);

            System.out.printf("%-12s | %-25s | %-12s | %-8s | %-20s | %7.2f | %7.1f | %7.1f | %-12s\n",
                    s.getStudentId(), s.getFullName(), s.getDateOfBirth(), s.getGender(), 
                    s.getCourse(), avg, max, min, grade);
        }
        System.out.println();
    }

    // =========================================================================
    // User Input & String Validation Helpers
    // =========================================================================

    /**
     * Reads a non-empty string from the console. Re-prompts if empty.
     */
    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String val = scanner.nextLine().trim();
            if (!val.isEmpty()) {
                return val;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    /**
     * Reads an integer within a specific range, handling invalid format exceptions.
     */
    private static int readIntegerInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.println("Value must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid integer.");
            }
        }
    }

    /**
     * Reads a score value, checking it is a valid decimal between 0 and 100.
     */
    private static double readScore(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                double score = Double.parseDouble(input);
                if (score >= 0.0 && score <= 100.0) {
                    return score;
                }
                System.out.println("Score must be between 0.0 and 100.0.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric score. Please try again.");
            }
        }
    }

    /**
     * Reads and parses a date string in YYYY-MM-DD format, checking that it 
     * is not in the future.
     */
    private static LocalDate readDate(String prompt) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                LocalDate date = LocalDate.parse(input, formatter);
                if (date.isAfter(LocalDate.now())) {
                    System.out.println("Date of birth cannot be in the future.");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Please use YYYY-MM-DD format (e.g., 2005-05-15) and enter a valid calendar date.");
            }
        }
    }

    /**
     * Reads confirmation text, returning true on 'Y' and false on 'N'.
     */
    private static boolean readYesOrNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String val = scanner.nextLine().trim();
            if (val.equalsIgnoreCase("Y")) {
                return true;
            }
            if (val.equalsIgnoreCase("N")) {
                return false;
            }
            System.out.println("Please enter 'Y' or 'N' (case-insensitive).");
        }
    }
}
