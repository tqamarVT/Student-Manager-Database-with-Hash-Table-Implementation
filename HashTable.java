import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * The hash table stores handles which can be used to retrieve records from the
 * memory manager. It does so efficiently via the string hashing function,
 * linear probing for collision handling, and bucket hashing to reduce File
 * read/write operations.
 * 
 * @author Taimoor Qamar (tqamar)
 * @version 2019.11.21
 *
 */
public class HashTable {
    private static final int HANDLE_SIZE = 8;
    private static final int NUM_HANDLES = 2;
    private static final int PID_SIZE = 9;
    private static final int SLOT_SIZE = (HANDLE_SIZE * NUM_HANDLES) + PID_SIZE;
    private static final int BUCKET_SLOT_SIZE = 32;
    private static final int BUCKET_SIZE = BUCKET_SLOT_SIZE * SLOT_SIZE;
    private static final int EMPTY = -1;
    private static final int TOMBSTONE = -2;
    private int numSlots;
    private MemoryManager memManager;
    private RandomAccessFile hashFile;
    private byte[] bucket;


// ----------------------------------------------------------------
    /**
     * Constructor for the hashtable class.
     * 
     * @param hashFileName
     *            file to name the hashfile
     * @param memManager
     *            memory manager containing student records
     * @param tableSize
     *            hashtable size.
     */
    public HashTable(
        String hashFileName,
        MemoryManager memManager,
        int tableSize) {
        try {
            this.memManager = memManager;
            hashFile = new RandomAccessFile(hashFileName, "rw");
            numSlots = tableSize;
            hashFile.setLength(numSlots * SLOT_SIZE);
            hashFile.seek(0);
            for (int i = 0; i < numSlots * SLOT_SIZE; i++) {
                hashFile.writeInt(EMPTY);
            }
            System.out.print("Created hash set with " + numSlots + " slots \n");
        }
        catch (FileNotFoundException e) {
            System.out.print("File Not Found Error!");
        }
        catch (IOException e) {
            System.out.print("File Read/Write Error!");
        }
    }


    // ----------------------------------------------------------------
    /**
     * Helper method to fill an array of bytes from the hash-file to reduce file
     * read/write processes.
     * 
     * @param slot
     *            the slot within the bucket to fill.
     */
    public void fillBucket(long slot) {
        long bucketNum = slot / BUCKET_SLOT_SIZE;
        bucket = new byte[BUCKET_SIZE];
        // could add an if statement here to check if we already filled a bucket
        // corresponding to this bucket number to reduce file-reading.
        try {
            hashFile.seek(bucketNum * BUCKET_SIZE);
            hashFile.read(bucket);
        }
        catch (IOException e) {
            System.out.print("File Read/Write Error!");
        }
    }


    // ----------------------------------------------------------------
    /**
     * Insert a pid and name-handle record into the hash table.
     * 
     * @param pID
     *            pid to be inserted
     * @param nameHandle
     *            name handle to be inserted
     */
    public void insert(String pID, Prj4Handle nameHandle) {
        long orgSlot = sfold(pID, numSlots);
        long currSlot = orgSlot;
        fillBucket(orgSlot);
        do {
            if (openSlot(currSlot)) {
                if (slotTombstoned(currSlot)) {
                    if (checkPIDDuplicates(currSlot, pID) != -1) {
                        return;
                    }
                    else {
                        writeRecord(currSlot, pID, nameHandle);
                        return;
                    }
                }
                else {
                    writeRecord(currSlot, pID, nameHandle);
                    return;
                }
            }
            else {
                if (getPID(currSlot).equals(pID)) {
                    return;
                }
            }
            currSlot = nextSlot(currSlot);
        }
        while (currSlot != orgSlot);
    }


