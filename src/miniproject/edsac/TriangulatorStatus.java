package miniproject.edsac;

/**
 * The TriangulatorStatus class is responsible for error-handling operations. Objects of this class are the primary signalling objects returned
 * by operations invoked within the architecture of a typical "GPU" triangulator.
 */

public class TriangulatorStatus {
    /**
     * The operation succeeded.
     */
    public static final int CODE_SUCCESS = 0;
    /**
     * An error was encountered during component initialization.
     */
    public static final int CODE_FAILURE_INITIALIZATION = 1;
    /**
     * An error was encountered while processing a drawing command.
     */
    public static final int CODE_FAILURE_COMMAND = 2;
    /**
     * The application invoked a hard or insecure shutdown procedure.
     */
    public static final int CODE_FAILURE_SHUTDOWN = 3;
    /**
     * The application invoked a hard or insecure shutdown procedure.
     */
    public static final int CODE_FAILURE_EVENT = 4;
    /**
     * The triangulator code.
     */
    private int code = CODE_SUCCESS;

    /** The constructor.
     *
     * @param code The code to set the status object to.
     */
    public TriangulatorStatus(int code) {
        setCode(code);
    }

    /** Setter method - sets the (error) code to the argument.
     *
     * @param code The code to set the status object to.
     */

    public void setCode(int code) {
        this.code = code;
    }

    /** Getter method - retrieves the (error) code.
     *
     * @return The status represented by the object.
     */
    public int getCode() {
        return code;
    }

    public String toString() {
        String output = "";
        switch (code)
        {
            case CODE_FAILURE_INITIALIZATION:
            {
                output = "CODE_FAILURE_INITIALIZATION: An error was encountered during component initialization.";
                break;
            }
            case CODE_FAILURE_COMMAND:
            {
                output = "CODE_FAILURE_COMMAND: An error was encountered while processing a drawing command.";
                break;
            }
            case CODE_FAILURE_SHUTDOWN:
            {
                output = "CODE_FAILURE_SHUTDOWN: The application invoked a hard or insecure shutdown procedure.";
            }
            case CODE_FAILURE_EVENT:
            {
                output = "CODE_FAILURE_EVENT: The application failed to send or receive an event.";
            }
            default:
            {
                output = "CODE_SUCCESS: The operation succeeded.";
            }
        }
        return output;
    }
}
