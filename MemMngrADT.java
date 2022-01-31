/**
 * This interface defines what a Memory Manager should do (iterface is like an
 * abstract class) if that Memory Manager is being used for this project.
 * 
 * @author Taimoor Qamar
 * @version 11-16-19
 *
 */
public interface MemMngrADT {
    /**
     * Allocates memory in the file for the given bytes and returns a Handle to
     * that memory so that the given bytes can be retrieved later.
     * 
     * @param info
     *            The bytes to be stored in the file (might have to use
     *            [instance of a String].getBytes("UTF-8"), e.g. buffer1 =
     *            vtStudents.getBytes("UTF-8");)
     * @return a Handle to the memory just allocated
     */
    public Prj4Handle insert(byte[] info);


    /**
     * When the bytes corresponding to the given handle no longer need to be
     * retrieved, call this method to make room in the file for other records.
     * Make sure that you do not have any "dangling pointers" to the memory
     * after calling this method (which is essentially free() from the C
     * programming language).
     * 
     * @param h
     *            The handle of the memory that no longer needs to be stored in
     *            the file
     */
    public void release(Prj4Handle h);


    /**
     * Retrieves the bytes associated with a given Handle. The bytes will still
     * be in the file even after they are retrieved (i.e. this does not modify
     * the file).
     * 
     * @param h
     *            the Handle given by the insert method that you use to get the
     *            bytes back that you inserted
     * @return the bytes associated with a given Handle
     */
    public byte[] getRecord(Prj4Handle h);
}
