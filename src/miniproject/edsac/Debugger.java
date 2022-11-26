package miniproject.edsac;

import java.util.Scanner;

public class Debugger {
    public static Scanner insc;
    public static void main(String[] args) {
        insc = new Scanner(System.in);
        String inputDebugOption;
        System.out.println("Would you like to debug the triangulator or the driver? (\"t\" or \"d\")");
        inputDebugOption = insc.nextLine();
        switch (inputDebugOption.charAt(0))
        {
            case 't':
            {
                DebugTriangulator();
                break;
            }
            case 'd':
            {
                DebugDriver();
                break;
            }
            default:
            {
                System.out.println("Invalid option, must be either \"t\" or \"d\"");
            }
        }
    }

    /** Debug the triangulator (template only) class.
     * The path for the application <strong>must</strong> be changed from C:\Users\jakub\IdeaProjects\Mini-Project - Event Driven Software Accelerated Communication\src\ to whatever path the project is located at
     * on your system.
     * @link References: <a href="https://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html">Runtime class</a>; <a href="https://docs.oracle.com/javase/7/docs/technotes/tools/windows/java.html">java.exe and commands</a>
     */
    public static void DebugTriangulator() {
        TDARGDatabaseAccess IOHandleOutput = new TDARGDatabaseAccess(TDARGDatabaseAccess.RECORD_TRIANGULATOR_IN, 0, TDARGDatabaseAccess.OPEN_MODE_WRITE), IOHandleInput = new TDARGDatabaseAccess(TDARGDatabaseAccess.RECORD_TRIANGULATOR_OUT, 0, TDARGDatabaseAccess.OPEN_MODE_READ);
        try {
            IOHandleOutput.write(new EdsacEventParameterized(0, new String[]{""}));
        } catch (Exception e) {
            System.out.println("Unable to initialize event database: " + e.toString());
            return;
        }
        try {
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "java", "-cp", "\"C:\\Users\\jakub\\IdeaProjects\\Mini-Project - Event Driven Software Accelerated Communication\\src\"", "miniproject/edsac/Triangulator"});
        } catch (Exception e)
        {
            System.out.println("Unable to launch application:\n" + e.toString() + "\nIf using a different configuration for running the files than the one specified in IntelliJ, please modify the path(s) in the command so they indicate the necessary application(s).\nOtherwise please check if all files and directories reside in their original locations, as distributed.");
            return;
        }
        int currentEvent;
        System.out.println("Please enter an event from the following event table:\n1 = draw\n2 = shutdown");
        currentEvent = insc.nextInt();
        while (currentEvent != 2)
        {
            String[] parameters = new String[]{""};
            if (currentEvent == 1)
            {
                /* Prepare the (dummy) parameters for the drawing event.
                 * A drawing event is expected to contain exactly 19 parameters:
                 *
                 * Parameters 0-2: X-coordinates of the vertices of the triangle to be drawn
                 * Parameters 3-5: Y-coordinates of the vertices of the triangle to be drawn
                 * Parameters 6-15: Z, U, and V-coordinates of the vertices of the triangle to be drawn (U and V
                 * coordinates represent the coordinates of the texture to be mapped onto the triangle)
                 * Parameter 16: Name of the texture to be mapped onto the triangle
                 * Parameter 17: Name of the normal map to be mapped onto the triangle (the normal map is used to create an effect
                 * of protuberances and bumpiness on a surface).
                 * Parameter 18: Specifies the intensity of specular reflections on the surface of the triangle.
                 * Parameter 19: Specifies whether the surface of the triangle emits light (has invariable brightness)
                 */
                parameters = new String[] {
                        "-100", "0", "100",
                        "-100", "100", "-100",
                        "0", "100", "200",
                        "0", "50", "100",
                        "0", "100", "0",
                        "Debug.texture",
                        "DebugNormal.texture",
                        "100", "false"
                };
            }

            try {
                boolean test = IOHandleOutput.write(new EdsacEventParameterized(currentEvent, parameters));
                if (!test)
                {
                    System.out.println("Error: Unable to write event. Please check the framework and path names.");
                    return;
                }
            }
            catch (Exception e)
            {
                System.out.println(e.toString());
            }
            if (currentEvent == 1)
            {
                //Wait until the triangulator returns. Keep checking the status in the database record for the triangulator output.
                int currentStatus = 0;
                EdsacEventParameterized readEvent = new EdsacEventParameterized(0, new String[]{""});
                try {
                    readEvent = IOHandleInput.read();
                    currentStatus = readEvent.getEventID();
                }
                catch (Exception e)
                {
                    System.out.println(e.toString());
                }
                // Inescapable WAB protocol again, as disliked as JTextArea :-)
                while (currentStatus != 1)
                {
                    try {
                        readEvent = IOHandleInput.read();
                    }
                    catch (Exception e)
                    {
                        System.out.println(e.toString());
                    }
                    currentStatus = readEvent.getEventID();
                }
                TDARGDatabaseAccess IOHandleInputForWriting = new TDARGDatabaseAccess(TDARGDatabaseAccess.RECORD_TRIANGULATOR_OUT, 0, TDARGDatabaseAccess.OPEN_MODE_WRITE);
                try {
                    IOHandleInputForWriting.write(new EdsacEventParameterized(0, new String[]{""}));
                } catch (Exception e)
                {
                    System.out.println(e.toString());
                }
            }
            System.out.println("Please enter an event from the following event table:\n0 = none\n1 = draw\n2 = shutdown");
            currentEvent = insc.nextInt();
        }
        try {
            IOHandleOutput.write(new EdsacEventParameterized(2, new String[]{""}));
        } catch (Exception e)
        {
            System.out.println("Warning: Unable to shut down triangulator: " + e.toString());
        }
        System.out.println("Exiting...");
    }
    /** Debug the driver class (with two triangulators).
     * The path for the application <strong>must</strong> be changed from C:\Users\jakub\IdeaProjects\Mini-Project - Event Driven Software Accelerated Communication\src\ to whatever path the project is located at
     * on your system.
     * @link References: <a href="https://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html">Runtime class</a>; <a href="https://docs.oracle.com/javase/7/docs/technotes/tools/windows/java.html">java.exe and commands</a>
     */
    public static void DebugDriver() {
        TDARGDatabaseAccess IOHandleInput = new TDARGDatabaseAccess(TDARGDatabaseAccess.RECORD_DRIVER_OUT, 0, TDARGDatabaseAccess.OPEN_MODE_READ), IOHandleOutput = new TDARGDatabaseAccess(TDARGDatabaseAccess.RECORD_DRIVER_IN, 0, TDARGDatabaseAccess.OPEN_MODE_WRITE), IOHandleInputForWriting = new TDARGDatabaseAccess(TDARGDatabaseAccess.RECORD_DRIVER_OUT, 0,  TDARGDatabaseAccess.OPEN_MODE_WRITE);
        try {
            IOHandleOutput.write(new EdsacEventParameterized(0, new String[]{""}));
        } catch (Exception e)
        {
            System.out.println("Unable to initialize event database: " + e.toString());
            return;
        }
        try {
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "java", "-cp", "\"C:\\Users\\jakub\\IdeaProjects\\Mini-Project - Event Driven Software Accelerated Communication\\src\"", "miniproject/edsac/Driver"});
        } catch (Exception e)
        {
            System.out.println("Unable to launch application:\n" + e.toString() + "\nIf using a different configuration for running the files than the one specified in IntelliJ, please modify the path(s) in the command so they indicate the necessary application(s).\nOtherwise please check if all files and directories reside in their original locations, as distributed.");
            return;
        }
        int currentEvent;
        System.out.println("Please enter an event from the following event table:\n0 = none\n1 = draw\n2 = shutdown\n3 = Clear raster matrix\n4 = clear depth matrix");
        currentEvent = insc.nextInt();
        while (currentEvent != 2)
        {
            String[] parameters = new String[]{""};
            if (currentEvent == 1)
            {
                /* Prepare the (dummy) parameters for the drawing event.
                 * A drawing event is expected to contain exactly 19 parameters:
                 *
                 * Parameters 0-2: X-coordinates of the vertices of the triangle to be drawn
                 * Parameters 3-5: Y-coordinates of the vertices of the triangle to be drawn
                 * Parameters 6-15: Z, U, and V-coordinates of the vertices of the triangle to be drawn (U and V
                 * coordinates represent the coordinates of the texture to be mapped onto the triangle)
                 * Parameter 16: Name of the texture to be mapped onto the triangle
                 * Parameter 17: Name of the normal map to be mapped onto the triangle (the normal map is used to create an effect
                 * of protuberances and bumpiness on a surface).
                 * Parameter 18: Specifies the intensity of specular reflections on the surface of the triangle.
                 * Parameter 19: Specifies whether the surface of the triangle emits light (has invariable brightness)
                 *
                 * The semicolons are used for each parameter to the driver mark the end of a value to be passed into one scheduled triangulator.
                 * When the driver encounters a semicolon, it parses an argument to the triangulator drawing event using the substring before the semicolon.
                 * After the semicolon, it keeps reading until it encounters the end of the string. It will then schedule the triangulators based on the count of
                 * the arguments (semicolons encountered in the parameter string).
                 */
                parameters = new String[] {
                        "-100;0;", "0;0;", "100;0;",
                        "-100;-100;", "100;100;", "-100;-100;",
                        "0;-100;", "0;0;", "0;100;",
                        "0;0;", "50;50;", "100;100;",
                        "0;0;", "100;100;", "0;0;",
                        "Debug.texture;Debug.texture;",
                        "DebugNormal.texture;DebugNormal.texture;",
                        "100;100;", "false;false;"
                };
            }
            else if (currentEvent == 3)
            {
                /* Prepare the (dummy) parameters for the raster-clearing event.
                 * A raster-clearing event is expected to contain exactly 3 parameters:
                 * Parameters 0-2: The red, green and blue values of the color to which to set the raster (pixel) matrix.
                 */
                parameters = new String[] {
                        "0", "127", "255" // Clear to a light-blue (red = 0; green = 0.5; blue = 1.0) color.
                };
            }
            else if (currentEvent == 4)
            {
                /* Prepare the (dummy) parameters for the depth-clearing event.
                 * A raster-clearing event is expected to contain only 1 parameter:
                 * Parameter 0: The depth to which to set all the values in the depth (z-buffer) matrix.
                 */
                parameters = new String[] {
                        "1000" // Clear to a depth of 1000 units (pixels, which are abstract since an image can only have actual pixels along the horizontal and vertical axes).
                };
            }

            try {
                boolean test = IOHandleOutput.write(new EdsacEventParameterized(currentEvent, parameters));
                if (!test)
                {
                    System.out.println("Error: Unable to write event. Please check the framework and path names.");
                    return;
                }
            }
            catch (Exception e)
            {
                System.out.println(e.toString());
            }
            if (currentEvent == 1 || currentEvent == 3 || currentEvent == 4)
            {
                //Wait until the driver returns. Keep checking the status in the database record for the driver output.
                int currentStatus = 0;
                EdsacEventParameterized readEvent = new EdsacEventParameterized(0, new String[]{""});
                try {
                    readEvent = IOHandleInput.read();
                    currentStatus = readEvent.getEventID();
                }
                catch (Exception e)
                {
                    System.out.println(e.toString());
                }
                /* WAB protocol - block until event is read (in fact, a recursive WAB protocol, as there is also a bottom-level WAB protocol
                * implemented within the read() method of the TDARGDatabaseAccess class).
                */
                while (currentStatus != 1)
                {
                    try {
                        readEvent = IOHandleInput.read();
                    }
                    catch (Exception e)
                    {
                        System.out.println(e.toString());
                    }
                    currentStatus = readEvent.getEventID();
                }
                try {
                    IOHandleInputForWriting.write(new EdsacEventParameterized(0, new String[]{""}));
                } catch (Exception e)
                {
                    System.out.println(e.toString());
                }
            }
            System.out.println("Please enter an event from the following event table:\n0 = none\n1 = draw\n2 = shutdown\n3 = Clear raster matrix\n4 = clear depth matrix");
            currentEvent = insc.nextInt();
        }
        try {
            IOHandleOutput.write(new EdsacEventParameterized(2, new String[]{""}));
        } catch (Exception e)
        {
            System.out.println("Warning: Unable to shut down triangulator: " + e.toString());
        }
        System.out.println("Exiting...");
    }
}
