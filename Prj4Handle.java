/**
 * A Handle is used by the Memory Manager to identify a chunk of memory
 * uniquely. In Project4, we use two ints to specify the starting position of
 * the chunk (in bytes) and the length of the chunk (in bytes). These two ints
 * cannot be changed after the Handle is created. You will need to ask the
 * Memory Manager for a new Handle if you want more (or less) memory.
 * 
 * @author Taimoor Qamar
 * @version 11-16-19
 *
 */
public class Prj4Handle {
    private final int position;
    private final int length;


    /**
     * Makes a new Prj4Handle with the given starting position and length (both
     * in bytes)
     * 
     * @param pos
     *            the starting position (in bytes) of the corresponding chunk of
     *            memory allocated by the Memory Manager. This should not be
     *            negative.
     * @param leng
     *            the number of bytes that the corresponding chunk of memory
     *            uses in the file. This should not be negative.
     */
    public Prj4Handle(int pos, int leng) {
        position = pos;
        length = leng;
    }


    /**
     * Getter for the starting position (in bytes)
     * 
     * @return the starting position (in bytes)
     */
    public int getPosition() {
        return position;
    }


    /**
     * Getter for the length (in bytes)
     * 
     * @return the length (in bytes)
     */
    public int getLength() {
        return length;
    }


    /**
     * Will have to change this once the output format is posted
     * 
     * @return position and length
     */
    public String toString() {
        return " starts from Byte " + position + " with length " + length;
    }


    /**
     * Two Prj4Handles are equal if and only if they have the same position and
     * length.
     * 
     * @param other
     *            Object that is being compared to this for equality
     * @return whether this is equal to other
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (this.getClass() == other.getClass()) {
            Prj4Handle newOther = (Prj4Handle)other;
            return this.position == newOther.position
                && this.length == newOther.length;
        }
        return false;
    }
}
