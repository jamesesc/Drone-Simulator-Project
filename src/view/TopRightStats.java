package view;

import Model.Drone;
import Model.TelemetryData;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import service.TimerManager;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that creates the stats screen for the simulation program.
 *
 * @version Autumn 2025
 */
public class TopRightStats extends StackPane {
    /*-- Constant UI Design --*/

    /** Represent the UI big tech font. */
    private static final String FONT_UI = "Helvetica";

    /** Represent the UI text section. */
    private static final String FONT_MONO = "Monospace";


    /*-- Default --*/

    /** Represent the default number of drones */
    private static final int DEFAULT_COUNT = 3;


    /*-- Grid layout indices --*/

    /** Layout indices inside VBox (TopRow → Separator → GridPane) --*/
    private static final int INDEX_GRID = 2;

    /** Position inside the HBox top row: [DroneID] [spacer] [StatusLabel] */
    private static final int POSITION_STATUS = 2;

    /** GridPane value column (label = 0, value = 1) */
    private static final int COLUMN_VALUE = 1;


    /*-- Grid row indices for the detail drone card --*/
    /** Row index for battery status. */
    private static final int ROW_BATTERY = 0;

    /** Row index for altitude data. */
    private static final int ROW_ALTITUDE = 1;

    /** Row index for latitude data. */
    private static final int ROW_LATITUDE = 2;

    /** Row index for longitude data. */
    private static final int ROW_LONGITUDE = 3;

    /** Row index for velocity data. */
    private static final int ROW_VELOCITY = 4;

    /** Row index for orientation data. */
    private static final int ROW_ORIENTATION = 5;


    /*-- Value indices for small card grid --*/

    /** Index for battery value. */
    private static final int VALUE_INDEX_BATTERY = 1;

    /** Index for altitude value. */
    private static final int VALUE_INDEX_ALTITUDE = 3;

    /** Index for speed value. */
    private static final int VALUE_INDEX_SPEED = 5;

    /*-- Font sizes --*/

    /** Font size for drone card label. */
    private static final int DRONE_CARD_LABEL_FONT_SIZE = 14;

    /** Font size for drone status label. */
    private static final int DRONE_STATUS_LABEL_FONT_SIZE = 10;

    /** Font size for statistics value. */
    private static final int STATS_VALUE_FONT_SIZE = 11;

    /** Font size for stat label. */
    private static final int STAT_LABEL_FONT_SIZE = 9;


    /*-- VBox uses in this display --*/

    /** VBox containing the little boxes that show each drone's stats (holds myDroneBoxes). */
    private final VBox myLiveTelemetry;

    /** The text area showing Drone statistics. */
    private final VBox myLargeStatsView;

    /** Represent the box that contains all the drones stats. */
    private final VBox myDroneHolder;


    /*-- Fields --*/

    /** Used by the method rightSwapPanel(), if true, then show the big stats box, Otherwise, show the little ones. */
    private boolean myShowingStats = false;

    /** Represent the user selected drone data stats. */
    private int mySelectedDroneID = -1;

    /** Map of all the small RegionBoxes for displaying their stats in the top-right. */
    private final Map<Integer, VBox> myDroneBoxes = new ConcurrentHashMap<>();

    /*-- Represent the Selected Drone Listener --*/
    private DroneSelectionListener mySelectionListener;

    /** Tracks the current simulation status */
    private TimerManager.Status mySimulationStatus = TimerManager.Status.STOPPED;


