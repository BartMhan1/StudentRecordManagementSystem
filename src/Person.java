import java.time.LocalDate;

/**
 * Class: Person
 *
 * Purpose:
 * Represents an abstract person containing personal details such as full name, 
 * date of birth, and gender. It is used as a base class for other human entities.
 *
 * Responsibilities:
 * - Store personal identification attributes (fullName, dateOfBirth, gender)
 * - Provide constructors to initialize personal data
 * - Validate personal data values during instantiation and modification
 *
 * OOP Concepts:
 * - Abstraction: The class is abstract and cannot be directly instantiated.
 * - Encapsulation: Fields are private and accessed/validated via getters and setters.
 */
public abstract class Person {
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;

    /**
     * Constructs a new Person with validated details.
     *
     * @param fullName The full name of the person (cannot be blank)
     * @param dateOfBirth The date of birth (cannot be null or in the future)
     * @param gender The gender description (cannot be blank)
     * @throws IllegalArgumentException if any validation checks fail
     */
    public Person(String fullName, LocalDate dateOfBirth, String gender) {
        setFullName(fullName);
        setDateOfBirth(dateOfBirth);
        setGender(gender);
    }

    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name after validation.
     *
     * @param fullName The full name to set (cannot be null or empty)
     * @throws IllegalArgumentException if the name is null or blank
     */
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty.");
        }
        this.fullName = fullName.trim();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the date of birth after validation.
     *
     * @param dateOfBirth The date of birth to set (cannot be null or in the future)
     * @throws IllegalArgumentException if the date of birth is null or in the future
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null.");
        }
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future.");
        }
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender after validation.
     *
     * @param gender The gender to set (cannot be null or empty)
     * @throws IllegalArgumentException if the gender is null or blank
     */
    public void setGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be null or empty.");
        }
        this.gender = gender.trim();
    }
}
