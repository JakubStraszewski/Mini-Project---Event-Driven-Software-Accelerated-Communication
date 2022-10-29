package miniproject.edsac;

/**
 * The EdsacEvent class represents the base for all EDSAC events. All events must possess an event ID by which the events are recognized by EDSAC components.
 **/

public class EdsacEvent {

    public static final int EVENT_NONE = 0;
    public static final int EVENT_SHUTDOWN = 2;
    private int eventID = 0;
    public EdsacEvent(int eventID) {
        setEventID(eventID);
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public int getEventID()
    {
        return eventID;
    }
    public String toString() {
        return "Event ID: " + eventID;
    }
}
