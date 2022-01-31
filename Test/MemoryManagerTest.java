import java.io.File;
import java.io.UnsupportedEncodingException;
import student.TestCase;
import java.util.Arrays;

/**
 * Tests the MemoryManager class
 * 
 * @author Taimoor Qamar
 * @version 11-16-19
 *
 */
public class MemoryManagerTest extends TestCase {
    private MemoryManager mm;
    private final String filename = "TestMemoryManager.bin";


    /**
     * Sets up variable before every test
     */
    public void setUp() {
        new File(filename).delete();
        mm = new MemoryManager(filename);
    }


    /**
     * Tests the insert method in MemoryManager
     */
    public void testInsert() {
        // basic:
        DLList<Prj4Handle> testFreeList = new DLList<>();
        testFreeList.add(new Prj4Handle(0, 10));
        DLList<Prj4Handle> testOccupiedList = new DLList<>();
        testOccupiedList.add(new Prj4Handle(-1, -1));
        assertEquals(mm.toString(), testFreeList.toString());
        String peter = "Peter Dolan";
        Prj4Handle h1 = null;
        try {
            h1 = mm.insert(peter.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            return;
        }
        assertEquals(h1, new Prj4Handle(0, 11));
        testOccupiedList.add(0, h1);
        testFreeList.clear();
        testFreeList.add(new Prj4Handle(11, 9));
        assertEquals(mm.toString(), testFreeList.toString());
        String feter = "wesfgpiuaedfbioguesfbvoibevfoiwerguhp";
        Prj4Handle h2 = null;
        try {
            h2 = mm.insert(feter.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            return;
        }
        assertEquals(h2, new Prj4Handle(11, 37));
        testOccupiedList.add(1, h2);
        testFreeList.clear();
        testFreeList.add(new Prj4Handle(48, 32));
        assertEquals(mm.toString(), testFreeList.toString());
        String newline = "\nf\nf\nf\nf\nf\nf\nf\nf\nf\nf\nf\nf\n";
        Prj4Handle h3 = null;
        try {
            h3 = mm.insert(newline.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            return;
        }
        assertEquals(h3, new Prj4Handle(48, 25));
        testOccupiedList.add(2, h3);
        testFreeList.clear();
        testFreeList.add(new Prj4Handle(73, 7));
        assertEquals(mm.toString(), testFreeList.toString());

        assertEquals(h1, new Prj4Handle(0, 11));
        assertEquals(h2, new Prj4Handle(11, 37));
        assertEquals(h3, new Prj4Handle(48, 25));

        try {
            assertEquals(new String(mm.getRecord(h1), "UTF-8"), peter);
            assertEquals(new String(mm.getRecord(h2), "UTF-8"), feter);
            assertEquals(new String(mm.getRecord(h3), "UTF-8"), newline);
        }
        catch (UnsupportedEncodingException e) {
            return;
        }
        mm.closeMM();

    }


    /**
     * Tests the release method in MemoryManager
     */
    public void testRelease() {
        int numEssays = (int)(Math.random() * 101) + 50;
        byte[][] essays = new byte[numEssays][];
        Prj4Handle[] handles = new Prj4Handle[numEssays];

        for (int outer = 0; outer < essays.length; outer++) {
            int sizeOfEssay = (int)(Math.random() * 100) + 1;
            essays[outer] = new byte[sizeOfEssay];
            for (int i = 0; i < essays[outer].length; i++) {
                essays[outer][i] = (byte)((int)(Math.random() * 95) + 32);
            }
        }
        int numOps = (int)(Math.random() * 1001) + 500; // [500, 1500]
        int howManyToInsert = (int)(Math.random() * 10) + numEssays / 2 - 5;
        for (int i = 0; i < howManyToInsert; i++) {
            handles[i] = mm.insert(essays[i]);
        }
        for (int i = 0; i < numOps; i++) {
            int whichByte = (int)(Math.random() * (numEssays)); // [0,
                                                                // numEssays-1]
            if (handles[whichByte] == null) { // this essay has not been
                                              // inserted yet
                handles[whichByte] = mm.insert(essays[whichByte]);
            }
            else {

                assertTrue(Arrays.equals(mm.getRecord(handles[whichByte]),
                    essays[whichByte]));
                mm.release(handles[whichByte]);
                handles[whichByte] = null;
            }
        }
        mm.closeMM();
    }
}
