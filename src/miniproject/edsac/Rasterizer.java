package miniproject.edsac;
import java.lang.Thread;

/**
 * Helper class for triangulation and rasterizing operations. Contains arrays containing the pixel coordinate vectors,
 * as well as mathematical methods, such as the dot product and cross product functions.
 * <strong style="background-color: red">Warning:  Uses a few dozen megabytes of stack - memory-wise, very expensive. Used for storing geometrical and color data.</strong>
 * @author Jakub Straszewski, T00225338 (Code)
 *
 */

public class Rasterizer {
    /**
     * Stores the raster matrix. Each char represents a color value (0-255).
     */
    private char[][] rasterMatrix = new char[4000][1000];

    /**
     * Stores the depth matrix. The type must be "char" rather than "int" in order to be compatible with the depth record in
     * the TDARG database, where depth is stored in byte-like form (an array of 32-bit unsigned integers). Each char represents the "2 to the power of the order of the byte" value
     * of the depth (char 1 represents the first 8 bits (bits 1-8) of the depth, char 2 represents the second 8 bits (bits 9-16), etc.)
     */

    private char[][] depthMatrix = new char[4000][1000];

    /**
     * Pixel X-coordinate array
     */

    private int[] pixelCoordsX = new int[1000000];
    /**
     * Pixel Y-coordinate array
     */
    private int[] pixelCoordsY = new int[1000000];
    /**
     * Pixel Z-coordinate array
     */
    private int[] pixelCoordsZ = new int[1000000];
    /**
     * Pixel U-coordinate array
     */
    private int[] pixelCoordsU = new int[1000000];
    /**
     * Pixel V-coordinate array
     */
    private int[] pixelCoordsV = new int[1000000];

    /**
     * Array specifying whether the current pixel is matched while drawing lines between pixels to fill a triangle.
     * Described further in the drawTriangle() method.
     */
    private boolean[] pixelCoordsMatched = new boolean[1000000];

    /**
     * Array index counter. Used for accessing and setting elements of the pixel coordinate arrays.
     */
    private int counter = 0;

    /**
     * Sets all the values in the raster matrix to the values of an input array.
     * @param matrix The input array containing the values to which the values in the raster matrix should be set.
     */

    public void setRasterMatrix(char[][] matrix)
    {
        rasterMatrix = matrix;
    }

    /**
     * Retrieves the raster matrix.
     * @return The raster matrix.
     */

    public char[][] getRasterMatrix() {
        return rasterMatrix;
    }

    /**
     * Sets all the values in the depth matrix to the values of an input array.
     * @param matrix The input array containing the values to which the values in the depth matrix should be set.
     */
    public void setDepthMatrix(char[][] matrix)
    {
        depthMatrix = matrix;
    }

    /**
     * Retrieves the depth matrix.
     * @return The depth matrix.
     */
    public char[][] getDepthMatrix() {
        return depthMatrix;
    }

    /**
     * Simple linear algorithm for computing a value between two values in a linear sequence or pattern.
     * Used for drawing lines, in particular.
     * @param val1 The first value of the linear sequence or pattern.
     * @param val2 The second value of the linear sequence or pattern.
     * @param min The starting point in the linear sequence or pattern.
     * @param current The current point in the linear sequence or pattern.
     * @param max Tha endpoint of the linear sequence or pattern.
     * @return A value between two values in the linear sequence or pattern.
     */

    private int computeLinear(int val1, int val2, int min, int current, int max) {
        int currentPoint = current-min;
        int maxPoint = max-min;
        return val1 + (val2 - val1) * currentPoint/maxPoint;
    }

    /**
     * Draws a line to the pixel coordinate arrays and increments the array index counter.
     * The algorithm of this method is borrowed from one of my previous C++ rendering applications from around May 2022.
     * @param xs The X-coordinates of the pixel. All coordinates are in pairs. No more than 2 must be specified.
     * @param ys The Y-coordinates of the pixel.
     * @param zs The Z-coordinates of the pixel.
     * @param us The U-coordinates (texture X-coordinates) of the pixel.
     * @param vs The V-coordinates (texture Y-coordinates) of the pixel.
     * @return The value of the counter at the end of the drawing operation.
     */