    // ----------------------------------------------------------------
    /**
     * Indicates whether the bucket containing the slot containing the pid
     * passed is full.
     * 
     * @param pID
     *            the pid for which to search the slot.
     * @return true or false.
     */
    public boolean bucketFull(String pID) {

        long orgSlot = sfold(pID, numSlots);
        long currSlot = orgSlot;
        fillBucket(orgSlot);
        do {

            if (openSlot(currSlot)) {
                return false;
            }
            currSlot = nextSlot(currSlot);
        }
        while (currSlot != orgSlot);
        return true;
    }


    // ----------------------------------------------------------------
    /**
     * Inserts an essay handle into the hash table for the student associated
     * with the pid passed.
     * 
     * @param pID
     *            the pid of the student whos essay we will store the essay
     *            handle for.
     * @param essayHandle
     *            the essay handle to store.
     */
    public void insertEssay(String pID, Prj4Handle essayHandle) {

        long orgSlot = sfold(pID, numSlots);
        long newSlot = checkPIDDuplicates(orgSlot, pID);
        writeEssay(newSlot, essayHandle);
    }


    // ----------------------------------------------------------------
    /**
     * Remove the entire record associated with the student associated with the
     * pid passed in the hash table.
     *
     * @param pID
     *            the pid of the student who's record will be removed.
     */
    public void remove(String pID) {
        long orgSlot = sfold(pID, numSlots);
        long currSlot = orgSlot;
        fillBucket(orgSlot);
        do {
            if (slotEmpty(currSlot)) {
                System.out.print(pID + " is not found in the database \n");
                return;
            }
            else if (getPID(currSlot).equals(pID)) {

                tombstoneSlot(currSlot);
                return;
            }
            else {
                currSlot = nextSlot(currSlot);
            }
        }
        while (currSlot != orgSlot);
        System.out.print(pID + " is not found in the database \n");
    }


    // ----------------------------------------------------------------

    /**
     * Remove just the essay handle for the essay for the student associated
     * with the pid passed in the parameter.
     * 
     * @param pID
     *            the pid of the student who's record will be removed.
     */
    public void removeEssayHandle(String pID) {
        long orgSlot = sfold(pID, numSlots);
        long currSlot = orgSlot;
        fillBucket(orgSlot);
        do {
            if (slotEmpty(currSlot)) {
                System.out.print(pID + " is not found in the database. \n");
                return;
            }
            else if (getPID(currSlot).equals(pID)) {

                tombstoneEssayOnly(currSlot);

                return;
            }
            else {
                currSlot = nextSlot(currSlot);
            }
        }
        while (currSlot != orgSlot);
        System.out.print(pID + " is not found in the database. \n");
    }


    // ----------------------------------------------------------------
    /**
     * Print all the records for the students contained in this hash table.
     */
    public void print() {
        System.out.print("Students in the database:\n");
        for (int i = 0; i < numSlots; i++) {

            if (i % BUCKET_SLOT_SIZE == 0) {
                fillBucket(i);
            }
            if (!openSlot(i)) {
                System.out.print(getFullNameRecord(i) + " at slot " + i + "\n");
            }
        }
        System.out.println("Free Block List: ");
        System.out.print(memManager.toString() + "\n");
    }


    // ----------------------------------------------------------------
    /**
     * Writes a pid and nameHandle offset/length record to the hashtable.
     * 
     * @param slot
     *            the slot to write to.
     * @param pID
     *            the pid to write
     * @param nameHandle
     *            the memory handle to write.
     */
    private void writeRecord(long slot, String pID, Prj4Handle nameHandle) {
        try {
            byte[] pidBytes = pID.getBytes("UTF-8");
            hashFile.seek(slot * SLOT_SIZE);
            hashFile.write(pidBytes);
            hashFile.writeInt(nameHandle.getPosition());
            hashFile.writeInt(nameHandle.getLength());
        }
        catch (IOException e) {
            System.out.print("File I/O Error! \n");
        }

    }


    // ----------------------------------------------------------------

    /**
     * Writes the tombstone key to the slot passed in the parameter, effectively
     * deleting the record in the hash table.
     * 
     * @param slot
     *            the slot to tombstone.
     */
    private void tombstoneSlot(long slot) {
        try {
            hashFile.seek(slot * SLOT_SIZE);
            for (int i = 0; i < SLOT_SIZE; i++) {
                hashFile.write(TOMBSTONE);
            }
        }

        catch (IOException e) {
            System.out.print("File I/O Error! \n");
        }

    }


