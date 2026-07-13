import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class: StudentRepository
 *
 * Purpose:
 * Acts as the central data access and persistence layer for the Student Record 
 * Management System. It holds student records in memory and syncs them with a CSV file.
 *
 * Responsibilities:
 * - Load student records from students.csv on startup, checking for duplicates.
 * - Save student records to students.csv after additions, updates, or deletions.
 * - Support rollbacks of in-memory changes if file writing fails.
 * - Add new students, ensuring uniqueness of Student IDs.
 * - Search students by ID or Full Name (returning defensive copies).
 * - Update existing student records and support changing Student IDs.
 * - Delete student records by ID.
 * - Sort students alphabetically by surname.
 * - Calculate class performance statistics (top 3, pass/fail counts).
 *
 * Relationships:
 * - Student: Manages a List of Student instances, instantiates Students from CSV lines, 
 *   and writes Student details back to CSV.
 * - GradeCalculator: Uses GradeCalculator to sort by average score (for top 3) and 
 *   to determine grade classifications (for pass/fail counts).
 *
 * OOP Concepts:
 * - Encapsulation: Restricts access to the internal list of students by returning copy-lists,
 *   returning defensive copies of Student objects so callers cannot mutate the database items,
 *   and handling internal file operations privately.
 */
public class StudentRepository {

    private final String csvPath;
    private final List<Student> students;

    /**
     * Constructs a new StudentRepository with a specified CSV file path.
     *
     * @param csvPath The relative or absolute file path to the CSV database
     * @throws IllegalArgumentException if the csvPath is null or blank
     */
    public StudentRepository(String csvPath) {
        if (csvPath == null || csvPath.trim().isEmpty()) {
            throw new IllegalArgumentException("CSV file path cannot be null or empty.");
        }
        this.csvPath = csvPath.trim();
        this.students = new ArrayList<>();
    }

