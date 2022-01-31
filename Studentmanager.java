/**
 *
 * @author Taimoor Qamar
 * @version 12/9/19
 *          The main driver class. This class reads the command-line inputs,
 *          makes a RealStudentManager using the hash table size, the hash table
 *          file, and the memory manager file (which will handle the actual
 *          commands after they are processed) passes the RealStudentManager and
 *          command file to the Parser (which will read the command file and
 *          call appropriate RealStudentManager methods).
 *
 */
public class Studentmanager {

    /**
     * Does some stuff with a hash table and a memory manager and prints its
     * progress to standard output
     * 
     * @param args
     *            <command-file> the first word indicates a .txt file from which
     *            this program will read its commands
     *            <hash-file> the second word indicates a file where the
     *            hashtable will be stored
     *            <hash-table-size> the third word indicate the maximum size
     *            that the hashtable can be
     *            <memory-file> the fourth word indicates where the essays and
     *            full names will be stored
     */
    public static void main(String[] args) {
        String cmdFileName = args[0];
        String hashFile = args[1]; // might not need anymore (specs changed)
        String hashTableSize = args[2]; // must be a multiple of 32
        String memoryFile = args[3];
        RealStudentManager studentManager = new RealStudentManager(hashFile,
            hashTableSize, memoryFile);
        Parser parser = new Parser(cmdFileName, studentManager);
        parser.parseFile();
    }
}