    // ----------------------------------------------------------------
    /**
     * Writes the tombstone key to the slot passed in the parameter, effectively
     * deleting the record in the hash table.
     * 
     * @param slot
     *            the slot to tombstone.
     */
    private void tombstoneEssayOnly(long slot) {
        try {
            hashFile.seek((slot * SLOT_SIZE) + PID_SIZE + HANDLE_SIZE);
            for (int i = 0; i < HANDLE_SIZE; i++) {
                hashFile.write(TOMBSTONE);
            }
        }
        catch (IOException e) {
            System.out.print("File I/O Error! \n");
        }
    }


    // ----------------------------------------------------------------
    /**
     * Writes an essay record in the essay portion of the slot passed in the
     * parameter.
     * 
     * @param slot
     *            the slot to write to.
     * @param essayHandle
     *            the essay handle information to write.
     */
    private void writeEssay(long slot, Prj4Handle essayHandle) {
        try {
            hashFile.seek((slot * SLOT_SIZE) + PID_SIZE + HANDLE_SIZE);
            hashFile.writeInt(essayHandle.getPosition());
            hashFile.writeInt(essayHandle.getLength());
        }
        catch (IOException e) {
            System.out.print("File I/O Error! \n");
        }

    }


    // ----------------------------------------------------------------

    /**
     * Hashing function provided by instructor.
     * 
     * @param s
     *            The string to be hashed
     * @param m
     *            The table size
     * @return The slot number for the string
     */
    private long sfold(String s, int m) {
        int intLength = s.length() / 4;
        long sum = 0;
        for (int j = 0; j < intLength; j++) {
            char[] c = s.substring(j * 4, (j * 4) + 4).toCharArray();
            long mult = 1;
            for (int k = 0; k < c.length; k++) {
                sum += c[k] * mult;
                mult *= 256;
            }
        }

        char[] c = s.substring(intLength * 4).toCharArray();
        long mult = 1;
        for (int k = 0; k < c.length; k++) {
            sum += c[k] * mult;
            mult *= 256;
        }

        sum = (sum * sum) >> 8;
        return (Math.abs(sum) % m);
    }


    // ----------------------------------------------------------------
    /**
     * Returns the status of the slot passed.
     * 
     * @param slot
     *            slot in hash-table
     * @return status of slot
     */
    private boolean openSlot(long slot) {
        return bucket[getBucketIndex(slot)] == EMPTY || bucket[getBucketIndex(
            slot)] == TOMBSTONE;
    }


    // ----------------------------------------------------------------
    /**
     * Indicates whether the slot passed is tombstoned.
     * 
     * @param slot
     *            the slot to check
     * @return the tombstone status
     */
    private boolean slotTombstoned(long slot) {
        return bucket[getBucketIndex(slot)] == TOMBSTONE;
    }


    // ----------------------------------------------------------------
    /**
     * Indicates whether the slot passed is empty
     * 
     * @param slot
     *            the slot to check
     * @return the empty status.
     */
    private boolean slotEmpty(long slot) {
        return bucket[getBucketIndex(slot)] == EMPTY;
    }


    // ----------------------------------------------------------------
    /**
     * Gets the index in the bucket array corresponding to the slot passed in
     * the parameter.
     * 
     * @param slot
     *            the slot for which to get the bucket index.
     * @return the index of the bucket
     */
    private int getBucketIndex(long slot) {
        return (int)((slot % BUCKET_SLOT_SIZE) * SLOT_SIZE);
    }


    // ----------------------------------------------------------------
    /**
     * Gets the index in the bucket array corresponding to the slot passed in
     * the parameter.
     * 
     * @param slot
     *            the slot for which to get the bucket index.
     * @return the index of the bucket
     */
    private long nextSlot(long slot) {
        if (slot % BUCKET_SLOT_SIZE == BUCKET_SLOT_SIZE - 1) {
            return ((slot - BUCKET_SLOT_SIZE) + 1);
        }
        return (slot + 1);
    }


