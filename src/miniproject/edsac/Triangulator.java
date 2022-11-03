package miniproject.edsac;

/**
 * The Triangulator class is responsible for rendering triangles for geometry. It is a software analogy to the GPU components responsible for
 * rasterization. It takes in arguments from the event database portion which is assigned to it according to its identifier, then
 * uses the arguments to "triangulate" a single triangle. Multiple triangulators typically function together to rasterize the image
 * at a speed which is not slower than that necessary to rasterize the largest triangle (the triangle which is receives the largest
 * triangle typically spends most time at triangulating it. Note that the distance of the triangle from the "camera" or viewpoint
 * decreases the size of the triangle regardless of its actual size). Succinctly stating, the general action of the triangulator
 * is to produce the triangle's pixels in memory, wait until it can access the pixel database and set the correct pixels to the
 * correct color values in the raster matrix, and to the correct depth in the <a href="https://learnopengl.com/Advanced-OpenGL/Depth-testing">depth matrix (this link refers to the OpenGL implementation of depth buffers, but the logic is essentially the same in all cases)</a>.
 * <strong>This triangulator version is a template - involved only in development. T1, T2, T3, etc. will be the actually used classes.</strong>
 * Event-processing functions follow the Wait-and-Block (WAB) protocol which allows synchronization between components. Synchronization is essential for
 * accurate results.
 * <div style="border: 10px solid orange; background-color:#7f0000">
 * <h2>Essential information!</h2>
 * <p>The majority of this accomplishment is authored by either Jakub Straszewski (Student ID T00225338) or <a href="https://www.britannica.com/topic/Sun-Microsystems-Inc">Sun Microsystems</a>/<a href="https://www.oracle.com/ie/java/">Oracle Corporation.</a></p>
 * </div>
 *
 * <strong>Comments using HTML syntax can be viewed as HTML after hovering the cursor over the name of a class, method and other declarations</strong>
 */

public class Triangulator {
    public static void main(String[] args) {
        TriangulatorStatus status = new TriangulatorStatus(TriangulatorStatus.CODE_SUCCESS);

        EdsacEventParameterized currentEvent = new EdsacEventParameterized(0, new String[]{""}); // Apologies here for creating an array like an object - hopefully this is allowed in the assessment.
        SendEvent(new EdsacEventParameterized(0, new String[]{""}), false);
        while (currentEvent.getEventID() != EdsacEventParameterized.EVENT_SHUTDOWN) {
            currentEvent = RetrieveEvent();
            switch (currentEvent.getEventID())
            {
                case EdsacEventParameterized.EVENT_NONE:
                {
                    //Used for intense debugging only
                    //System.out.println("Component received no events. Continuing...");
                    break;
                }
                case EdsacEventParameterized.EVENT_DRAW_COMMAND:
                {
                    System.out.println("Component received drawing command. Triangulating...");
                    currentEvent.setEventID(0);
                    currentEvent.setParameters(new String[]{""});
                    status = SendEvent(currentEvent, true);
                    if (status.getCode() != TriangulatorStatus.CODE_SUCCESS)
                    {
                        System.out.println(status.toString());
                    }
                    status = SendEvent(new EdsacEventParameterized(1, new String[]{""}), false);
                    if (status.getCode() != TriangulatorStatus.CODE_SUCCESS)
                    {
                        System.out.println(status.toString());
                    }
                    break;
                }
                case EdsacEventParameterized.EVENT_SHUTDOWN:
                {
                    System.out.println("Component received shutdown command. Shutting down...");
                    break;
                }
                default:
                {
                    System.out.println("Component received an invalid event (" + currentEvent.toString() + "), ignoring...");
                    currentEvent.setEventID(0);
                    currentEvent.setParameters(new String[]{""});
                    status = SendEvent(currentEvent, true);
                    if (status.getCode() != TriangulatorStatus.CODE_SUCCESS)
                    {
                        System.out.println(status.toString());
                    }
                }
            }
        }
    }
    /**
     * Sends an event from the Triangulator to the event database. The event-handling functions and classes follow the
     * WAB protocol. Unfortunately, the protocol is long and very dense. However, it is possible to contain it within a function to facilitate event handling.
     * @param event Event message to send.
     * @param clearing Specifies whether the function sends the event to the triangulator input. Used primarily for clearing
     * events which should no longer be executing (preventing a potentially infinite loop).
     * @return The status indicating whether the operation succeeded or failed. The status corresponds to one of the constants declared in the TriangulatorStatus class.
     * @author Jakub Straszewski, T00225338 (top-level framework); Sun Microsystems/Oracle Corporation (<a href="https://docs.oracle.com/javase/7/docs/api/java/io/package-summary.html">File and IO operations</a>)
     */
    public static TriangulatorStatus SendEvent(EdsacEventParameterized event, boolean clearing)
    {
        int recordType = 0;
        if (clearing)
            recordType = TDARGDatabaseAccess.RECORD_TRIANGULATOR_IN;
        else
            recordType = TDARGDatabaseAccess.RECORD_TRIANGULATOR_OUT;

        TDARGDatabaseAccess handle = new TDARGDatabaseAccess(recordType, 0, TDARGDatabaseAccess.OPEN_MODE_WRITE);
        try {
            handle.write(event);
        } catch (Exception e)
        {
            System.out.println(e.toString());
            return new TriangulatorStatus(TriangulatorStatus.CODE_FAILURE_EVENT);
        }
        return new TriangulatorStatus(TriangulatorStatus.CODE_SUCCESS);
    }
    /**
     * Receives an event from the event database. The event-handling functions and classes follow the
     * WAB protocol. Unfortunately, the protocol is long and very dense. However, it is possible to contain it within a function to facilitate event handling.
     * @return An EdsacEvent object containing the predefined event.
     * @author Jakub Straszewski, T00225338 (top-level framework); Sun Microsystems/Oracle Corporation (<a href="https://docs.oracle.com/javase/7/docs/api/java/io/package-summary.html">File and IO operations</a>)
     */
    public static EdsacEventParameterized RetrieveEvent()
    {
        TDARGDatabaseAccess handle = new TDARGDatabaseAccess(TDARGDatabaseAccess.RECORD_TRIANGULATOR_IN, 0, TDARGDatabaseAccess.OPEN_MODE_READ);
        EdsacEventParameterized event;
        try {
            event = handle.read();
        } catch (Exception e) {
            System.out.println(e.toString());
            String[] lastResortParam = new String[1];
            return new EdsacEventParameterized(0, lastResortParam);
        }
        return event;
    }
}
