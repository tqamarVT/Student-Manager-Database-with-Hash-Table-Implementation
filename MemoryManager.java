import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * The Memory Manager for this project (Memory Manager is defined in the
 * MemMngrADT interface). Uses First Fit to select the block of memory.
 * 
 *
 * @author Taimoor Qamar
 * @version 11-16-19
 *
 */
public class MemoryManager implements MemMngrADT {
    private DLList<Prj4Handle> freeList;
    private static final int DEFAULT_FILE_SIZE = 10;
    private int size;
    private String filename;
    private RandomAccessFile r;


    /**
     * Makes a new Memory Manager whose memory file will have the given filename
     * and will start with the given filesize.
     * 
     * @param fname
     *            where the memory manager saves the records
     * @param filesize
     *            the initial size of the file used to store records (can expand
     *            later automatically)
     */
    public MemoryManager(String fname, int filesize) {
        filename = fname;
        size = filesize;
        try {
            r = new RandomAccessFile(filename, "rw");
        }
        catch (FileNotFoundException e) {
            System.out.println(
                "MemoryManager: constructor: FileNotFound Filename: "
                    + filename);
        }
        try {
            r.setLength(filesize);
        }
        catch (IOException e) {
            System.out.println(
                "MemoryManager: constructor: IOException Filename: " + filename
                    + " File size: " + size);
        }
        freeList = new DLList<>();
        Prj4Handle newFreeBlock = new Prj4Handle(0, size);
        freeList.add(newFreeBlock);
    }


    /**
     * Constructor for the MemoryManager class that assumes the default size.
     * 
     * @param fname
     *            the name of the file.
     */
    public MemoryManager(String fname) {
        this(fname, DEFAULT_FILE_SIZE);
    }


    @Override
    public Prj4Handle insert(byte[] info) {
        if (info == null) {
            System.out.println("MemoryManager: insert: info is null");
            return null;
        }
        int startingPosition = -1;
        int length = -1;

        // insert record at first freelist element with enough space:
        for (int i = 0; i < freeList.size(); i++) {
            if (freeList.get(i).getLength() >= info.length) {
                // handle:
                startingPosition = freeList.get(i).getPosition();
                length = info.length;
                // update freelist:
                int newLength = freeList.get(i).getLength() - info.length;
                freeList.remove(i);
                if (newLength > 0) {
                    freeList.add(i, new Prj4Handle(startingPosition
                        + info.length, newLength)); // unsure
                }
                break;
            }
        }

        // if NO element has enough space: (increase file size and update
        // freelist)
        if (startingPosition == -1 && length == -1) {
            size = 2 * size;
            try {
                r.setLength(size);
            }
            catch (IOException e) {
                System.out.println("MemoryManager: insert: IOException"
                    + " (after expanding) Filename: " + filename
                    + " File size: " + size);
            }
            // need to update freeList:
            // if there are NO blocks in the freelist (file is completely full):
            if (freeList.size() == 0) {
                freeList.add(freeList.size(), new Prj4Handle(size / 2, size
                    / 2));
            }
            // check if last element is adjacent to the new block:
            else if (freeList.get(freeList.size() - 1).getPosition() + freeList
                .get(freeList.size() - 1).getLength() == size / 2) {
                int newPos = freeList.get(freeList.size() - 1).getPosition();
                int newLeng = freeList.get(freeList.size() - 1).getLength()
                    + (size / 2);
                freeList.remove(freeList.size() - 1);
                freeList.add(freeList.size(), new Prj4Handle(newPos, newLeng));
            }
            else {
                freeList.add(freeList.size(), new Prj4Handle(size / 2, size
                    / 2));
            }
            return this.insert(info); // hope no StackOverFlow
        }
        Prj4Handle result = new Prj4Handle(startingPosition, length);
        // write bytes to file:
        try {
            r.seek(result.getPosition());
            r.write(info);
        }
        catch (IOException e) {
            System.out.println(
                "MemoryManager: insert: IOException (while writing) Filename: "
                    + filename + " File size: " + size);
        }
        return result;
    }


