package miniproject.edsac;

/**
 * The DriverStatus class is responsible for error-handling operations in the <a href="https://learn.microsoft.com/en-us/windows-hardware/drivers/gettingstarted/user-mode-and-kernel-mode">"kernel mode"</a> device driver runtime. Objects of this class are the
 * primary signalling objects returned by operations invoked within the architecture of a typical "kernel mode" ARGEDSAC driver.
 * <strong>Notice: This is a <i>user-mode implementation</i> of a device driver which is intended to be implemented in kernel mode. Since I cannot find a method to directly access hardware through the kernel, all of the framework is software-simulated.<br>I wonder if it is possible to directly manipulate <i></i> hardware from an application without any damage to the hardware components?</strong>
 */

public class DriverStatus extends EdsacStatus {

    /** The constructor.
     *
     * @param code The code to set the status object to.
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
     * The application failed to send or receive an event.
     */
    public static final int CODE_FAILURE_EVENT = 4;

    public DriverStatus(int code) {
        super(code);
    }
    public String toString() {
        String output = "";
        switch (super.getCode())
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