    /**
     * Constructor to create the right stats display.
     */
    public TopRightStats() {
        HBox.setHgrow(this, Priority.NEVER);

        //Setup for the VBox that's going to hold our small drone stats boxes.
        myLiveTelemetry = new VBox();
        myLiveTelemetry.setPrefWidth(275);
        myLiveTelemetry.setPadding(new Insets(15));
        myLiveTelemetry.setSpacing(15);
        HBox.setHgrow(myLiveTelemetry, Priority.NEVER);

        // Color Scheme for the Box
        myLiveTelemetry.getStyleClass().add("rounded-box");

        //Header for our main vbox
        Label header = new Label("Live Telemetry");
        header.getStyleClass().add("stats-header");

        //VBox that holds each RegionBox
        myDroneHolder = new VBox();
        myDroneHolder.setSpacing(10);

        // Make it so we can scroll through all our stats
        ScrollPane sPane = new ScrollPane();
        // Scroll Pane settings
        sPane.getStyleClass().add("stats-pane");
        sPane.setFitToHeight(true);
        sPane.setFitToWidth(true);
        sPane.setContent(myDroneHolder);
        VBox.setVgrow(sPane, Priority.ALWAYS);

        // Looping through and making the # drone card inside the stat panel
        for (int i = 1; i <= DEFAULT_COUNT; i++) {
            // Creating each stats card, with those default values
            VBox card = createDroneCard(
                    "DRONE-" + i
            );
            myDroneBoxes.put(i, card);
            myDroneHolder.getChildren().add(card);
        }

        //Add children to our main top-right vbox
        myLiveTelemetry.getChildren().addAll(header, sPane);

        //Adjusting the big stats box
        myLargeStatsView = new VBox();
        myLargeStatsView.setPrefWidth(275);
        myLargeStatsView.setVisible(false);
        myLargeStatsView.setPadding(new Insets(15));
        myLargeStatsView.setSpacing(10);
        myLargeStatsView.getStyleClass().add("rounded-box");

        //The pane that allows us to swap between big stats box and small stats boxes
        getChildren().addAll(myLiveTelemetry, myLargeStatsView);
    }


    /*-- Listener --*/

    /**
     * Method to set the theListener to the class.
     *
     * @param theListener represents the listener class.
     */
    public void setMySelectionListener(final DroneSelectionListener theListener) {
        mySelectionListener = theListener;
    }

    /*-- Following methods to create either small stats, detail stats, and the stats value --*/

    /**
     * Helper method that will create the card stats itself.
     *
     * @param theDroneID It displays the name for the drone.
     * @return A VBox containing the complete styled drone card.
     */
    private VBox createDroneCard(final String theDroneID) {
        // Creating the Drone Stats Card
        VBox statsCardHolder = new VBox(5);
        statsCardHolder.setPadding(new Insets(10));

        // Styling the drone statsCardHolder data itself color
        statsCardHolder.getStyleClass().add("stats-card-holder");

        // Creating the Top section of the card (aka topRow) of the stats Card: Contains the Drone ID & Drone Status
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Creating and Styling the Drone card ID Label
        Label droneCardLabel = new Label(theDroneID);
        droneCardLabel.setFont(Font.font(FONT_UI, FontWeight.BOLD, DRONE_CARD_LABEL_FONT_SIZE));
        droneCardLabel.getStyleClass().add("stats-card-label");

        // The spacer between the Drone ID label and the Status label
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Setting up the Label for the drone
        Label droneStatusLabel = new Label("INACTIVE");
        droneStatusLabel.setFont(Font.font(FONT_MONO, FontWeight.EXTRA_BOLD, DRONE_STATUS_LABEL_FONT_SIZE));
        droneStatusLabel.getStyleClass().add("drone-status");


        // Adding the DroneCardLabel, theSpacer, and the droneStatusLabel into one container
        topRow.getChildren().addAll(droneCardLabel, spacer, droneStatusLabel);


        // Creating the Stats itself

        // Using a gridStats so we can organize and line up the stats together
        GridPane gridStats = new GridPane();
        // Setting the gaps for the gridStats
        gridStats.getStyleClass().add("stats-grid");

        // Calling a method to create each stat to the gridStats
        addStatRow(gridStats, 0, "BATTERY : ", "0%");
        addStatRow(gridStats, 1, "ALTITUDE : ", "0m");
        addStatRow(gridStats, 2, "SPEED : ", "0 m/s");

        // Combining everything tof form the statsCardHolder: TopRow, Separator, GridStats
        statsCardHolder.getChildren().addAll(topRow, new Separator(), gridStats);

        // Event Action when click on the statsCardHolder, it will switch over to the detail view
        statsCardHolder.setOnMouseClicked(_ -> {
            if (mySimulationStatus == TimerManager.Status.STOPPED) {
                return;
            }

            int droneId = Integer.parseInt(theDroneID.replace("DRONE-", ""));
            if (mySelectionListener != null) {
                mySelectionListener.onDroneSelected(droneId);
            }
        });

        // Returning the new built statsCardHolder for the drone
        return statsCardHolder;
    }


