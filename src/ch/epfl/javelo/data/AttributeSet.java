package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;


/**
 * Represents an attributeSet of OSM points
 *
 * @param bits contain the OSM attributes
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */

public record AttributeSet(long bits) {

    /**
     * Checks if the bits given to AttributeSet contains a 1 bit that doesn't correspond to any valid attributes,
     * launches IllegalArgumentException if there is
     *
     * @param bits the bits representing the Set
     */

    public AttributeSet {

        long unValidBits = bits >> Attribute.COUNT;

        Preconditions.checkArgument(unValidBits == 0);
    }

    /**
     * @param attributes are the OSM attributes
     * @return the AttributeSet of the attributes given
     */

    public static AttributeSet of(Attribute... attributes) {

        long bits = 0L;

        for (Attribute a : attributes) {
            bits |= 1L << a.ordinal();
        }

        return new AttributeSet(bits);
    }

    /**
     * @param attribute are the OSM attributes
     * @return true if the AttributeSet contains the given attribute
     */
    public boolean contains(Attribute attribute) {

        long l = 1L;
        l = l << attribute.ordinal();
        return ((bits & l) != 0);

    }

    /**
     * @param that : another AttributeSet
     * @return if there are attributes in common between the sets
     */
    public boolean intersects(AttributeSet that) {
        return ((that.bits & this.bits) != 0);
    }

    /**
     * @return the list of every attributes in the AttributeSet
     */
    @Override
    public String toString() {

        StringJoiner sj = new StringJoiner(",", "{", "}");

        for (Attribute a : Attribute.ALL) {
            if (this.contains(a)) {
                sj.add(a.toString());
            }
        }
        return sj.toString();
    }

}



