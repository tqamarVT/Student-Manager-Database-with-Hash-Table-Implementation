import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Parses commands from command file and calls respective functions from the
 * RealStudentManager.
 * 
 * @author Taimoor Qamar
 * @version 8/29/19
 *
 */
public class Parser {
    private RealStudentManager studentManager;
    private Scanner fileScanner;


    /**
     * 
     * @param fname
     *            name of the file to parse
     * @param stdmngr
     *            the class that will execute the commands given in the command
     *            file
     */
    public Parser(String fname, RealStudentManager stdmngr) {
        studentManager = stdmngr;
        try {
            fileScanner = new Scanner(new File(fname));
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found.");
            return;
        }
    }


    /**
     * Reads the file for commands and calls other methods to execute commands.
     * 
     * 
     */
    public void parseFile() {
        // System.out.println(new File(".").getAbsoluteFile());
        String line = null;
        while (fileScanner.hasNext()) { // maybe use hasNextLine() ?
            line = fileScanner.nextLine();
            parseLine(line);
        }
        fileScanner.close(); // hopefully good enough

    }


    /**
     * reads the line and calls the appropriate function from the
     * RealStudentManger class
     * 
     * @param line
     *            line of text from the file (includes command and arguments)
     */
    private void parseLine(String line) {
        Scanner lineScanner = new Scanner(line);
        // loadstudentdata <textfilename>
        // insert <pid><full name>
        // update <pid><full name>
        // essay on
        // <arbitrary length of paragraphs>
        // essay off
        // remove <pid#>
        // clear <pid#>
        // search <pid#>
        // print
        if (lineScanner.hasNext()) {
            String commandType = lineScanner.next();
            switch (commandType) { // hopefully the command will be lower-case
                case "loadstudentdata":
                    loadstudentdata(lineScanner);
                    break;
                case "insert":
                    insert(lineScanner); // different
                    break;
                case "update":
                    update(lineScanner);
                    break;
                case "essay":
                    essayOn(lineScanner); // complicated
                    break;
                case "remove":
                    remove(lineScanner); // different
                    break;
                case "clear":
                    clear(lineScanner);
                    break;
                case "search":
                    search(lineScanner);
                    break;
                case "print":
                    studentManager.print();
                    break;
                default:
                    System.out.println("Invalid command");
                    break;

            }
        }
        lineScanner.close();
    }


    /**
     * Reads arguments from the file using the passed Scanner and calls
     * RealStudentManager.loadstudentdata() with the arguments from the file.
     * 
     * @param lineScanner
     *            Scanner object at the position in the file after reading the
     *            command but before reading the arguments
     */
    public void loadstudentdata(Scanner lineScanner) {
        String arg1 = lineScanner.next();
        studentManager.loadStudentData(arg1);
    }


    /**
     * Reads arguments from the file using the passed Scanner and calls
     * RealStudentManager.insert() with the arguments from the file. Full name
     * will be parsed in this method before calling RealStudentManager.insert().
     * 
     * @param lineScanner
     *            Scanner object at the position in the file after reading the
     *            command but before reading the arguments
     */
    public void insert(Scanner lineScanner) {
        try {
            String arg1 = lineScanner.next();
            lineScanner.useDelimiter("\\s+"); // probably unnecessary
            String arg2 = lineScanner.next();
            String arg3 = lineScanner.next();
            studentManager.insert(arg1, arg2, arg3); // pid, first name, last
                                                     // namef
        }
        catch (NumberFormatException e) {
            return;
        }

    }


    /**
     * Reads arguments from the file using the passed Scanner and calls
     * RealStudentManager.update() with the arguments from the file. Full name
     * will be parsed in this method before calling RealStudentManager.update().
     * 
     * @param lineScanner
     *            Scanner object at the position in the file after reading the
     *            command but before reading the arguments
     */
    public void update(Scanner lineScanner) {
        try {
            String arg1 = lineScanner.next();
            lineScanner.useDelimiter("\\s+"); // probably unnecessary
            String arg2 = lineScanner.next();
            String arg3 = lineScanner.next();
            studentManager.update(arg1, arg2, arg3); // pid, first name, last
                                                     // name
        }
        catch (NumberFormatException e) {
            return;
        }
    }


    /**
     * Reads arguments from the file using the passed Scanner and calls
     * RealStudentManager.essayOn() with the arguments from the file.
     * 
     * @param lineScanner
     *            Scanner object at the position in the file after reading the
     *            command but before reading the arguments
     */
    public void essayOn(Scanner lineScanner) {
        lineScanner.nextLine(); // read until the end of the line containing
                                // essay on
        String essay = "";
        // read new lines until we see an essay off:
        boolean isEssayOff = false;
        do {
            String nextLine = fileScanner.nextLine();
            if (nextLine.split(" ").length >= 2) {
                String firstWord = nextLine.split(" ")[0].toLowerCase();
                String secondWord = nextLine.split(" ")[1].toLowerCase();
                isEssayOff = firstWord.equals("essay") && secondWord.equals(
                    "off");
            }
            if (!isEssayOff) { // you cant assume the essay
                               // wont be a 2 word essay
                               // like this

                isEssayOff = false;
                essay += nextLine + "\n"; // unsure if this is
                                          // platform-independent
// also, this adds an extra newline character at the end
            }
        }
        while (!isEssayOff);
        // unnecessary
        // essay = essay.substring(0, essay.length() - 1); // takes out the
        // extra
        // newline character
        studentManager.essayOn(essay);
    }


    /**
     * Reads arguments from the file using the passed Scanner and calls
     * RealStudentManager.remove() with the arguments from the file.
     * 
     * @param lineScanner
     *            Scanner object at the position in the file after reading the
     *            command but before reading the arguments
     */
    public void remove(Scanner lineScanner) {
        try {
            String arg1 = lineScanner.next();
            studentManager.remove(arg1);
        }
        catch (NumberFormatException e) {
            return;
        }

    }


    /**
     * Reads arguments from the file using the passed Scanner and calls
     * RealStudentManager.clear() with the arguments from the file.
     * 
     * @param lineScanner
     *            Scanner object at the position in the file after reading the
     *            command but before reading the arguments
     */
    public void clear(Scanner lineScanner) {

        try {
            String arg1 = lineScanner.next();
            studentManager.clear(arg1);
        }
        catch (NumberFormatException e) {
            return;
        }

    }


    /**
     * Reads arguments from the file using the passed Scanner and calls
     * RealStudentManager.search() with the arguments from the file.
     * 
     * @param lineScanner
     *            Scanner object at the position in the file after reading the
     *            command but before reading the arguments
     */
    public void search(Scanner lineScanner) {

        String arg2 = lineScanner.next();
        studentManager.search(arg2);

    }
}
