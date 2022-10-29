package miniproject.edsac;

/**
 * The EdsacEventParameterized is a parameterized version of the EdsacEvent class. It allows for events to contain parameters.
 * It is primarily intended for drawing commands which usually contain parameters for location, orientation, scale, texture, etc. of a geometrical object
 * which is to be triangulated.
 */

public class EdsacEventParameterized extends EdsacEvent {
    private String[] parameters;

    public static final int EVENT_DRAW_COMMAND = 1;
    public static final int EVENT_CLEAR_RASTER = 3;
    public static final int EVENT_CLEAR_DEPTH = 4;
    public EdsacEventParameterized(int eventID, String[] parameters) {
        super(eventID);
        setParameters(parameters);
    }
    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }
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