    /**
     * Checks if a folder and CSV file exist at the configured path, 
     * creating them automatically if they are missing.
     *
     * @throws IOException if folder or file creation fails due to system I/O errors
     */
    private void ensureFileExists() throws IOException {
        File file = new File(csvPath);
        File parent = file.getParentFile();
        
        // Auto-create directories if they don't exist
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                throw new IOException("Failed to create data directory: " + parent.getAbsolutePath());
            }
        }
        
        // Auto-create the CSV file and write header if newly created
        if (file.createNewFile()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("StudentId,FullName,DateOfBirth,Gender,Course,MathScore,EnglishScore,ScienceScore,HistoryScore,CSScore");
            }
        }
    }

    /**
     * Loads student records from the CSV file into the in-memory list.
     * Overwrites any existing records currently loaded in memory.
     * Enforces uniqueness check on Student ID during parsing.
     *
     * @throws IOException if a file reading error occurs
     * @throws IllegalArgumentException if CSV data format is invalid, corrupted, or has duplicate IDs
     */
    public void loadStudents() throws IOException {
        ensureFileExists();
        this.students.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
            String line = reader.readLine(); // Read the header line
            if (line == null) {
                return; // Empty file
            }

            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (line.trim().isEmpty()) {
                    continue; // Skip blank lines
                }

                try {
                    List<String> fields = parseCsvLine(line);
                    // A valid record must contain exactly 10 fields matching the CSV columns
                    if (fields.size() != 10) {
                        throw new IllegalArgumentException("Incorrect number of columns (expected 10, got " + fields.size() + ").");
                    }

                    String studentId = fields.get(0).trim();
                    String fullName = fields.get(1);
                    LocalDate dateOfBirth = LocalDate.parse(fields.get(2));
                    String gender = fields.get(3);
                    String course = fields.get(4);

                    double[] scores = new double[Student.SUBJECT_COUNT];
                    scores[0] = Double.parseDouble(fields.get(5));
                    scores[1] = Double.parseDouble(fields.get(6));
                    scores[2] = Double.parseDouble(fields.get(7));
                    scores[3] = Double.parseDouble(fields.get(8));
                    scores[4] = Double.parseDouble(fields.get(9));

                    // Verify Student ID uniqueness before adding
                    for (Student s : this.students) {
                        if (s.getStudentId().equalsIgnoreCase(studentId)) {
                            throw new IllegalArgumentException("Duplicate Student ID '" + studentId + "' found at CSV line " + lineNum + ".");
                        }
                    }

                    Student student = new Student(fullName, dateOfBirth, gender, studentId, course, scores);
                    this.students.add(student);
                } catch (IllegalArgumentException e) {
                    // Propagate structural validation / duplication errors with line details
                    throw new IllegalArgumentException("Error parsing CSV data at line " + lineNum + ": " + e.getMessage(), e);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Uncaught parsing error at CSV line " + lineNum + ": " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Writes all currently loaded student records to the CSV file.
     *
     * @throws IOException if a file writing error occurs
     */
    public void saveStudents() throws IOException {
        ensureFileExists();
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvPath))) {
            // Write the CSV columns header
            writer.println("StudentId,FullName,DateOfBirth,Gender,Course,MathScore,EnglishScore,ScienceScore,HistoryScore,CSScore");
            
            for (Student s : students) {
                StringBuilder sb = new StringBuilder();
                sb.append(escapeCsvField(s.getStudentId())).append(",");
                sb.append(escapeCsvField(s.getFullName())).append(",");
                sb.append(escapeCsvField(s.getDateOfBirth().toString())).append(",");
                sb.append(escapeCsvField(s.getGender())).append(",");
                sb.append(escapeCsvField(s.getCourse())).append(",");

                double[] scores = s.getScores();
                sb.append(scores[0]).append(",");
                sb.append(scores[1]).append(",");
                sb.append(scores[2]).append(",");
                sb.append(scores[3]).append(",");
                sb.append(scores[4]); // No trailing comma

                writer.println(sb.toString());
            }
        }
    }

    /**
     * Adds a new student record to the repository and saves to CSV.
     * Enforces ID format and checks for duplicate Student IDs (case-insensitive, trimmed).
     * If saving to the CSV database fails, changes are rolled back to keep data in sync.
     *
     * @param student The student record to add (original gets defensively copied)
     * @throws IllegalArgumentException if the student is null, or if ID is a duplicate
     * @throws IOException if an error occurs while writing updates to disk
     */
    public void addStudent(Student student) throws IOException {
        if (student == null) {
            throw new IllegalArgumentException("Student record cannot be null.");
        }
        String cleanId = student.getStudentId().trim();
        
        // Ensure ID is unique
        for (Student s : students) {
            if (s.getStudentId().equalsIgnoreCase(cleanId)) {
                throw new IllegalArgumentException("Duplicate Student ID not accepted: " + cleanId);
            }
        }

        // Backup current in-memory list structure for rollback
        List<Student> backup = new ArrayList<>(this.students);
        
        // Add a defensive copy to prevent external mutation of repository items
        this.students.add(new Student(student));
        
        try {
            saveStudents();
        } catch (IOException e) {
            // Roll back to previous state if file write fails
            this.students.clear();
            this.students.addAll(backup);
            throw e;
        }
    }

    /**
     * Retrieves all student records currently in memory.
     * Returns a copy list containing defensive copies of each Student instance
     * to protect database integrity from external caller mutations.
     *
     * @return a List containing copies of all Student records
     */
    public List<Student> getAllStudents() {
        List<Student> copies = new ArrayList<>();
        for (Student s : this.students) {
            copies.add(new Student(s));
        }
        return copies;
    }

    /**
     * Finds a single student record matching the specified Student ID exactly.
     * Returns a defensive copy of the student record to protect integrity.
     *
     * @param studentId The Student ID to search for
     * @return a copy of the Student record if found; null otherwise
     */
    public Student findStudentById(String studentId) {
        if (studentId == null) {
            return null;
        }
        String cleanId = studentId.trim();
        for (Student s : students) {
            if (s.getStudentId().equalsIgnoreCase(cleanId)) {
                return new Student(s); // Return defensive copy
            }
        }
        return null;
    }

    /**
     * Searches for students by matching ID or Full Name case-insensitively.
     * Supports partial matches. Returns a list containing defensive copies.
     *
     * @param query The search query string
     * @return a List of Student copies matching the query criteria
     */
    public List<Student> searchStudents(String query) {
        List<Student> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }
        String lowerQuery = query.trim().toLowerCase();
        for (Student s : students) {
            if (s.getStudentId().toLowerCase().contains(lowerQuery) || 
                s.getFullName().toLowerCase().contains(lowerQuery)) {
                results.add(new Student(s)); // Return defensive copy
            }
        }
        return results;
    }

    /**
     * Replaces an existing student record with the updated version, supporting ID changes.
     * If the ID has changed, verifies the new ID is unique and doesn't belong to another student.
     * If saving to the CSV database fails, changes are rolled back to keep data in sync.
     *
     * @param originalStudentId The ID of the student to modify
     * @param updatedStudent The updated student record to store
     * @throws IllegalArgumentException if the original student is not found, or the new ID is already in use
     * @throws IOException if saving to CSV fails
     */
    public void updateStudent(String originalStudentId, Student updatedStudent) throws IOException {
        if (originalStudentId == null || originalStudentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Original Student ID cannot be null or empty.");
        }
        if (updatedStudent == null) {
            throw new IllegalArgumentException("Updated student cannot be null.");
        }
        
        String cleanOriginalId = originalStudentId.trim();
        String newId = updatedStudent.getStudentId().trim();
        
        // Locate index of target student record
        int targetIndex = -1;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equalsIgnoreCase(cleanOriginalId)) {
                targetIndex = i;
                break;
            }
        }
        
        if (targetIndex == -1) {
            throw new IllegalArgumentException("Cannot update. Student with ID " + cleanOriginalId + " does not exist.");
        }
        
        // If Student ID is changing, ensure the new ID is not used by another student
        if (!cleanOriginalId.equalsIgnoreCase(newId)) {
            for (int i = 0; i < students.size(); i++) {
                if (i != targetIndex && students.get(i).getStudentId().equalsIgnoreCase(newId)) {
                    throw new IllegalArgumentException("Cannot update. Student ID '" + newId + "' is already in use by another student.");
                }
            }
        }
        
        // Backup list structure for rollback safety
        List<Student> backup = new ArrayList<>(this.students);
        
        // Update database with a defensive copy
        students.set(targetIndex, new Student(updatedStudent));
        
        try {
            saveStudents();
        } catch (IOException e) {
            // Roll back to previous state if file write fails
            this.students.clear();
            this.students.addAll(backup);
            throw e;
        }
    }

    /**
     * Deletes a student record matching the specified ID.
     * If saving to the CSV database fails, changes are rolled back.
     *
     * @param studentId The Student ID of the record to delete
     * @return true if the student was successfully found and deleted; false otherwise
     * @throws IOException if saving updates to CSV fails
     */
    public boolean deleteStudent(String studentId) throws IOException {
        if (studentId == null) {
            return false;
        }
        String cleanId = studentId.trim();
        int targetIndex = -1;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equalsIgnoreCase(cleanId)) {
                targetIndex = i;
                break;
            }
        }
        
        if (targetIndex == -1) {
            return false;
        }
        
        // Backup current in-memory list structure for rollback
        List<Student> backup = new ArrayList<>(this.students);
        
        this.students.remove(targetIndex);
        
        try {
            saveStudents();
        } catch (IOException e) {
            // Roll back to previous state if file write fails
            this.students.clear();
            this.students.addAll(backup);
            throw e;
        }
        
        return true;
    }

    /**
     * Checks whether a Student ID exists in the repository.
     *
     * @param studentId The Student ID to verify
     * @return true if the ID exists; false otherwise
     */
    public boolean studentIdExists(String studentId) {
        if (studentId == null) {
            return false;
        }
        String cleanId = studentId.trim();
        for (Student s : students) {
            if (s.getStudentId().equalsIgnoreCase(cleanId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sorts the loaded student records list alphabetically by surname (case-insensitive).
     * The surname is defined as the last whitespace-separated segment of the student's full name.
     */
    public void sortStudentsBySurname() {
        students.sort((s1, s2) -> {
            String surname1 = extractSurname(s1.getFullName());
            String surname2 = extractSurname(s2.getFullName());
            return surname1.compareToIgnoreCase(surname2);
        });
    }

    /**
     * Computes student performance and returns the top 3 highest-performing students 
     * based on their average score in descending order. Returns defensive copies.
     *
     * @return a List containing up to 3 highest-scoring Student copies
     */
    public List<Student> getTopThreeStudents() {
        List<Student> copy = new ArrayList<>(this.students);
        copy.sort((s1, s2) -> {
            double avg1 = GradeCalculator.calculateAverage(s1.getScores());
            double avg2 = GradeCalculator.calculateAverage(s2.getScores());
            return Double.compare(avg2, avg1); // Sort descending
        });
        
        List<Student> results = new ArrayList<>();
        int limit = Math.min(3, copy.size());
        for (int i = 0; i < limit; i++) {
            results.add(new Student(copy.get(i))); // Return defensive copy
        }
        return results;
    }

    /**
     * Counts the number of students who have passed.
     * A pass is defined as a grade classification of Pass, Merit, or Distinction.
     *
     * @return the total number of passing students
     */
    public int getPassCount() {
        int count = 0;
        for (Student s : students) {
            double avg = GradeCalculator.calculateAverage(s.getScores());
            String grade = GradeCalculator.classifyGrade(avg);
            if (grade.equals("Pass") || grade.equals("Merit") || grade.equals("Distinction")) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of students who have failed.
     * A fail is defined as a grade classification of Fail.
     *
     * @return the total number of failing students
     */
    public int getFailCount() {
        int count = 0;
        for (Student s : students) {
            double avg = GradeCalculator.calculateAverage(s.getScores());
            String grade = GradeCalculator.classifyGrade(avg);
            if (grade.equals("Fail")) {
                count++;
            }
        }
        return count;
    }

    /**
     * Extracts the surname from a full name string.
     * Surname is extracted as the last word of a whitespace-split string.
     */
    private String extractSurname(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts[parts.length - 1];
    }

    /**
     * Escapes standard CSV fields containing delimiters (commas or quotes).
     * Doubles inner quotation marks and wraps the final field inside double quotes.
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(",") || field.contains("\"")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    /**
     * Parses a raw CSV line supporting quoted delimiters and escaped quotes.
     * Implements a state-machine reader checking if the cursor lies inside quotes.
     *
     * @param line The raw CSV record line
     * @return List of parsed fields
     * @throws IllegalArgumentException if the line has an unmatched quotation mark
     */
    private List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Two double quotes "" inside quotes parsed as a single literal quote
                    currentField.append('"');
                    i++; // Skip the next quote character
                } else {
                    // Toggle inside/outside quotes mode
                    inQuotes = !inQuotes;
                }
            } else if (c == ',') {
                if (inQuotes) {
                    currentField.append(c);
                } else {
                    fields.add(currentField.toString());
                    currentField.setLength(0); // Clear builder for next field
                }
            } else {
                currentField.append(c);
            }
        }
        
        // Enforce quote matching checks
        if (inQuotes) {
            throw new IllegalArgumentException("CSV field contains an unmatched quotation mark.");
        }
        
        fields.add(currentField.toString());
        return fields;
    }
}
