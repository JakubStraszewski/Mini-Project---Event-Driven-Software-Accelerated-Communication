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
     * @link References: <a href="https://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html">Runtime class</a>
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
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "java", "-cp", "\"C:\\Users\\jakub\\IdeaProjects\\Mini-Project - Event Driven Software Accelerated Communication\\src\"", "\"C:\\Users\\jakub\\IdeaProjects\\Mini-Project - Event Driven Software Accelerated Communication\\src\\miniproject\\edsac\\Triangulator.java\""});
        } catch (Exception e)
        {
            System.out.println("Unable to launch application:\n" + e.toString() + "\nIf using a different configuration for running the files than the one specified in IntelliJ, please modify the path(s) in the command so they indicate the necessary application(s).\nOtherwise please check if all files and directories reside in their original locations, as distributed.");
            return;
        }
        int currentEvent;
        System.out.println("Please enter an event from the following event table:\n0 = none\n1 = draw\n2 = shutdown");
        currentEvent = insc.nextInt();
        while (currentEvent != 2)
        {
            String[] parameters = new String[]{""};
            if (currentEvent == 1)
            {
                /* Prepare the parameters for the drawing event.
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
                // Inescapable WAB protocol again, as disliked as JTextArea ;-)
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
    public static void DebugDriver() {

    }

}