    /*-- Create a detailed card for large view --*/

    /**
     * Creating a detail card for the theClickDrone we click on.
     *
     * @param theClickDrone represents the drone that we click.
     * @return a detail stats card base on the drone that we passed (aka we click on).
     */
    private VBox createDetailedDroneCard(final Drone theClickDrone) {
        if (theClickDrone == null || theClickDrone.getDroneTelemetry() == null) return null;

        TelemetryData data = theClickDrone.getDroneTelemetry();

        // Create main card VBox
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.getStyleClass().add("stats-card-detail");

        // Top Row: Drone ID + Status
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Drone ID Label
        Label droneIDLabel = new Label("DRONE-" + theClickDrone.getDroneID());
        droneIDLabel.getStyleClass().add("stats-card-label");

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Drone Status Label
        Label droneStatusLabel = new Label(theClickDrone.isDroneOn().toString());
        droneStatusLabel.getStyleClass().add("drone-status");
        droneStatusLabel.setStyle("-fx-background-color: " + getDroneStatusColor(theClickDrone.isDroneOn().toString()));

        // Add nodes to topRow
        topRow.getChildren().addAll(droneIDLabel, spacer, droneStatusLabel);

        // Stats Grid
        GridPane gridStats = new GridPane();
        gridStats.getStyleClass().add("stats-grid");
        gridStats.setHgap(15);
        gridStats.setVgap(5);

        // Add stat rows
        addStatRow(gridStats, 0, "BATTERY: ", theClickDrone.getBatteryLevel() + "%");
        addStatRow(gridStats, 1, "ALTITUDE: ", data.getAltitude() + " m");
        addStatRow(gridStats, 2, "LATITUDE: ", String.valueOf(data.getLatitude()));
        addStatRow(gridStats, 3, "LONGITUDE: ", String.valueOf(data.getLongitude()));
        addStatRow(gridStats, 4, "VELOCITY: ", data.getVelocity() + "m/s");
        addStatRow(gridStats, 5, "ORIENTATION: ", data.getOrientation() + "°");

        // Add topRow, separator, and grid to card
        card.getChildren().addAll(topRow, new Separator(), gridStats);

        // Click event to swap back
        card.setOnMouseClicked(_ -> {
            if (mySelectionListener != null) {
                mySelectionListener.onDroneSelected(-1); // -1 means deselect
            }
        });

        return card;
    }

    /*-- LOGIC: Switching Back and Forth Big & Small Data --*/

    /**
     * Swaps between displaying the small stats boxes and the big stats box.
     *
     * @param theBigStatsBox True = show big stats box, False = show small stats boxes.
     */
    public void swapRightPanel(boolean theBigStatsBox) {
        if (theBigStatsBox == myShowingStats) return;

        myShowingStats = theBigStatsBox;

        //Hide the correct box
        if (theBigStatsBox) {
            myLiveTelemetry.setVisible(false);
            myLargeStatsView.setVisible(true);

        } else {
            myLargeStatsView.setVisible(false);
            myLiveTelemetry.setVisible(true);
        }
    }


    /* LOGIC: Updating the Stats Card */

    /**
     * Used to clear the existing drone cards setup, and rebuilds them base on the # of drones is in FleetManager.
     *
     * @param theDroneFleet represent the array of drones to recreate for.
     */
    public void recreateDroneCards(final Drone[] theDroneFleet) {
        // Null safety check
        if (theDroneFleet == null) return;

        // Running to clear, and updating the Drone Stats Card
        Platform.runLater(() -> {
            myDroneBoxes.clear();
            myDroneHolder.getChildren().clear();

            // Looping through to the # of drones in order to make the # of stats card to make
            for (Drone drone : theDroneFleet) {
                VBox card = createDroneCard("DRONE-" + drone.getDroneID());
                myDroneBoxes.put(drone.getDroneID(), card);
                myDroneHolder.getChildren().add(card);
            }
        });
    }

