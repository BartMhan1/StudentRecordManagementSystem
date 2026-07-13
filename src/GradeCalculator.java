/**
 * Class: GradeCalculator
 *
 * Purpose:
 * Provides utility methods to compute student performance statistics (average, highest, 
 * and lowest scores) and classify grades according to configurable threshold boundaries.
 *
 * Responsibilities:
 * - Define grade boundary constants (Distinction, Merit, Pass)
 * - Calculate the mathematical average of a scores array
 * - Identify the highest and lowest scores in a scores array
 * - Determine the grade classification string based on average performance
 *
 * OOP Concepts:
 * - Encapsulation (Utility Context): The grading thresholds are encapsulated as constants 
 *   within this calculator, preventing arbitrary values from being scattered throughout the system.
 */
public class GradeCalculator {

    // Centralized constant definitions for grade classification thresholds
    public static final double DISTINCTION_LIMIT = 70.0;
    public static final double MERIT_LIMIT = 60.0;
    public static final double PASS_LIMIT = 50.0;

    // Subject names ordered by array indices (helps in display formatting if needed)
    public static final String[] SUBJECT_NAMES = {
        "Mathematics", 
        "English", 
        "Science", 
        "History", 
        "Computer Science"
    };

    /**
     * Calculates the arithmetic mean (average) of an array of scores.
     *
     * @param scores The array of student scores
     * @return the average score
     * @throws IllegalArgumentException if the scores array is null or empty
     */
    public static double calculateAverage(double[] scores) {
        validateScoresArray(scores);
        double sum = 0.0;
        for (double score : scores) {
            sum += score;
        }
        return sum / scores.length;
    }

    /**
     * Finds the maximum value (highest score) in an array of scores.
     *
     * @param scores The array of student scores
     * @return the highest score
     * @throws IllegalArgumentException if the scores array is null or empty
     */
    public static double calculateHighest(double[] scores) {
        validateScoresArray(scores);
        double highest = scores[0];
        for (double score : scores) {
            if (score > highest) {
                highest = score;
            }
        }
        return highest;
    }

    /**
     * Finds the minimum value (lowest score) in an array of scores.
     *
     * @param scores The array of student scores
     * @return the lowest score
     * @throws IllegalArgumentException if the scores array is null or empty
     */
    public static double calculateLowest(double[] scores) {
        validateScoresArray(scores);
        double lowest = scores[0];
        for (double score : scores) {
            if (score < lowest) {
                lowest = score;
            }
        }
        return lowest;
    }

    /**
     * Classifies the overall grade based on the student's average score.
     *
     * @param average The student's average score
     * @return Grade classification string ("Distinction", "Merit", "Pass", "Fail")
     */
    public static String classifyGrade(double average) {
        if (average >= DISTINCTION_LIMIT) {
            return "Distinction";
        } else if (average >= MERIT_LIMIT) {
            return "Merit";
        } else if (average >= PASS_LIMIT) {
            return "Pass";
        } else {
            return "Fail";
        }
    }

    /**
     * Validates that the scores array is not null or empty.
     */
    private static void validateScoresArray(double[] scores) {
        if (scores == null || scores.length == 0) {
            throw new IllegalArgumentException("Scores array cannot be null or empty.");
        }
    }
}