    public int drawLine(int[] xs, int[] ys, int[] zs, int[] us, int[] vs) {
        int xDifference = Math.abs(xs[1] - xs[0]);
        int yDifference = Math.abs(ys[1] - ys[0]);
        int zDifference = Math.abs(zs[1] - zs[0]);
        /* Find the largest difference and draw along it to minimize gaps in the line (drawing along smaller differences
         * will leave values of larger differences unhandled, which creates gaps in the line).
         */
        int currentDifference = Math.max(yDifference, zDifference);
        currentDifference = Math.max(currentDifference, xDifference);

        // Draw the line. Use the computeLinear() method to resolve the values between the two endpoints of the line.

        for (int i = 0; i < currentDifference; i++)
        {
            pixelCoordsX[counter] = computeLinear(xs[0], xs[1], 0, i, currentDifference);
            pixelCoordsY[counter] = computeLinear(ys[0], ys[1], 0, i, currentDifference);
            pixelCoordsZ[counter] = computeLinear(zs[0], zs[1], 0, i, currentDifference);
            pixelCoordsU[counter] = computeLinear(us[0], us[1], 0, i, currentDifference);
            pixelCoordsV[counter++] = computeLinear(vs[0], vs[1], 0, i, currentDifference); // Post-increment the counter.
        }
        return counter;
    }

    /**
     * A very complex function - writes the triangle to the pixel coordinates array. Involves abundant algorithms and mathematics.
     * @param xs An array specifying the x-coordinates of the vertices of the triangle.
     * @param ys An array specifying the y-coordinates of the vertices of the triangle.
     * @param zs An array specifying the z-coordinates of the vertices of the triangle.
     * @param us An array specifying the u-coordinates (texture x-coordinates) of the vertices of the triangle.
     * @param vs An array specifying the v-coordinates (texture y-coordinates) of the vertices of the triangle.
     * @param texture The name of the texture to apply to the surface of the triangle.
     * @param normalMap The name of the normal map to apply to the surface of the triangle.
     * @param specular The intensity of specular reflections of the surface of the triangle.
     * @param emissive Specifies whether the triangle should emit light. Surfaces which emit light are typically unaffected by
     * light or its absence.
     */

