package miniproject.edsac;

/**
 * This is a software representation of the desired <a href="https://learn.microsoft.com/en-us/windows-hardware/drivers/gettingstarted/user-mode-and-kernel-mode">"kernel mode"</a> <a href = "https://www.techtarget.com/searchenterprisedesktop/definition/device-driver">device driver</a> which may be really useful for rendering if it was possible
 * to somehow write one directly and request that a connection would be formed by the operating system between the top-level application/ARG API and the GPU hardware.
 * This implementation is essentially intended to provide a very generalized cross-section of how such a driver would operate: It would schedule different Triangulator components to execute different drawing commands, and it will then update the top-level rasterization database.
 * In a real-life implementation this driver would most likely perform those operations through the operating system. Here we use a different type of calls to the OS, which allow the data to be shared between components through files rather than the restricted
 * kernel mode memory.
 * <strong>Notice: This is a <i>user-mode implementation</i> of a device driver which is intended to be implemented in kernel mode. Since I cannot find a method to directly access hardware through the kernel, all of the framework is software-simulated.</strong>
 * @Question <strong>I wonder if it is possible to directly manipulate <i></i> hardware from an application without any damage to the hardware components? I, the author of this framework, would really appreciate any guidance on the matter.</strong>
 * @author Jakub Straszewski, T00225338 (top-level framework); Sun Microsystems/Oracle Corporation (<a href="https://docs.oracle.com/javase/7/docs/api/java/io/package-summary.html">File and IO operations</a>)
 */

public class Driver {

    public static void main(String[] args) {
        DriverStatus status = new DriverStatus(DriverStatus.CODE_SUCCESS);
        status = SendEvent(new EdsacEventParameterized(0, new String[]{""}), false);
        EdsacEventParameterized globalEvent = new EdsacEventParameterized(0, new String[]{""});
        while (globalEvent.getEventID() != 2)
        {
            globalEvent = RetrieveEvent();
            switch (globalEvent.getEventID())
            {
                case EdsacEventParameterized.EVENT_NONE: {
                    break;
                }
                case EdsacEventParameterized.EVENT_DRAW_COMMAND: {
                    System.out.println("The driver received a drawing command. Scheduling triangulators...");
                    globalEvent = new EdsacEventParameterized(0, new String[]{""});
                    status = SendEvent(globalEvent, true);
                    if (status.getCode() != DriverStatus.CODE_SUCCESS)
                    {
                        System.out.println(status.toString());
                    }
                    status = SendEvent(new EdsacEventParameterized(1, new String[]{""}), false);
                    if (status.getCode() != DriverStatus.CODE_SUCCESS)
                    {
                        System.out.println(status.toString());
                    }
                    break;
                }
                case EdsacEventParameterized.EVENT_SHUTDOWN: {
                    System.out.println("The driver received a shutdown command. Shutting down...");
                    break;
                }
                case EdsacEventParameterized.EVENT_CLEAR_RASTER: {
                    System.out.println("The driver received a clear command. Clearing raster matrix...");
                    clearRasterMatrix(globalEvent.getParameters());
                    globalEvent = new EdsacEventParameterized(0, new String[]{""});
                    status = SendEvent(globalEvent, true);
                    if (status.getCode() != DriverStatus.CODE_SUCCESS)
                    {
                        System.out.println(status.toString());
                    }
                    status = SendEvent(new EdsacEventParameterized(1, new String[]{""}), false);
                    if (status.getCode() != DriverStatus.CODE_SUCCESS)
                    {
                        System.out.println(status.toString());
                    }
                    break;
                }
                case EdsacEventParameterized.EVENT_CLEAR_DEPTH: {
                    System.out.println("Component received a clearing command. Clearing depth matrix...");
                    clearDepthBuffer(globalEvent.getParameters());
                    globalEvent = new EdsacEventParameterized(0, new String[]{""});
                    status = SendEvent(globalEvent, true);
                    if (status.getCode() != DriverStatus.CODE_SUCCESS)
                    {
                        System.out.println(status.toString());
                    }
                    status = SendEvent(new EdsacEventParameterized(1, new String[]{""}), false);
                    if (status.getCode() != DriverStatus.CODE_SUCCESS)
                    {
                        System.out.println(status.toString());
                    }
                    break;
                }
                default:
                {
                    System.out.println("The driver received an invalid event (" + globalEvent.toString() + "), ignoring...");
                    globalEvent = new EdsacEventParameterized(0, new String[]{""});
                    status = SendEvent(globalEvent, true);
                    if (status.getCode() != DriverStatus.CODE_SUCCESS)
                    {
                        System.out.println(status.toString());
                    }
                }
            }
        }
    }
    /**
     * Sends an event from the ARGEDSAC device driver to the event database. The event-handling functions and classes follow the
     * WAB protocol. Unfortunately, the protocol is long and very dense. However, it is possible to contain it within a function to facilitate event handling.
     * @param event Event message to send.
     * @param clearing Specifies whether the function sends the event to the driver input. Used primarily for clearing
     * events which should no longer be executing (preventing a potentially infinite loop).
     * @return The status indicating whether the operation succeeded or failed. The status corresponds to one of the constants declared in the DriverStatus class.
     * @author Jakub Straszewski, T00225338 (top-level framework); Sun Microsystems/Oracle Corporation (<a href="https://docs.oracle.com/javase/7/docs/api/java/io/package-summary.html">File and IO operations</a>)
     */
    public static DriverStatus SendEvent(EdsacEventParameterized event, boolean clearing)
    {
        int recordType = 0;
        if (clearing)
            recordType = TDARGDatabaseAccess.RECORD_DRIVER_IN;
        else
            recordType = TDARGDatabaseAccess.RECORD_DRIVER_OUT;

        TDARGDatabaseAccess handle = new TDARGDatabaseAccess(recordType, 0, TDARGDatabaseAccess.OPEN_MODE_WRITE);
        try {
            handle.write(event);
        } catch (Exception e)
        {
            System.out.println(e.toString());
            return new DriverStatus(DriverStatus.CODE_FAILURE_EVENT);
        }
        return new DriverStatus(DriverStatus.CODE_SUCCESS);
    }

