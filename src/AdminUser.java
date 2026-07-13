/**
 * Class: AdminUser
 *
 * Purpose:
 * Represents an administrator account in the student record management system.
 *
 * Responsibilities:
 * - Define credentials and permissions for administrative tasks
 * - Grant access to full write capabilities (add, update, delete records, generate reports)
 *
 * OOP Concepts:
 * - Inheritance: AdminUser extends User, inheriting username and password attributes.
 * - Polymorphism: Overrides the abstract User methods to return Admin-specific details and full write access.
 */
public class AdminUser extends User {

    /**
     * Constructs a new AdminUser.
     *
     * @param username The administrator's username
     * @param password The administrator's password
     */
    public AdminUser(String username, String password) {
        super(username, password);
    }

    /**
     * Overrides to return true, granting the Admin role full modify permissions.
     */
    @Override
    public boolean canModify() {
        return true;
    }

    /**
     * Overrides to return the role identifier "Admin".
     */
    @Override
    public String getRoleName() {
        return "Admin";
    }
}