    // ----------------------------------------------------------------
    /**
     * Returns true or false depending on if the hash table contains the pid
     * passed in the parameter.
     * 
     * @param pID
     *            the pid to search for
     * @return true or false
     */
    public boolean hasPID(String pID) {
        long orgSlot = sfold(pID, numSlots);
        return checkPIDDuplicates(orgSlot, pID) != -1;
    }


    // ----------------------------------------------------------------
    /**
     * Returns a string representation of the pid located at the slot passed in
     * the parameter.
     * 
     * @param slot
     *            the slot for which to retrieve the pid
     * @return string representation of the pid.
     */
    private String getPID(long slot) {
        String pID = null;
        try {
            byte[] pidBytes = new byte[PID_SIZE];
            for (int i = 0; i < pidBytes.length; i++) {
                pidBytes[i] = bucket[getBucketIndex(slot) + i];
            }
            pID = new String(pidBytes, "UTF-8");
            return pID;
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return pID;
    }


    // ----------------------------------------------------------------
    /**
     * Retrieves the namehandle located at the slot in the hash table, passed in
     * the parameter.
     * 
     * @param slot
     *            the slot containing the name handle to retrieve.
     * @return the name handle.
     */
    private Prj4Handle getNameHandle(long slot) {
        byte[] nameHandleBytes = new byte[HANDLE_SIZE];
        for (int i = 0; i < nameHandleBytes.length; i++) {
            nameHandleBytes[i] = bucket[getBucketIndex(slot) + PID_SIZE + i];
        }
        ByteBuffer tempBuffer = ByteBuffer.wrap(nameHandleBytes);
        Prj4Handle nameHandle = new Prj4Handle(tempBuffer.getInt(), tempBuffer
            .getInt());
        return nameHandle;
    }


    // ----------------------------------------------------------------
    /**
     * Retrieves the essay handle located at the slot passed in the parameter.
     * 
     * @param slot
     *            the slot containing the essay handle.
     * @return the essay handle.
     */
    private Prj4Handle getEssayHandle(long slot) {
        byte[] essayHandleBytes = new byte[HANDLE_SIZE];
        for (int i = 0; i < essayHandleBytes.length; i++) {
            essayHandleBytes[i] = bucket[getBucketIndex(slot) + PID_SIZE
                + HANDLE_SIZE + i];
        }
        ByteBuffer tempBuffer = ByteBuffer.wrap(essayHandleBytes);
        Prj4Handle essayHandle = new Prj4Handle(tempBuffer.getInt(), tempBuffer
            .getInt());
        if (essayHandle.getLength() < 0 || essayHandle.getPosition() < 0) {
            return null;
        }
        return essayHandle;
    }


    // ----------------------------------------------------------------
    /**
     * Gets the namehandle in the hash table corresponding to the pid passed in
     * the parameter.
     * 
     * @param pID
     *            the pid for the student whos name handle will be retrieved.
     * @return the name handle
     */
    public Prj4Handle getNameHandle(String pID) {
        long orgSlot = sfold(pID, numSlots);
        long newSlot = checkPIDDuplicates(orgSlot, pID);
        if (newSlot == -1) {
            return null;
        }
        byte[] nameHandleBytes = new byte[HANDLE_SIZE];
        for (int i = 0; i < nameHandleBytes.length; i++) {
            nameHandleBytes[i] = bucket[getBucketIndex(newSlot) + PID_SIZE + i];
        }
        ByteBuffer tempBuffer = ByteBuffer.wrap(nameHandleBytes);
        Prj4Handle nameHandle = new Prj4Handle(tempBuffer.getInt(), tempBuffer
            .getInt());
        return nameHandle;
    }


    // ----------------------------------------------------------------
    /**
     * Retrieves the essayhandle located in the hash table for the student
     * corresponding to the pid passed in the parameter.
     * 
     * @param pID
     *            pid of the student whos essay handle will be retrieved
     * @return the essay handle
     */
    public Prj4Handle getEssayHandle(String pID) {
        long orgSlot = sfold(pID, numSlots);
        long newSlot = checkPIDDuplicates(orgSlot, pID);
        if (newSlot == -1) {
            return null;
        }
        byte[] essayHandleBytes = new byte[HANDLE_SIZE];
        for (int i = 0; i < essayHandleBytes.length; i++) {
            essayHandleBytes[i] = bucket[getBucketIndex(newSlot) + PID_SIZE
                + HANDLE_SIZE + i];
        }
        ByteBuffer tempBuffer = ByteBuffer.wrap(essayHandleBytes);
        Prj4Handle essayHandle = new Prj4Handle(tempBuffer.getInt(), tempBuffer
            .getInt());
        if (essayHandle.getLength() < 0 || essayHandle.getPosition() < 0) {
            return null;
        }
        return essayHandle;
    }


    // ----------------------------------------------------------------
    /**
     * Returns the slot number in the hash table that contains the pid passed in
     * the parameter. If pid is not in hash table, retunrs -1.
     * 
     * @param slot
     *            The slot from which to start searching for the pid, usually
     *            calculated via the sfold function on the pid.
     * @param pID
     *            the pid to search for
     * @return slot number or -1
     */

    private long checkPIDDuplicates(long slot, String pID) {
        fillBucket(slot);
        long tempSlot = slot;
        do {
            if (getPID(tempSlot).equals(pID)) {
                return tempSlot;
            }
            tempSlot = nextSlot(tempSlot);
        }
        while (tempSlot != slot && tempSlot != EMPTY);
        return -1;
    }


    // ----------------------------------------------------------------
    /**
     * Retrieves the fullname record from the memory manager for the name handle
     * stored at the slot passed in the parameter, and returns it as a string.
     *
     * @param slot
     *            the slot containing the name handle to search for.
     * @return a string representation of the full name retrieved from memory
     *         manager.
     */
    private String getFullNameRecord(long slot) {
        if (openSlot(slot)) {
            return null;
        }
        byte[] fullNameBytes = memManager.getRecord(getNameHandle(slot));
        String fullName = null;
        try {
            fullName = new String(fullNameBytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fullName;
    }


    // ----------------------------------------------------------------
    /**
     * Retrieves a string representation of the full name record corresponding
     * to the pid passed in the parameter from the memory manager.
     * 
     * @param pID
     *            the pid corresponding to the student whos full name is
     *            retrieved.
     * @return a string representation of the students full name.
     */
    public String getFullNameRecord(String pID) {
        long orgSlot = sfold(pID, numSlots);
        long newSlot = checkPIDDuplicates(orgSlot, pID);
        if (newSlot == -1) {
            return null;
        }

        if (openSlot(newSlot)) {
            return null;
        }
        byte[] fullNameBytes = memManager.getRecord(getNameHandle(newSlot));
        String fullName = null;
        try {
            fullName = new String(fullNameBytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fullName;
    }


    // ----------------------------------------------------------------
    /**
     * Retrieves an essay record corresponding to the student corresponding to
     * the pid passed in the parameter, from the memory manager.
     * 
     * @param pID
     *            of the student whos essay record is retrieved.
     * @return A string representation of the essay.
     */
    public String getEssayRecord(String pID) {
        long orgSlot = sfold(pID, numSlots);
        long newSlot = checkPIDDuplicates(orgSlot, pID);

        if (newSlot == -1) {
            return null;
        }

        if (openSlot(newSlot)) {
            return null;
        }
        Prj4Handle essayHandle = getEssayHandle(newSlot);
        if (essayHandle == null) {
            return null;
        }
        else {
            byte[] essayBytes = memManager.getRecord(essayHandle);
            String essay = null;
            try {
                essay = new String(essayBytes, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return essay;
        }
    }
}