    /**
     * Sends an event to a triangulator component.
     * @param event The event to send.
     * @param index The index of the triangulator from which to retrieve the event.
     * @return The status indicating whether the operation succeeded or failed. The status corresponds to one of the constants declared in the DriverStatus class.
     */
    public static DriverStatus SendEventToTriangulator(EdsacEventParameterized event, int index)
    {

        TDARGDatabaseAccess handle = new TDARGDatabaseAccess(TDARGDatabaseAccess.RECORD_TRIANGULATOR_IN, 0, TDARGDatabaseAccess.OPEN_MODE_WRITE);
        try {
            handle.write(event);
        } catch (Exception e)
        {
            System.out.println(e.toString());
            return new DriverStatus(DriverStatus.CODE_FAILURE_EVENT);
        }
        return new DriverStatus(DriverStatus.CODE_SUCCESS);
    }

    /**
     * Receives an event from the event database. The event-handling functions and classes follow the
     * WAB protocol. Unfortunately, the protocol is long and very dense. However, it is possible to contain it within a function to facilitate event handling.
     * @return An EdsacEvent object containing the predefined event.
     * @author Jakub Straszewski, T00225338 (top-level framework); Sun Microsystems/Oracle Corporation (<a href="https://docs.oracle.com/javase/7/docs/api/java/io/package-summary.html">File and IO operations</a>)
     */
    public static EdsacEventParameterized RetrieveEvent()
    {
        TDARGDatabaseAccess handle = new TDARGDatabaseAccess(TDARGDatabaseAccess.RECORD_DRIVER_IN, 0, TDARGDatabaseAccess.OPEN_MODE_READ);
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

    /**
     * Retrieves an event from a triangulator component.
     * @param index The index of the triangulator from which to retrieve the event.
     * @return The status indicating whether the operation succeeded or failed. The status corresponds to one of the constants declared in the DriverStatus class.
     */
    public static EdsacEventParameterized RetrieveEventFromTriangulator(int index)
    {
        TDARGDatabaseAccess handle = new TDARGDatabaseAccess(TDARGDatabaseAccess.RECORD_TRIANGULATOR_OUT, 0, TDARGDatabaseAccess.OPEN_MODE_READ);
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

    /**
     * Processes the EVENT_CLEAR_RASTER event.
     * It converts the parameters into red, green and blue values for the pixels in the raster.
     * <strong>This method deals with extremely large memory - Requires a stack reserve size of at least 5MB.</strong>
     * @param args The arguments corresponding to the parameters member of the EdsacEventParameterized object from which the EVENT_CLEAR_RASTER
     * event was retrieved.
     * @return The status indicating whether the operation succeeded or failed. The status corresponds to one of the constants declared in the DriverStatus class.
     */
    public static DriverStatus clearRasterMatrix(String[] args)
    {
        //Pixel values
        final int a = 255; // Alpha (opaqueness level)
        int r, g, b; // Red, green and blue

        char[] rasterBuffer = new char[4000000];
        try {
            r = Integer.parseInt(args[0]);
            g = Integer.parseInt(args[1]);
            b = Integer.parseInt(args[2]);
        }
        catch (Exception e)
        {
            return new DriverStatus(DriverStatus.CODE_FAILURE_COMMAND);
        }
        // 32-bit BGRA pixel pattern for compatibility with graphics and images.
        for (int i = 0; i < 1000000; i++)
        {
            rasterBuffer[i * 4] = (char)b;
            rasterBuffer[i * 4+1] = (char)g;
            rasterBuffer[i * 4+2] = (char)r;
            rasterBuffer[i * 4+3] = (char)a;
        }
        TDARGDatabaseAccess databaseAccess = new TDARGDatabaseAccess(TDARGDatabaseAccess.RECORD_RASTER_MATRIX, 0, TDARGDatabaseAccess.OPEN_MODE_WRITE);
        try {
                databaseAccess.writeCharacterArray(rasterBuffer);

        } catch (Exception e) {
            return new DriverStatus(DriverStatus.CODE_FAILURE_COMMAND);
        }
        return new DriverStatus(DriverStatus.CODE_SUCCESS);
    }
    /**
     * Processes the EVENT_CLEAR_DEPTH event.
     * It converts the parameters into red, green and blue values for the pixels in the raster.
     * <strong>This method deals with extremely large memory - Requires a stack reserve size of at least 5MB.</strong>
     * References: <a href=https://docs.oracle.com/javase/tutorial/java/nutsandbolts/op3.html">Bit shifting</a> (for forming <a href="https://www.ibm.com/docs/en/aix/7.2?topic=types-signed-unsigned-integers">32-bit unsigned integers</a> from bytes).
     * @param args The arguments corresponding to the parameters member of the EdsacEventParameterized object from which the EVENT_CLEAR_DEPTH
     * event was retrieved.
     * @return The status indicating whether the operation succeeded or failed. The status corresponds to one of the constants declared in the DriverStatus class.
     */
    public static DriverStatus clearDepthBuffer(String[] args)
    {
        int depth;
        char[] depthBuffer = new char[4000000];
        try {
            depth = Integer.parseInt(args[0]);
        }
        catch (Exception e)
        {
            return new DriverStatus(DriverStatus.CODE_FAILURE_COMMAND);
        }

        //Divide the depth into 32 bits.

        for (int i = 0; i < 1000000; i++)
        {

            // Lower 16 bits
            depthBuffer[i * 4] = (char)((depth & 0xff)); // Bits 1-8
            depthBuffer[i * 4+1] = (char)((depth & 0xff00)>>8); // Bits 9-16

            // Upper 16 bits
            depthBuffer[i * 4+2] = (char)((depth & 0xff0000)>>16); // Bits 17-24
            depthBuffer[i * 4+3] = (char)((depth & 0xff000000)>>24); // Bits 25-32
        }
        TDARGDatabaseAccess databaseAccess = new TDARGDatabaseAccess(TDARGDatabaseAccess.RECORD_DEPTH_MATRIX, 0, TDARGDatabaseAccess.OPEN_MODE_WRITE);
        try {
            databaseAccess.writeCharacterArray(depthBuffer);
        } catch (Exception e) {
            return new DriverStatus(DriverStatus.CODE_FAILURE_COMMAND);
        }
        return new DriverStatus(DriverStatus.CODE_SUCCESS);
    }
}
