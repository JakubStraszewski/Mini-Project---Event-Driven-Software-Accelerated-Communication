package miniproject.edsac;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;

/**
 * The Triangulator class is responsible for rendering triangles for geometry. It is a software analogy to the GPU components responsible for
 * rasterization.
 * Template only - involved only in development.
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
        status.setCode(TriangulatorStatus.CODE_SUCCESS);
    }

    /**
     * Sends an event from the Triangulator to the event database. The event-handling functions follow the
     * WAB protocol. Unfortunately, the protocol is long and very dense. However, it is possible to contain it within a function to facilitate event handling.
     * @param event Event message to send, contained within a character array.
     * @param msgLen Quantity of characters in the event message.
     * @return The status indicating whether the operation succeeded or failed. The status corresponds to one of the constants declared in the TriangulatorStatus class.
     * @author Jakub Straszewski, T00225338 (top-level framework); Sun Microsystems/Oracle Corporation (<a href="https://docs.oracle.com/javase/7/docs/api/java/io/package-summary.html">File and IO operations</a>)
     */
    public static TriangulatorStatus SendEvent(char[] event, int msgLen)
    {
        return new TriangulatorStatus(TriangulatorStatus.CODE_SUCCESS);
    }
    /**
     * Receives an event from the event database. The event-handling functions follow the
     * WAB protocol. Unfortunately, the protocol is long and very dense. However, it is possible to contain it within a function to facilitate event handling.
     * @param buffer The character array to receive the message.
     * @param msgLen Quantity of characters to receive.
     * @return The status indicating whether the operation succeeded or failed. The status corresponds to one of the constants declared in the TriangulatorStatus class.
     * @author Jakub Straszewski, T00225338 (top-level framework); Sun Microsystems/Oracle Corporation (<a href="https://docs.oracle.com/javase/7/docs/api/java/io/package-summary.html">File and IO operations</a>)
     */
    public TriangulatorStatus RetrieveEvent(char[] buffer, int msgLen)
    {
        return new TriangulatorStatus(TriangulatorStatus.CODE_SUCCESS);
    }
}
