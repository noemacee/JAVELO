package ch.epfl.javelo;
// ATTENTION


/**
 * Extract bits with 2 separate methods from a 32 bits vector
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */

public final class Bits {


    private Bits() {
    }

    /**
     * @param value  the 32 bits vector
     * @param start  the index of the bit we start at
     * @param length the length we want to check
     * @return extracts the value in a signed way with the parameters given or throws an
     * IllegalArgumentException if the length is not compatible with the start
     */
    public static int extractSigned(int value, int start, int length) {
        Preconditions.checkArgument(start + length <= Integer.SIZE
                && start >= 0
                && start <= Integer.SIZE
                && length >= 0
                && length <= Integer.SIZE);
        value = value << Integer.SIZE - length - start;
        value = value >> Integer.SIZE - length;

        return value;

    }

    /**
     * @param value  the 32 bits vector
     * @param start  the index of the bit we start at
     * @param length the length we want to check
     * @return the same method as the precedent method but in unsigned this time
     */
    public static int extractUnsigned(int value, int start, int length) {
        Preconditions.checkArgument(start + length <= Integer.SIZE
                && start >= 0
                && start <= Integer.SIZE
                && length >= 0
                && length < Integer.SIZE);
        value = value << Integer.SIZE - length - start;
        value = value >>> Integer.SIZE - length;

        return value;
    }
}

