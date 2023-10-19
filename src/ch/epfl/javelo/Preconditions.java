package ch.epfl.javelo;


/**
 * Checks if the arguments of the method we are calling are valid
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */
public final class Preconditions {

    /**
     * empty contructor
     */
    private Preconditions() {
    }

    /**
     * checks if an assessment is true
     *
     * @param shouldBeTrue the boolean needing to be checked
     * @throws IllegalArgumentException when the assessment is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue)
            throw new IllegalArgumentException();
    }
}
