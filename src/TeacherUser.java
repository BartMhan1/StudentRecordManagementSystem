/**
 * Class: TeacherUser
 *
 * Purpose:
 * Represents a teacher account in the student record management system.
 *
 * Responsibilities:
 * - Define credentials and permissions for teacher tasks
 * - Enforce read-only limitations (only allow viewing and searching, deny modification and reports)
 *
 * OOP Concepts:
 * - Inheritance: TeacherUser extends User, inheriting username and password attributes.
 * - Polymorphism: Overrides abstract User methods to return Teacher-specific details, restricting write permission.
 */
public class TeacherUser extends User {

    /**
     * Constructs a new TeacherUser.
     *
     * @param username The teacher's username
     * @param password The teacher's password
     */
    public TeacherUser(String username, String password) {
        super(username, password);
    }

    /**
     * Overrides to return false, restricting the Teacher role to read-only capabilities.
     */
    @Override
    public boolean canModify() {
        return false;
    }

    /**
     * Overrides to return the role identifier "Teacher".
     */
    @Override
    public String getRoleName() {
        return "Teacher";
    }
}
