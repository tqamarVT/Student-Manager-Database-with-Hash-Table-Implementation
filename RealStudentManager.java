import java.io.UnsupportedEncodingException;

/**
 * The RealStudentManager class is the only class that can actually do what the
 * commands tell it to do. It uses the HashTable and the MemoryManager to help
 * it execute its commands.
 *
 * @author Taimoor Qamar
 * @version 12/10/19
 *
 */
public class RealStudentManager {
    private HashTable hashTable;
    private MemoryManager memoryManager;
    private Student lastStudentInserted;


    // ----------------------------------------------------------------
    /**
     * Constructor method for the RealStudentManager class.
     * 
     * @param hf
     *            Name of the hash-file.
     * @param htsize
     *            Hash table size
     * @param mf
     *            Name of the memory-file.
     */
    public RealStudentManager(String hf, String htsize, String mf) {
        memoryManager = new MemoryManager(mf);
        hashTable = new HashTable(hf, memoryManager, Integer.parseInt(htsize));

    }


    // ----------------------------------------------------------------
    /**
     * 
     * Loads student data from an external file.
     * 
     * @param textfile
     *            the external file containing student data.
     */
    public void loadStudentData(String textfile) {
        SaveAndLoad loader = new SaveAndLoad(textfile);
        DetailedStudent[] loadedStudents = loader.loadStudentData();
        for (int i = 0; i < loadedStudents.length; i++) {
            if (hashTable.hasPID(loadedStudents[i].getPID())) {
                System.out.print("Warning: Student " + loadedStudents[i]
                    .getPID() + " " + loadedStudents[i].getName().toString()
                    + " is not loaded" + " since a student"
                    + " with the same pid exists.\n");
                continue;
            }

            else if (hashTable.bucketFull(loadedStudents[i].getPID())) {
                System.out.print("Warning: There is no free place in"
                    + " the bucket to load student " + loadedStudents[i]
                        .getPID() + " " + loadedStudents[i].getName().toString()
                    + "\n");
                continue;
            }

            else {
                try {
                    Prj4Handle nameh = memoryManager.insert(loadedStudents[i]
                        .getName().toString().getBytes("UTF-8"));
                    hashTable.insert(loadedStudents[i].getPID(), nameh);
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        lastStudentInserted = null;
    }


    // ----------------------------------------------------------------
    /**
     * Inserts a student into the hashtable (with the name being stored in the
     * memory manager). Essay can be called after a successful insert.
     * 
     * @param pid
     *            pid of student
     * @param firstName
     *            first name of student
     * @param lastName
     *            last name of student
     */
    public void insert(String pid, String firstName, String lastName) {
        Student student = new Student(String.valueOf(pid), firstName, lastName);
        if (hashTable.hasPID(student.getPID())) {
            System.out.print(student.getName().toString()
                + " insertion failed since the pid " + student.getPID()
                + " belongs to another student \n");
            lastStudentInserted = null;
            return;
        }
        if (hashTable.bucketFull(student.getPID())) {
            System.out.print(student.getName().toString()
                + " insertion failed. Attempt to insert in a full bucket. \n");
            lastStudentInserted = null;
            return;
        }
        try {
            Prj4Handle h = memoryManager.insert(student.getName().toString()
                .getBytes("UTF-8"));
            hashTable.insert(student.getPID(), h);
            System.out.print(student.getName().toString() + " inserted. \n");
            lastStudentInserted = student;

        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    // ----------------------------------------------------------------
    /**
     * Changes the name associated with this pid. Essay can be called after any
     * update.
     * 
     * @param pid
     *            pid of student
     * @param firstName
     *            first name of student
     * @param lastName
     *            last name of student.
     */
    public void update(String pid, String firstName, String lastName) {
        Student student = new Student(String.valueOf(pid), firstName, lastName);
        if (!hashTable.hasPID(student.getPID())) {
            insert(student.getPID(), firstName, lastName);
            return;
        }
        String fullName = hashTable.getFullNameRecord(student.getPID());
        if (fullName.equals(student.getName().toString())) {
            System.out.print("Student " + student.getPID() + " updated to "
                + student.getName().toString() + "\n");
            lastStudentInserted = student;
            return;
        }
        else if (hashTable.getEssayHandle(student.getPID()) != null) {
            String essay = hashTable.getEssayRecord(student.getPID());
            memoryManager.release(hashTable.getNameHandle(student.getPID()));
            memoryManager.release(hashTable.getEssayHandle(student.getPID()));
            hashTable.remove(student.getPID());
            try {
                Prj4Handle nameHandle;
                Prj4Handle essayHandle;
                nameHandle = memoryManager.insert(student.getName().toString()
                    .getBytes("UTF-8"));
                hashTable.insert(student.getPID(), nameHandle);
                essayHandle = memoryManager.insert(essay.getBytes("UTF-8"));
                hashTable.insertEssay(student.getPID(), essayHandle);
                System.out.print("Student " + student.getPID() + " updated to "
                    + student.getName().toString() + "\n");
                lastStudentInserted = student;

            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else {
            memoryManager.release(hashTable.getNameHandle(student.getPID()));
            hashTable.remove(student.getPID());
            try {
                Prj4Handle nameHandle = memoryManager.insert(student.getName()
                    .toString().getBytes("UTF-8"));
                hashTable.insert(student.getPID(), nameHandle);
                System.out.print("Student " + student.getPID() + " updated to "
                    + student.getName().toString() + "\n");
                lastStudentInserted = student;

            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    // ----------------------------------------------------------------
    /**
     * Add an essay to the last student inserted. Fails if used after any
     * command other than insert, update (or sometimes essayOn). Calling essayOn
     * after a successful essayOn is ok (keep the last one).
     * 
     * @param essay
     *            string representation of essay to be inserted.
     */
    public void essayOn(String essay) {
        if (lastStudentInserted == null) {
            System.out.print("essay commands can only follow"
                + " successful insert or update commands\n");
            return;
        }

        try {
            Prj4Handle essayHandle = hashTable.getEssayHandle(
                lastStudentInserted.getPID());
            if (essayHandle != null) {
                memoryManager.release(essayHandle);
            }
            Prj4Handle h = memoryManager.insert(essay.getBytes("UTF-8"));
            hashTable.insertEssay(lastStudentInserted.getPID(), h);
            System.out.print("essay saved for " + lastStudentInserted.getName()
                .toString() + "\n");
            lastStudentInserted = null;

        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    // ----------------------------------------------------------------
    /**
     * Removes the student and corresponding essay from the spot in the
     * hashtable corresponding to the given pid.
     * 
     * @param pid
     *            pid of student to remove.
     */
    public void remove(String pid) {
        Student std = new Student(String.valueOf(pid), "a", "b");
        if (!hashTable.hasPID(std.getPID())) {
            System.out.print(std.getPID() + " is not found in the database \n");
            lastStudentInserted = null;
            return;
        }

        Prj4Handle nameHandle = hashTable.getNameHandle(std.getPID());
        Prj4Handle essayHandle = hashTable.getEssayHandle(std.getPID());
        String fullName = hashTable.getFullNameRecord(std.getPID());
        memoryManager.release(nameHandle);
        if (essayHandle != null) {
            memoryManager.release(essayHandle);
        }
        hashTable.remove(std.getPID());
        System.out.print(std.getPID() + " with full name " + fullName
            + " is removed from the database. \n");
        lastStudentInserted = null;

    }


    // ----------------------------------------------------------------
    /**
     * Like remove, but only removes the essay (the name stays)
     * 
     * @param pid
     *            pid of student corresponding to the essay to rmeove.
     */
    public void clear(String pid) {
        Student std = new Student(String.valueOf(pid), "a", "b");
        if (!hashTable.hasPID(std.getPID())) {
            System.out.print(std.getPID() + " is not found in the database \n");
            lastStudentInserted = null;
            return;
        }

        Prj4Handle essayHandle = hashTable.getEssayHandle(std.getPID());
        String fullName = hashTable.getFullNameRecord(std.getPID());
        if (essayHandle == null) {
            System.out.print("record with pid " + std.getPID()
                + " with full name " + fullName + " is cleared. \n");
            lastStudentInserted = null;
            return;
        }
        memoryManager.release(essayHandle);
        hashTable.removeEssayHandle(std.getPID());
        System.out.print("record with pid " + std.getPID() + " with full name "
            + fullName + " is cleared. \n");
        lastStudentInserted = null;

    }


    // ----------------------------------------------------------------
    /**
     * prints out the information about the student who has the pid given
     * 
     * @param pid
     *            pid of student to search and print records for.
     */
    public void search(String pid) {
        Student std = new Student(String.valueOf(pid), "a", "b");
        if (!hashTable.hasPID(std.getPID())) {
            System.out.print("Search Failed: Couldnt find any student with ID "
                + std.getPID() + "\n");
            lastStudentInserted = null;
            return;
        }
        String fullName = hashTable.getFullNameRecord(std.getPID());
        String essay = hashTable.getEssayRecord(std.getPID());
        if (essay == null) {
            System.out.print(std.getPID() + " " + fullName + "\n");
            lastStudentInserted = null;
            return;
        }
        System.out.print(std.getPID() + " " + fullName + ":\n" + essay);
        lastStudentInserted = null;

    }


    // ----------------------------------------------------------------
    /**
     * Print out all records and free blocks.
     */
    public void print() {
        hashTable.print();
        lastStudentInserted = null;

    }
}
