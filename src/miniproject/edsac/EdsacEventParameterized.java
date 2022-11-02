package miniproject.edsac;

/**
 * The EdsacEventParameterized is a parameterized version of the EdsacEvent class. It allows for events to contain parameters.
 * It was originally intended for drawing commands which usually contain parameters for location, orientation, scale, texture, etc. of a geometrical object
 * which is to be triangulated. It is now the standard EDSAC event class used throughout the TDARG-EDSAC framework.
 */

public class EdsacEventParameterized extends EdsacEvent {
    private String[] parameters;

    public static final int EVENT_DRAW_COMMAND = 1;
    public static final int EVENT_CLEAR_RASTER = 3;
    public static final int EVENT_CLEAR_DEPTH = 4;

    /**
     * Constructs a new parameterized EDSAC event object.
     * @param eventID The event identifier.
     * @param parameters The event parameters.
     */
    public EdsacEventParameterized(int eventID, String[] parameters) {
        super(eventID);
        setParameters(parameters);
    }

    /**
     * Sets the parameters of this event.
     * @param parameters The parameters to set, contained in a String array object.
     */
    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }

    /**
     * Retrieves the parameters of this event.
     * @return The parameters of this event, contained in a String array object.
     */
    public String[] getParameters()
    {
        return parameters;
    }
    public String toString() {
        String output = "Event ID: " + getEventID() + " Parameters: ";
        for (int i = 0; i < parameters.length; i++)
        {
            output+=parameters[i];
        }
        return output;
    }
}
