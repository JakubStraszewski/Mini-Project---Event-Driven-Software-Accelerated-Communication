package miniproject.edsac;

/**
 * The EdsacEvent class represents the base for all EDSAC events. All events must possess an event ID by which the events are recognized by EDSAC components.
 * @deprecated Do not use this class directly, it is no longer used. Where possible the parameterized version should be used.
 **/
@Deprecated
public class EdsacEvent {
    /**
     * No event.
     */
    public static final int EVENT_NONE = 0;
    /**
     * An external component requested that this component terminates its operation.
     */
    public static final int EVENT_SHUTDOWN = 2;
    private int eventID = 0;

    /**Constructs a new EDSAC event object.
     *
     * @param eventID Event identifier.
     */
    public EdsacEvent(int eventID) {
        setEventID(eventID);
    }

    /**
     * Sets the event identifier on this event.
     * @param eventID Event identifier to set.
     */

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    /**
     * Retrieves the event identifier of this event.
     * @return The event identifier.
     */
    public int getEventID()
    {
        return eventID;
    }
    public String toString() {
        return "Event ID: " + eventID;
    }
}
