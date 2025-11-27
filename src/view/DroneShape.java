package view;

import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

/**
 * Creates each individual shapes
 */
public class DroneShape extends Polygon {

    /**
     * Public constructor to create the drone shape
     */
    public DroneShape() {
        // All the points for an Arrow shape base on the center (0,0)
        // Format: X1, Y1, X2, Y2, X3, Y3...
        super(
                0.0, -15.0,  // Represent the Tip
                10.0, 10.0,  // Represent the Bottom Right
                0.0,  5.0,   // Represent like an indent on top
                -10.0, 10.0   // Represnt the Bottom Left
        );

        // Default Style
        setStrokeWidth(1);
        setStrokeType(StrokeType.INSIDE);

        // Ensure mouse clicks work on the shape ( only works by clicking the actual shape)
        setPickOnBounds(false);
    }

    /**
     * Helper to change color dynamically using Color objects.
     *
     * @param theColorSet is the color to set the drone shape.
     */
    public void setColor(final Color theColorSet) {
        setFill(theColorSet);
    }
}