    public void drawTriangle(int[] xs, int[] ys, int[] zs, int[] us, int[] vs, String texture, String normalMap, int specular, boolean emissive) {
        /*
        * Transform the x and y coordinates into 3D perspective first using the corresponding z coordinates.
         */

        for (int i = 0; i < 3; i++)
        {
            xs[i] = (int)(500.0+(double)xs[i] / (1.0 + (double)zs[i]/100.0f));
            ys[i] = (int)(500.0+(double)ys[i] / (1.0 + (double)zs[i]/100.0f));
        }

        /*
        * Now draw the three sides of the triangle.
         */
        int edge1Counter = drawLine(new int[]{xs[0], xs[1]}, new int[]{ys[0], ys[1]}, new int[]{zs[0],zs[1]}, new int[]{us[0], us[1]}, new int[]{vs[0], vs[1]});
        int edge2Counter = drawLine(new int[]{xs[1], xs[2]}, new int[]{ys[1], ys[2]}, new int[]{zs[1],zs[2]}, new int[]{us[1], us[2]}, new int[]{vs[1], vs[2]});
        int edge3Counter = drawLine(new int[]{xs[2], xs[0]}, new int[]{ys[2], ys[0]}, new int[]{zs[2],zs[0]}, new int[]{us[2], us[0]}, new int[]{vs[2], vs[0]});

        /*
        * Fill the triangle in (with lines, unfortunately - no speedier method available yet!) by matching two points on
        * opposite sides of the triangle in a straight line.
        *
        * Typically, this search would work with the x or y coordinate. I decided to use the x coordinate for this
        * project.
        *
        * This is where the pixelCoordsMatched array is first used. The elements of the array with a value of "true"
        * correspond to the matched points on the triangle. The registering of the matched points is essential to avoid
        * superfluous drawing operations which could decrease performance while rendering the triangle.
         */

        //Process all points between the two points on the first edge.
        for (int i = 0; i < edge1Counter; i++)
        {
            // Automatically, the element of the pixelCoordsMatched array corresponding to the current point is set to "true"
            pixelCoordsMatched[i] = true;

            /* The matchingIndex variable is declared for holding the value of the index of the second point which matches
            * the first.
            */
            int matchingIndex = 0;

            /* Search through the pixel X-coordinate array.
            * Break when the first matching point is encountered.
             */

            //Search among all points between the first edge and the last edge.
            for (int j = edge1Counter; j < edge3Counter; j++)
            {
                //If the second point was not matched previously and is equal to the first point, the match is registered.
                if (!pixelCoordsMatched[j] && pixelCoordsX[j] == pixelCoordsX[i])
                {
                    //Ensure that the point is not used again superfluously.
                    pixelCoordsMatched[j] = true;
                    //Set the matchingIndex variable to the index of the matching point and break.
                    matchingIndex = j;
                    break;
                }
            }
            //Now, draw the line between the two matching points.
            drawLine(new int[]{pixelCoordsX[i], pixelCoordsX[matchingIndex]}, new int[]{pixelCoordsY[i], pixelCoordsY[matchingIndex]}, new int[]{pixelCoordsZ[i], pixelCoordsZ[matchingIndex]}, new int[]{pixelCoordsU[i], pixelCoordsU[matchingIndex]}, new int[]{pixelCoordsV[i], pixelCoordsV[matchingIndex]});
        }

        //Repeat the above steps with the unprocessed edges.

        // Search along the second edge.
        for (int i = edge1Counter; i < edge2Counter; i++)
        {
            // Automatically, the element of the pixelCoordsMatched array corresponding to the current point is set to "true"
            pixelCoordsMatched[i] = true;

            /* The matchingIndex variable is declared for holding the value of the index of the second point which matches
             * the first.
             */
            int matchingIndex = 0;

            /* Search through the pixel X-coordinate array.
             * Break when the first matching point is encountered.
             */

            /* Search along any potentially unhandled points, which can now only be along the last edge since the first
            * and second points are either being handled or have been handled by the first loop.
            */
            for (int j = edge2Counter; j < edge3Counter; j++)
            {
                //If the second point was not matched previously and is equal to the first point, the match is registered.
                if (!pixelCoordsMatched[j] && pixelCoordsX[j] == pixelCoordsX[i])
                {
                    //Ensure that the point is not used again superfluously.
                    pixelCoordsMatched[j] = true;
                    //Set the matchingIndex variable to the index of the matching point and break.
                    matchingIndex = j;
                    break;
                }
            }
            //Now, draw the line between the two matching points.
            drawLine(new int[]{pixelCoordsX[i], pixelCoordsX[matchingIndex]}, new int[]{pixelCoordsY[i], pixelCoordsY[matchingIndex]}, new int[]{pixelCoordsZ[i], pixelCoordsZ[matchingIndex]}, new int[]{pixelCoordsU[i], pixelCoordsU[matchingIndex]}, new int[]{pixelCoordsV[i], pixelCoordsV[matchingIndex]});
        }

        /*
        * Validate the UV (texture XY) coordinates of the pixels. The coordinates between 0 and 99 specify the locations
        * to map from the texture onto the surface of the triangle. Since I chose that the texture can be tiled for this project,
        * values exceeding the boundaries will be converted to values between 0 and 99, rather than being explicitly "clipped" to
        * 0 or 99.
         */

        /*The counter should have been incremented automatically to show the quantity of pixels to be filled in the triangle, so we can
        * simply validate for all the values at indices between 0 and the value currently specified by the counter.
        */
        for (int i = 0; i < counter; i++) {

            //If the coordinate is negative:
            if (pixelCoordsU[i] < 0)
            {
                /* Perform integer division by 100 upon the current pixels and subtract that to ensure the quantity is not below -100.
                */
                pixelCoordsU[i] = pixelCoordsU[i] - 100 * (pixelCoordsU[i]/100);
                /*Add the result to 100 (which will in most cases result in subtraction since the coordinate is expected to be less than 0).
                * One important observation is that if the result is a multiple of 100 (like 200, 300, etc.) the result will be
                * 0, which when added to 100 will result in exactly 100, which exceeds the boundary by 1 unit. This must be handled
                * by the next validation.
                 */
                pixelCoordsU[i] = 100 + pixelCoordsU[i];
            }
            /* If either the coordinate is greater than or equal to 100 or if the above validation resulted in the value of 100,
            * perform the following validation:
            */

            if (pixelCoordsU[i] >= 100) {
                /*Perform integer division by 100 upon the current pixels and subtract that. This ensures that the value never
                * exceeds 99.
                 */
                pixelCoordsU[i] = pixelCoordsU[i] - 100 * (pixelCoordsU[i]/100);
            }

            //Repeat the above steps on the V (texture Y) coordinate:

            if (pixelCoordsV[i] < 0)
            {

                pixelCoordsV[i] = pixelCoordsV[i] - 100 * (pixelCoordsV[i]/100);

                pixelCoordsV[i] = 100 + pixelCoordsV[i];
            }


            if (pixelCoordsV[i] >= 100) {

                pixelCoordsV[i] = pixelCoordsV[i] - 100 * (pixelCoordsV[i]/100);
            }
        }

        /* At this point we should be prepared to finalize the triangle by adding the appropriate effects, such as lighting and normal-mapping.
        * The following code involves a prodigious quantity of mathematical calculations and algorithms. This corresponds to the data entered
        * into a pixel or fragment shader in the standardized 3-dimensional graphics APIs such as Direct3D or OpenGL.
        */

        //First prepare the normal coordinates.

        //We do this by first of all creating two vectors, one for each of two edges from the triangle.

        int triangleEdge1X = xs[1] - xs[0];
        int triangleEdge1Y = ys[1] - ys[0];
        int triangleEdge1Z = zs[1] - zs[0];

        int triangleEdge2X = xs[2] - xs[1];
        int triangleEdge2Y = ys[2] - ys[1];
        int triangleEdge2Z = zs[2] - zs[1];

        double[] edge1Vector = new double[]{triangleEdge1X, triangleEdge1Y, triangleEdge1Z};
        double[] edge2Vector = new double[]{triangleEdge2X, triangleEdge2Y, triangleEdge2Z};

        // We then normalize the cross product of the two vectors, what should return the final result.

        double[] normalVector = normalize(cross(edge1Vector, edge2Vector));

        //Now establish the light position for illuminating the surface of the triangle.

        double[] lightPosition = new double[]{-1.0, 0.0, 0.0};

        // Normalize the light position.

        double[] lightPositionNormalized = normalize(lightPosition);

        /* The lightning factor (intensity of the illumination of the triangle surface) is obtained from the dot product of the
        * normal vector and the normalized light position vector.
        */

        double lightningFactor = dot(normalVector, lightPositionNormalized);

        for (int i = 0; i < counter; i++) {

        }
    }

