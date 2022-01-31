import java.nio.ByteBuffer;

/**
 * @author Taimoor Qamar
 *
 */
//public class HashTableTest extends student.TestCase {
//    // FIELDS
//    String testPID;
//    ByteBuffer testBuffer;
//    HashTable testHash;
//    Prj4Handle testNameHandle;
//    Prj4Handle testEssayHandle;
//
//
//    // ----------------------------------------------------------------
//    /**
//     * Initializes test cases.
//     */
//    public void setUp() {
//        testPID = "123456789";
//        testNameHandle = new Prj4Handle(9, 8);
//        testEssayHandle = new Prj4Handle(17, 8);
//        testHash = new HashTable("hashFileTest", 32);
//    }
//
//
//    // ----------------------------------------------------------------
//    public void testConstructor() {
//        assertNotNull(testHash);
//    }
//
//
//    // ----------------------------------------------------------------
//
//    public void testInsert() {
//        // BASIC INSERT
//        testHash.insert(testPID, testNameHandle);
//        testHash.fillBucket(18);
//        assertEquals(testHash.getPID(18), testPID);
//        assertEquals(testHash.getNameHandle(18).getPosition(), testNameHandle
//            .getPosition());
//        assertEquals(testHash.getNameHandle(18).getLength(), testNameHandle
//            .getLength());
//    }
//
//
//    // ----------------------------------------------------------------
//
//    public void testUpdate() {
//        // BASIC UPDATE
//        testHash.insert(testPID, testNameHandle);
//        testHash.fillBucket(18);
//        assertEquals(testHash.getPID(18), testPID);
//        assertEquals(testHash.getNameHandle(18).getPosition(), testNameHandle
//            .getPosition());
//        assertEquals(testHash.getNameHandle(18).getLength(), testNameHandle
//            .getLength());
//        Prj4Handle testUpdateNameHandle = new Prj4Handle(9, 42);
//        testHash.update(testPID, testUpdateNameHandle);
//        testHash.fillBucket(18);
//        assertEquals(testHash.getNameHandle(18).getPosition(),
//            testUpdateNameHandle.getPosition());
//        assertEquals(testHash.getNameHandle(18).getLength(),
//            testUpdateNameHandle.getLength());
//    }
//
//
//    // ----------------------------------------------------------------
//
//    public void testInsertEssay() {
//        // BASIC INSERT ESSAY
//        testHash.insert(testPID, testNameHandle);
//        testHash.fillBucket(18);
//        assertEquals(testHash.getEssayHandle(18).getPosition(), -1);
//        assertEquals(testHash.getEssayHandle(18).getLength(), -1);
//        testHash.insertEssay(testPID, testEssayHandle);
//        testHash.fillBucket(18);
//        assertEquals(testHash.getEssayHandle(18).getPosition(), testEssayHandle
//            .getPosition());
//        assertEquals(testHash.getEssayHandle(18).getLength(), testEssayHandle
//            .getLength());
//    }
//
//
//    // ----------------------------------------------------------------
//
//    public void testRemove() {
//        // BASIC REMOVE
//        testHash.insert(testPID, testNameHandle);
//        testHash.fillBucket(18);
//        assertEquals(testHash.getPID(18), testPID);
//        assertEquals(testHash.getNameHandle(18).getPosition(), testNameHandle
//            .getPosition());
//        assertEquals(testHash.getNameHandle(18).getLength(), testNameHandle
//            .getLength());
//        testHash.remove(testPID);
//        testHash.fillBucket(18);
//        System.out.print(testHash.getPID(18) + "\n");
//        assertTrue(testHash.getNameHandle(18).getPosition() < 0);
//        assertTrue(testHash.getNameHandle(18).getLength() < 0);
//    }
//
//
//    // ----------------------------------------------------------------
//
//    public void testRemoveEssay() {
//        // BASIC ESSAY REMOVE
//        testHash.insert(testPID, testNameHandle);
//        testHash.fillBucket(18);
//        assertEquals(testHash.getEssayHandle(18).getPosition(), -1);
//        assertEquals(testHash.getEssayHandle(18).getLength(), -1);
//        testHash.insertEssay(testPID, testEssayHandle);
//        testHash.fillBucket(18);
//        assertEquals(testHash.getEssayHandle(18).getPosition(), testEssayHandle
//            .getPosition());
//        assertEquals(testHash.getEssayHandle(18).getLength(), testEssayHandle
//            .getLength());
//        testHash.removeEssayHandle(testPID);
//        testHash.fillBucket(18);
//        assertTrue(testHash.getEssayHandle(18).getPosition() < 0);
//        assertTrue(testHash.getEssayHandle(18).getLength() < 0);
//        assertEquals(testHash.getNameHandle(18).getPosition(), testNameHandle
//            .getPosition());
//        assertEquals(testHash.getNameHandle(18).getLength(), testNameHandle
//            .getLength());
//    }
//
//    // ----------------------------------------------------------------
//
//}