    @Override
    public void release(Prj4Handle h) {
        if (h == null) { // or if length or position are invalid
            System.out.println("MemoryManager: release: h is null");
            return;
        }
        // find memory at h.getPosition() (I will 0 out for testing)
        byte[] zeros = new byte[h.getLength()];
        try {
            r.seek(h.getPosition());
            r.write(zeros);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
// update freelist so that current block is free:
        int size2 = freeList.size();
        for (int i = 0; i <= size2; i++) {
            // where to add the new free block (because freeList has to be
            // sorted)?
            // If the entire file is occupied insert new free block (anywhere)
            // Otherwise, there will be at least one free block.
            // Because position starts at 0, if 0 < h < freeList(i), then we
            // place the new before i
            // if that never happens, then we place it at the end (size - 1)
            if (i == freeList.size() || freeList.get(i).getPosition() > h
                .getPosition()) {
                // update freelist:
                freeList.add(i, new Prj4Handle(h.getPosition(), h.getLength()));
                // merge free blocks together:
                boolean isPrevAdjacent = (i != 0) && freeList.get(i - 1)
                    .getPosition() + freeList.get(i - 1).getLength() == freeList
                        .get(i).getPosition();
                boolean isNextAdjacent = (i != freeList.size() - 1) && freeList
                    .get(i).getPosition() + freeList.get(i)
                        .getLength() == freeList.get(i + 1).getPosition();
                // both?
                if (isPrevAdjacent && isNextAdjacent) {
                    int newPos = freeList.get(i - 1).getPosition();
                    int newLeng = freeList.get(i - 1).getLength() + freeList
                        .get(i).getLength() + freeList.get(i + 1).getLength();
                    freeList.remove(i + 1); // remove next
                    freeList.remove(i); // remove current
                    freeList.remove(i - 1); // remove previous
                    freeList.add(i - 1, new Prj4Handle(newPos, newLeng));
                    return;
                }
                // is freeList[i-1] adjacent? (what if i == 0)
                // hopefully the previous does not overflow into the current
                else if (isPrevAdjacent) {
                    int newPos = freeList.get(i - 1).getPosition();
                    int newLeng = freeList.get(i - 1).getLength() + freeList
                        .get(i).getLength();
                    freeList.remove(i); // remove current
                    freeList.remove(i - 1); // remove previous
                    freeList.add(i - 1, new Prj4Handle(newPos, newLeng));
                    return;
                }
                // is freeList[i+1] adjacent? (what if i is at end?)
                else if (isNextAdjacent) {
                    int newPos = freeList.get(i).getPosition();
                    int newLeng = freeList.get(i).getLength() + freeList.get(i
                        + 1).getLength();
                    freeList.remove(i + 1); // remove next
                    freeList.remove(i); // remove current
                    freeList.add(i, new Prj4Handle(newPos, newLeng));
                    return;
                }
                return;
            }
        }
    }


    @Override
    public byte[] getRecord(Prj4Handle h) {
        if (h == null) {
            System.out.println("MemoryManager: getRecord: h is null");
            return null;
        }
        // seek to h.getPosition() and store each byte in result until
        // we read h.getLength() bytes

        // if somehow a bad Handle occurs (or the file size shrinks)
        if (h.getPosition() >= this.size || h.getPosition() + h
            .getLength() > this.size) {
            System.out.println("MemoryManager: getRecord: handle"
                + " points outside of the file! Position: " + h.getPosition()
                + " Length: " + h.getLength() + " Size: " + size);
            return null;
        }

        try {
            r.seek(h.getPosition());
        }
        catch (IOException e) {
            System.out.println(
                "MemoryManager: getRecord: IOException Position: " + h
                    .getPosition() + " Length: " + h.getLength() + " Size: "
                    + size);
        }
        byte[] result = new byte[h.getLength()];
        try {
            r.read(result);
        }
        catch (IOException e) {
            System.out.println(
                "MemoryManager: getRecord: IOException 2 Position: " + h
                    .getPosition() + " Length: " + h.getLength() + " Size: "
                    + size);
        }

        return result;
    }

  
    /**
     * As described in the specifications document (I will wait until TAs post
     * output format). Does not include the free block between the last record
     * and the end of file (to my understanding).
     * 
     * @return for each free block, returns its starting byte position and size
     *         (from lowest to highest in order of byte position)
     */
    public String toString() {
        return freeList.toString();
    }


    /**
     * Allows other programs to use the file that the Memory Manager was using
     * (but the Memory Manager cannot use it anymore). Use once you are done
     * with the Memory Manager.
     */
    public void closeMM() {
        try {
            r.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
