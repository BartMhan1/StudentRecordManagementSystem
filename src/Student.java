import java.time.LocalDate;

/**
 * Class: Student
 *
 * Purpose:
 * Represents a student record containing personal details, course details, 
 * and scores for five fixed subjects.
 *
 * Responsibilities:
 * - Store student identifier, course, and subject scores
 * - Validate that the student ID conforms to the required format (8 digits + 1 uppercase letter)
 * - Validate that each subject score is within the 0 to 100 range
 * - Protect the internal scores array from unauthorized outside modification
 * - Support deep copying of student instances for safe update confirmations
 *
 * OOP Concepts:
 * - Inheritance: Student extends Person, inheriting name, date of birth, and gender.
 * - Encapsulation: Fields are private and accessed/validated via methods, returning copies of arrays to prevent corruption.
 */
public class Student extends Person {
    
    // Regular expression for validating the Student ID format: 8 digits followed by 1 uppercase letter
    private static final String ID_REGEX = "^\\d{8}[A-Z]$";
    
    // The number of fixed subjects required for every student
    public static final int SUBJECT_COUNT = 5;
    
    private String studentId;
    private String course;
    private double[] scores; // Index mapping: 0=Math, 1=English, 2=Science, 3=History, 4=Computer Science

    /**
     * Constructs a new Student with full validated parameters.
     *
     * @param fullName The student's full name
     * @param dateOfBirth The student's date of birth
     * @param gender The student's gender
     * @param studentId The unique student ID (exactly 8 digits + 1 uppercase letter)
     * @param course The course description
     * @param scores An array containing exactly five subject scores
     * @throws IllegalArgumentException if any validation checks fail
     */
    public Student(String fullName, LocalDate dateOfBirth, String gender, 
                   String studentId, String course, double[] scores) {
        super(fullName, dateOfBirth, gender);
        setStudentId(studentId);
        setCourse(course);
        setScores(scores);
    }

    /**
     * Copy Constructor (Deep Copy)
     * Creates a new Student instance by copying all fields from an existing Student.
     * Used for the safe-update confirmation flow.
     *
     * @param other The student instance to copy
     * @throws IllegalArgumentException if the other student is null
     */
    public Student(Student other) {
        super(other.getFullName(), other.getDateOfBirth(), other.getGender());
        this.studentId = other.studentId;
        this.course = other.course;
        // Deep copy the scores array to ensure independent state
        this.scores = other.scores.clone();
    }

    public String getStudentId() {
        return studentId;
    }

    /**
     * Sets the student ID after validating its format.
     *
     * @param studentId The student ID to set (cannot be null, must match pattern)
     * @throws IllegalArgumentException if the format is invalid
     */
    public void setStudentId(String studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null.");
        }
        String trimmedId = studentId.trim();
        if (!trimmedId.matches(ID_REGEX)) {
            throw new IllegalArgumentException("Student ID must be exactly 8 digits followed by one uppercase letter (e.g., 01244946B).");
        }
        this.studentId = trimmedId;
    }

    public String getCourse() {
        return course;
    }

    /**
     * Sets the course after validation.
     *
     * @param course The course to set
     * @throws IllegalArgumentException if course name is empty
     */
    public void setCourse(String course) {
        if (course == null || course.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be null or empty.");
        }
        this.course = course.trim();
    }

    /**
     * Returns a copy of the scores array to preserve encapsulation.
     * Outside code cannot modify this array directly.
     *
     * @return a clone of the internal scores array
     */
    public double[] getScores() {
        return scores.clone();
    }

    /**
     * Validates and sets all five scores.
     *
     * @param scores An array of exactly five scores
     * @throws IllegalArgumentException if array length is not 5 or any score is outside [0, 100]
     */
    public void setScores(double[] scores) {
        if (scores == null) {
            throw new IllegalArgumentException("Scores array cannot be null.");
        }
        if (scores.length != SUBJECT_COUNT) {
            throw new IllegalArgumentException("Scores array must contain exactly " + SUBJECT_COUNT + " elements.");
        }
        // Validate all scores before applying any changes
        for (double score : scores) {
            validateScoreValue(score);
        }
        // Clone the input array to preserve encapsulation
        this.scores = scores.clone();
    }

    /**
     * Sets a single subject score by index.
     *
     * @param index The index of the subject (0 to 4)
     * @param score The score to set (0 to 100)
     * @throws IndexOutOfBoundsException if index is invalid
     * @throws IllegalArgumentException if score is out of range
     */
    public void setScore(int index, double score) {
        if (index < 0 || index >= SUBJECT_COUNT) {
            throw new IndexOutOfBoundsException("Subject index must be between 0 and " + (SUBJECT_COUNT - 1) + ".");
        }
        validateScoreValue(score);
        this.scores[index] = score;
    }

    /**
     * Helper to validate that a score is between 0 and 100.
     */
    private void validateScoreValue(double score) {
        if (score < 0.0 || score > 100.0) {
            throw new IllegalArgumentException("Score must be between 0.0 and 100.0 (received: " + score + ").");
        }
    }
}