    /**
     * Update the small stats boxes at the top right of the GUI.
     *
     * @param theDrone The drone whose data we want to display.
     */
    public void updateStatsText(final Drone theDrone) {
        //If our drone or its data is null, return nothing
        if (theDrone == null || theDrone.getDroneTelemetry() == null) return;

        // 1. Getting the data of the drone
        TelemetryData data = theDrone.getDroneTelemetry();
        String battery = theDrone.getBatteryLevel() + "%";
        String altitude = String.format("%.1f m", data.getAltitude());
        String velocity = String.format("%.1f m/s", data.getVelocity());

        // Getting the Enum value as a String
        String droneStatusStr = theDrone.isDroneOn().toString();

        // Running the Action to update the small stats card
        Platform.runLater(() -> {
            //Get the box first
            var box = myDroneBoxes.get(theDrone.getDroneID());

            //Check if the box actually exists before using it
            if (box != null) {
                // UPDATING THE STATUS LABEL (Top Row)
                HBox topRow = (HBox) box.getChildren().getFirst();
                Label statusLabel = (Label) topRow.getChildren().get(POSITION_STATUS); // Index 2 is the Status Label

                // Only updating if changed
                if (!statusLabel.getText().equals(droneStatusStr)) {
                    statusLabel.setText(droneStatusStr);
                    statusLabel.setStyle("-fx-background-color: " + getDroneStatusColor(droneStatusStr)); // ADD THIS LINE
                }


                // UPDATING THE STATS GRID (Bottom Row)
                GridPane grid = (GridPane) box.getChildren().get(INDEX_GRID);

                // Update Battery
                ((Label) grid.getChildren().get(VALUE_INDEX_BATTERY)).setText(battery);
                // Update Altitude
                ((Label) grid.getChildren().get(VALUE_INDEX_ALTITUDE)).setText(altitude);
                // Update Speed
                ((Label) grid.getChildren().get(VALUE_INDEX_SPEED)).setText(velocity);
            }

            // Only update the detail view if its in display, otherwise not
            if (myShowingStats && theDrone.getDroneID() == mySelectedDroneID) {
                refreshDetailedCard(theDrone);
            }
        });
    }

    /**
     * Update the large stats box at the top-right of the GUI.
     *
     * @param theDrone The drone whose data we are looking at.
     */
    public void updateStatsTextLarge(final Drone theDrone) {
        if (theDrone == null || theDrone.getDroneTelemetry() == null) return;

        // Save the ID so we know to update this specific drone later
        mySelectedDroneID = theDrone.getDroneID();

        Platform.runLater(() -> {
            VBox detailedCard = createDetailedDroneCard(theDrone);
            myLargeStatsView.getChildren().clear();
            myLargeStatsView.getChildren().add(detailedCard);
        });
    }

    /**
     * Getter method whether showing stats
     *
     * @return true if it is showing Detail view stats, otherwise, false.
     */
    public boolean getShowingStats() {
        return myShowingStats;
    }

