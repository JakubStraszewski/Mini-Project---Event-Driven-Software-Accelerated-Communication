package miniproject.edsac;

/**
 * The base class used for error-handling operations. Objects inheriting from this class are the primary signalling objects returned
 * by operations invoked within the architecture of the ARGEDSAC framework.
 */

public class EdsacStatus {
    private int code;

    /** The constructor.
     *
     * @param code The code to set the status object to.
     */
    public EdsacStatus(int code) {
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
        return "Event code: " + getCode();
    }
}
