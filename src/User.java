/**
 * Class: User
 *
 * Purpose:
 * Represents an abstract system user within the application. Used as a base class
 * to manage login credentials and role-based permissions.
 *
 * Responsibilities:
 * - Store login credentials (username, password)
 * - Authenticate a user by verifying their password
 * - Declare abstract methods for determining role-based system permissions
 *
 * OOP Concepts:
 * - Abstraction: The class is abstract and contains abstract methods implemented by subclasses.
 * - Encapsulation: Fields are private and login validation is handled internally.
 */
public abstract class User {
    private String username;
    private String password;

    /**
     * Constructs a new User.
     *
     * @param username The system username (cannot be blank)
     * @param password The login password (cannot be blank)
     * @throws IllegalArgumentException if username or password is invalid
     */
    public User(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        this.username = username.trim();
        this.password = password; // In a simple console application, plain text password comparison is acceptable
    }

    public String getUsername() {
        return username;
    }

    /**
     * Verifies if the provided password matches the user's password.
     *
     * @param inputPassword The password input to check
     * @return true if matches, false otherwise
     */
    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    /**
     * Abstract method to determine if the user has authorization to modify student records.
     *
     * @return true if the user can add, update, or delete records; false if read-only.
     */
    public abstract boolean canModify();

    /**
     * Abstract method to retrieve the display name of the user's role.
     *
     * @return String representing the role name (e.g., "Admin", "Teacher")
     */
    public abstract String getRoleName();
}