    /**
     * Set the stylesheet to the display.
     *
     * @param theCSSName represent the class name in which to apply the display.
     */
    public void applyStylesheet(final String theCSSName) {
        this.getStylesheets().clear();
        this.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource(theCSSName)).toExternalForm()
        );
    }


    /*-- Helper Methods --*/

    /**
     * Helper method to help create each stat's label.
     *
     * @param theGridStats Is the gridPane the stat is being added too.
     * @param theRow The row index in the grid to add the stats to (base on 0-index)
     * @param theStatsLabel The Text that represents the stats
     * @param theValue The text for the value of the stats itself
     */
    private void addStatRow(final GridPane theGridStats, final int theRow,
                            final String theStatsLabel, final String theValue) {
        // Creating the Stats Label on the left side
        Label statsLabel = new Label(theStatsLabel);
        // Styling the stats label
        statsLabel.getStyleClass().add("stats-card-label");
        statsLabel.setFont(Font.font(FONT_UI, STAT_LABEL_FONT_SIZE));

        // Creating the Stats Value label on the right side
        Label statsValue = new Label(theValue);
        // Styling the stats value label
        statsValue.getStyleClass().add("stats-card-label");
        statsValue.setFont(Font.font(FONT_MONO, STATS_VALUE_FONT_SIZE));

        // Adding both labels to the grid
        theGridStats.add(statsLabel, 0, theRow);
        theGridStats.add(statsValue, 1, theRow);
    }

    /**
     * Helper method to get the status color of the drone status.
     *
     * @param theStatString represent the Stat in string.
     * @return the color base on the string.
     */
    private String getDroneStatusColor(final String theStatString) {
        // Convert string back to Enum safely, or just check string values
        try {
            Drone.DroneState state = Drone.DroneState.valueOf(theStatString);
            return switch (state) {
                case FLYING, TAKEOFF -> "#1B5E20"; // Dark Green
                case CHARGING, STARTING -> "#F57F17"; // Dark Orange/Yellow
                case LANDING -> "#B71C1C"; // Dark Red
                case INACTIVE -> "#424242"; // Gray for inactive
            };
        } catch (IllegalArgumentException e) {
            return "#333333";
        }
    }

    /**
     * Helper method to help refresh the detail card to update.
     *
     * @param theDrone represent the drone to refresh Detail card.
     */
    private void refreshDetailedCard(final Drone theDrone) {
        // Safety checks
        if (myLargeStatsView.getChildren().isEmpty()) return;

        // Get the VBox Card (It's the first child of myLargeStatsView)
        VBox card = (VBox) myLargeStatsView.getChildren().getFirst();

        TelemetryData data = theDrone.getDroneTelemetry();
        String droneStatusStr = theDrone.isDroneOn().toString();

        // 1. Update Status Header
        HBox topRow = (HBox) card.getChildren().getFirst();
        Label statusLabel = (Label) topRow.getChildren().get(POSITION_STATUS);

        if (!statusLabel.getText().equals(droneStatusStr)) {
            statusLabel.setText(droneStatusStr);
            statusLabel.setStyle("-fx-background-color: " + getDroneStatusColor(droneStatusStr) + "; ");
        }

        // 2. Update Grid Stats
        // Grid is index 2 (TopRow is 0, Separator is 1, Grid is 2)
        GridPane grid = (GridPane) card.getChildren().get(INDEX_GRID);

        // Helper to update grid row safely
        updateGridLabel(grid, ROW_BATTERY, theDrone.getBatteryLevel() + "%");
        updateGridLabel(grid, ROW_ALTITUDE, String.valueOf(data.getAltitude()));
        updateGridLabel(grid, ROW_LATITUDE, String.valueOf(data.getLatitude()));
        updateGridLabel(grid, ROW_LONGITUDE, String.valueOf(data.getLongitude()));
        updateGridLabel(grid, ROW_VELOCITY, String.valueOf(data.getVelocity()));
        updateGridLabel(grid, ROW_ORIENTATION, data.getOrientation() + "°");
    }

    /**
     * Small helper to grab the Value Label (index 1) from a specific row
     *
     * @param theGrid represent the grid stats.
     * @param theRow represent the row in the grid.
     * @param theNewValue represent the new value into that specific row.
     */
    private void updateGridLabel(final GridPane theGrid, final int theRow, final String theNewValue) {
        /*
         In the grid, we added (Label, 0, row) and (Value, 1, row).
         The children's list is flat.
         If we added them strictly in order:
         Row 0: index 0 (Label), index 1 (Value)
         Row 1: index 2 (Label), index 3 (Value)
         Formula for Value index: (row * 2) + 1
        */

        int nodeIndex = (theRow * 2) + COLUMN_VALUE;
        if (nodeIndex < theGrid.getChildren().size()) {
            Label valueLabel = (Label) theGrid.getChildren().get(nodeIndex);
            valueLabel.setText(theNewValue);
        }
    }

    /**
     * Updates the simulation status for this component.
     * This allows the stats panel to know when cards should be clickable.
     *
     * @param theStatus the current simulation status
     */
    public void updateSimulationStatus(final TimerManager.Status theStatus) {
        mySimulationStatus = theStatus;

        // If simulation is stopped, return to small view
        if (theStatus == TimerManager.Status.STOPPED && myShowingStats) {
            if (mySelectionListener != null) {
                mySelectionListener.onDroneSelected(-1);
            }
        }
    }
}
