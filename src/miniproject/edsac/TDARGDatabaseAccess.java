package miniproject.edsac;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Represents a handle (view) to the TDARG database. Used in event handling and for modifying rasterized pixels.
 * The event-handling functions and classes follow the WAB protocol. Unfortunately, the protocol is long and very dense. However, it is possible to contain it within a function to facilitate event handling.
 * @author Jakub Straszewski, T00225338 (top-level framework); Sun Microsystems/Oracle Corporation (<a href="https://docs.oracle.com/javase/7/docs/api/java/io/package-summary.html">File and IO operations</a>; <a href="https://docs.oracle.com/javase/7/docs/api/java/io/IOException.html">Exception Handling</a>)
 */

public class TDARGDatabaseAccess {
    private boolean isOpen = false;
    private FileReader retrievingHandle = null;
    private FileWriter sendingHandle = null;
    private int openMode;
    private int databaseRecord;
    private int recordIndex;

    public static final int OPEN_MODE_READ = 1;
    public static final int OPEN_MODE_WRITE = 2;

    public static final int RECORD_DRIVER_IN = 1;
    public static final int RECORD_DRIVER_OUT = 2;
    public static final int RECORD_TRIANGULATOR_IN = 3;
    public static final int RECORD_TRIANGULATOR_OUT = 4;
    public static final int RECORD_RASTER_MATRIX = 5;
    public static final int RECORD_DEPTH_MATRIX = 6;

    /**
     * Constructs a new handle (view) to the TDARG database.
     * @param databaseRecord The type of record to obtain access to.
     * @param recordIndex The index of the record, in case multiple records of the same type exist. Used with triangulator data. Can be null for other record types.
     * @param openMode Specifies whether the record is to be written to or read from.
     */

    public TDARGDatabaseAccess(int databaseRecord, int recordIndex, int openMode) {
        this.databaseRecord = databaseRecord;
        this.recordIndex = recordIndex;
        this.openMode = openMode;
    }

    /**
     * Opens the handle to the TDARG database. After the handle is opened, input or output can be performed on the database record.
     * @return
     */

    public boolean open() {
        String pathname = "";
        switch (databaseRecord) {
            case RECORD_DRIVER_IN: {
                pathname = "src/driver_in";
                break;
            }
            case RECORD_DRIVER_OUT: {
                pathname = "src/driver_out";
                break;
            }
            case RECORD_TRIANGULATOR_IN: {
                pathname = "src/T" + recordIndex + "/T" + recordIndex + "_in";
                break;
            }
            case RECORD_TRIANGULATOR_OUT: {
                pathname = "src/T" + recordIndex + "/T" + recordIndex + "_out";
                break;
            }
            case RECORD_RASTER_MATRIX: {
                pathname = "src/rastermatrix";
                break;
            }
            case RECORD_DEPTH_MATRIX: {
                pathname = "src/depthmatrix";
                break;
            }
        }
        if (openMode == OPEN_MODE_WRITE) {
            try {
                sendingHandle = new FileWriter(pathname);
                isOpen = true;
            }
            catch (Exception e) {
                isOpen = false;
            }
            /* The WAB protocol implementation traditionally begins here - the calling object must block regardless until access to the TDARG database is attained.
            * Blocking must occur regardless of reason for failure to attain the access immediately.
            * Otherwise, multiple discrepancies may result. Synchronization is essential for accurate end results.
            */
            while (!isOpen)
            {
                try {
                    sendingHandle = new FileWriter(pathname);
                    isOpen = true;
                }
                catch (Exception e) {
                    isOpen = false;
                }
            }
        }
        else {
            try {
                retrievingHandle = new FileReader(pathname);
                isOpen = true;
            }
            catch (Exception e) {
                isOpen = false;
            }
            while (!isOpen)
            {
                try {
                    retrievingHandle = new FileReader(pathname);
                    isOpen = true;
                }
                catch (Exception e) {
                    isOpen = false;
                }
            }
        }
        return true;
    }

    private String readParameter() throws IOException {
        String parameter = "";
        char currentCharacter = 0;
        /* Read in a single character until a white space (' ') character is hit. Probably not the most efficient method of input.
        However, this is the only known method for this project.
        */
        currentCharacter = (char)retrievingHandle.read();
        while (currentCharacter != ' ') {
            parameter += currentCharacter;
            currentCharacter = (char)retrievingHandle.read();
        }
        return parameter;
    }

    public EdsacEventParameterized read() throws IOException {
        if (!isOpen)
            throw new IOException("An attempt was made to perform an IO operation on a closed handle.");

        if (openMode != OPEN_MODE_READ)
            throw new IOException("An attempt was made to read from a handle opened for writing.");

        int ID = 0;
        ID = retrievingHandle.read();
        // Input validation - The event identifier must be an integer with a value below 10
        if (ID < '0' || ID > '9')
            throw new IOException("Database error: An invalid identifier was read from a record.");
        else
            ID  -= '0'; //Subtract the value of the character '0' to convert to an integer if the ID is a valid integer between 0 and 9.
        String[] parameters;
        switch (ID)
        {
            case EdsacEventParameterized.EVENT_DRAW_COMMAND:
            {
                parameters = new String[8];
                for (int i = 0; i < 8; i++) {
                    parameters[i] = readParameter();
                }
                break;
            }
            case EdsacEventParameterized.EVENT_CLEAR_RASTER:
            {
                // Deliberate omission of 'break' - Fall through case
            }
            case EdsacEventParameterized.EVENT_CLEAR_DEPTH:
            {
                parameters = new String[3];
                for (int i = 0; i < 3; i++) {
                    parameters[i] = readParameter();
                }
                break;
            }
            default:
            {
                parameters = new String[1];
            }
        }
        return new EdsacEventParameterized(ID, parameters);
    }
}
