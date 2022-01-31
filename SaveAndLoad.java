import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Handles reading from .csv files
 * 
 * @author Taimoor Qamar
 * @version 9/28/19
 */
public class SaveAndLoad {
    private final int initialCapacity = 100000;
    private String filename;


    /**
     * Makes a new SaveAndLoad
     * 
     * @param fname
     *            the file name used for saving and loading
     */
    public SaveAndLoad(String fname) {
        filename = fname;

    }


    /**
     * Gives the filename associated with this SaveAndLoad
     * 
     * @return filename
     */
    public String getFilename() {
        return filename;
    }


    /**
     * Sets the filename to be newFileName
     * 
     * @param newFileName
     *            the String to become the file name.
     */
    public void setFilename(String newFileName) {
        filename = newFileName;
    }


    /**
     * Reads all of the student info from this SaveAndLoad's filename and
     * returns
     * an array of DetailedStudents with the corresponding information
     * 
     * @return an array of DetailedStudents with the corresponding
     *         information or null if error
     */
    public DetailedStudent[] loadStudentData() {
        ArrayList<DetailedStudent> studs = new ArrayList<>(initialCapacity);
        if (filename.contains(".csv")) {
            Scanner fileScanner = null;
            try {
                fileScanner = new Scanner(new File(filename));
            }
            catch (FileNotFoundException e) {
                System.out.println(".csv File not found to load student data");
                return null;
            }
            while (fileScanner.hasNext()) { // what about blank lines? Prob
                                            // won't happen
                String line = fileScanner.nextLine();
                // System.out.println(line);
                readLineForLSD(line, studs);
            }
            fileScanner.close();
        }
        else {
            System.out.println("Unable to read file to load student data");
            return null;
        }
        System.out.println(filename + " successfully loaded"); // won't test
        return studs.toArray(new DetailedStudent[1]);
    }


    /**
     * Reads a line of text for the load student data method and adds a new
     * DeatiledStudent with values from the text to studs
     * 
     * @param line
     *            a line of text
     * @param studs
     *            the list of DetailedStudents to which a new DetailedStudent
     *            will be added
     * @return
     *         the list of DetailedStudents after a new DetailedStudent is added
     */
    private ArrayList<DetailedStudent> readLineForLSD(
        String line,
        ArrayList<DetailedStudent> studs) {
        ArrayList<DetailedStudent> result = studs;
        Scanner lineScanner = new Scanner(line);
        lineScanner.useDelimiter("\\s*,\\s*");
        int pid = -1;
        String firstName = null;
        String middleName = null;
        String lastName = null;
        if (lineScanner.hasNext()) {
            pid = lineScanner.nextInt();
            firstName = lineScanner.next();
            middleName = lineScanner.next();
            lastName = lineScanner.next();
            lastName = lastName.trim();
            studs.add(new DetailedStudent(pid, firstName, middleName,
                lastName));
        }
        lineScanner.close();
        return result;
    }

}
© 2022 GitHub, Inc.
Terms
