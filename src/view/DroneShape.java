package view;

import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

import java.util.Objects;

/**
 * Creates each of the drone individual shapes.
 *
 * @version Autumn 2025.
 */
public class DroneShape extends Polygon {
    /*-- Fields --*/

    /** Represent the Drone Shape Points */
    private static final double[] SHAPE_POINTS = {
            0.0, -20.0,  // Tip
            12.0, 8.0,   // Bottom Right
            0.0, 5.0,    // Indent (top center)
            -10.0, 10.0  // Bottom Left
    };


    /*-- Default Styling Constants --*/

    /** Default stroke color for the shape. */
    private static final Color DEFAULT_STROKE_COLOR = Color.BLACK;

    /** Default stroke width for the shape. */
    private static final double DEFAULT_STROKE_WIDTH = 1.0;

    /** Default stroke type for the shape. */
    private static final StrokeType DEFAULT_STROKE_TYPE = StrokeType.INSIDE;


    /*-- Constructor --*/

    /**
     * Public constructor to create the drone shape
     */
    public DroneShape() {
        super(SHAPE_POINTS);

        // Default Style
        setStroke(DEFAULT_STROKE_COLOR);
        setStrokeWidth(DEFAULT_STROKE_WIDTH);
        setStrokeType(DEFAULT_STROKE_TYPE);

        // Ensure mouse clicks work on the shape (only works by clicking the actual shape)
        setPickOnBounds(false);
    }


    /*-- Helper methods --*/

    /**
     * Helper to change color dynamically using Color objects.
     *
     * @param theColorSet is the color to set the drone shape.
     * @throws NullPointerException if theColorSet is null.
     */
    public void setColor(final Color theColorSet) {
        setFill(Objects.requireNonNull(theColorSet, "Color can't be null"));
    }
}