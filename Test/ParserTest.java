/**
 * @author Taimoor Qamar
 * @version 2019.14/14
 *
 */
public class ParserTest extends student.TestCase {
    // FIELDS
    private Parser testParser;


    /**
     * Initializes the cases for all the tests.
     */
    public void setUp() {
        RealStudentManager rsmTest = new RealStudentManager("hashTable", "64",
            "memoryFile");
        testParser = new Parser("prj4sample.txt", rsmTest);
    }


    /**
     * Tests mostly every method in the project via the sample input provided by
     * the instructor.
     */
    public void test() {
        testParser.parseFile();
        assertFuzzyEquals("Created Hash Set with 64 slots.\r\n"
            + "Francisco Paco inserted.\r\n" + "Cassandra Witt inserted.\r\n"
            + "essay saved for Cassandra Witt\r\n"
            + "SampleStudents.csv successfully loaded.\r\n"
            + "Warning: Student 022136196 Cassandra Witt is"
            + " not loaded since a student with the same pid exists.\r\n"
            + "essay commands can only follow successful insert"
            + " or update commands\r\n"
            + "Student 711016998 updated to Zeph Serrano\r\n"
            + "essay saved for Zeph Serrano\r\n"
            + "Student 770218626 updated to Rooney Horton\r\n"
            + "essay saved for Rooney Horton\r\n"
            + "711016999 is not found in the database.\r\n"
            + "record with pid 711016998 with full name"
            + " Zeph Serrano is cleared.\r\n"
            + "James David insertion failed since the pid"
            + " 000030822 belongs to another student.\r\n"
            + "essay commands can only follow successful"
            + " insert or update commands\r\n" + "James David inserted.\r\n"
            + "essay saved for James David\r\n" + "Cao Young inserted.\r\n"
            + "essay saved for Cao Young\r\n"
            + "Search Failed. Couldn't find any student with ID 301461169\r\n"
            + "Sheik Salah inserted.\r\n" + "essay saved for Sheik Salah\r\n"
            + "Search Failed. Couldn't find any student with ID Amber\r\n"
            + "Student 440978815 updated to Raphael Kramer\r\n"
            + "essay saved for Raphael Kramer\r\n"
            + "Student 729231860 updated to Roman Empror\r\n"
            + "Student 440978815 updated to Persian King\r\n"
            + "815012117 with full name Ezekiel Ruiz is removed"
            + " from the database.\r\n" + "940597271 Lionel Wood:\r\n"
            + "Search Failed. Couldn't find any student with ID 476753012\r\n"
            + "Search Failed. Couldn't find any student with ID 815012117\r\n"
            + "815012117 is not found in the database.\r\n"
            + "X Y inserted.\r\n" + "essay saved for X Y\r\n"
            + "815012117 X Y:\r\n"
            + "What is the highest possible score in Pac Man?.According to"
            + " the Wikipedia page for Pac-Man, the highest possible"
            + " score is 3333360 points."
            + " It's called a perfect game and it was achieved by 3 different"
            + " people already.\r\n" + "949920714 Sheik Salah:\r\n"
            + "ALL PRAISE IS DUE TO GOD..We thank him, we seek his help"
            + " and forgiveness..We see refuge with Lord from the evils"
            + " of our own soles and our bad deeds..Whomsoever"
            + " Lord guides no one can"
            + " lead astray, and whomsoever he misguides no one can guide.\r\n"
            + "Students in the database:\r\n" + "Quentin Clay at slot 7\r\n"
            + "Quon Ochoa at slot 8\r\n" + "Melinda Chen at slot 9\r\n"
            + "Orlando Dodson at slot 10\r\n" + "Francisco Paco at slot 22\r\n"
            + "James Harrington at slot 24\r\n"
            + "Dominic Kirkland at slot 25\r\n" + "Sheik Salah at slot 26\r\n"
            + "X Y at slot 36\r\n" + "Persian King at slot 37\r\n"
            + "Zeph Serrano at slot 38\r\n" + "Rooney Horton at slot 39\r\n"
            + "Lionel Wood at slot 41\r\n" + "Cassandra Witt at slot 43\r\n"
            + "Burton Frost at slot 45\r\n" + "Cao Young at slot 48\r\n"
            + "Fuller Frye at slot 52\r\n" + "Roman Empror at slot 56\r\n"
            + "James David at slot 61\r\n" + "Free Block List:\r\n"
            + "Free Block 1 starts from Byte 95 with length 9\r\n"
            + "Free Block 2 starts from Byte 128 with length 3\r\n"
            + "Free Block 3 starts from Byte 327 with length 11", systemOut()
                .getHistory());
        systemOut().clearHistory();
    }
}