    /**
     * Calculates the dot product of two vectors.
     * @param vector1 The first vector.
     * @param vector2 The second vector.
     * @return The dot product of the two vectors.
     */

    public static double dot(double[] vector1, double[] vector2) {
        return vector1[0] * vector2[0] + vector1[1] * vector2[1] + vector1[2] * vector2[2];
    }

    /**
     * Normalizes a vector. Useful for finding normals of a surface, along with the cross product.
     * @param vector The input vector.
     * @return The normalized vector.
     */

    public static double[] normalize(double[] vector)
    {
        double magnitude = Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
        return new double[]{vector[0] / magnitude, vector[1] /magnitude, vector[2] /magnitude};
    }

    /**
     * Calculates the cross product of two vectors.
     * @param vector1 The first vector.
     * @param vector2 The second vector.
     * @return The cross product of the two vectors.
     */

    public static double[] cross(double[] vector1, double[] vector2) {
        /*
        * This code is somewhat difficult to explain, but it is based on obtaining a component by multiplying
        * the components of the two vectors excluding the component which is to be obtained in the result, then
        * inverting the order of the vectors from which the components are obtained and subtracting this product from
        * the first. According to the nature of the cross product, this results in a vector perpendicular to both of
        * the input vectors.
         */
        double[] resultingVector = new double[3];
        resultingVector[0] = vector1[1] * vector2[2] - vector2[1] * vector1[2];
        resultingVector[1] = vector1[2] * vector2[0] - vector2[2] * vector1[0];
        resultingVector[2] = vector1[0] * vector2[1] - vector2[0] * vector1[1];
        return resultingVector;
    }

    /**
     * Resets the pixel coordinate index counter.
     * This method must not be forgotten to be called after the entire triangle is drawn.
     */

    public void resetCounter() {
        counter = 0;
    }
}
