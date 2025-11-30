package view;

import Model.Drone;
import Model.TelemetryData;
import controller.DroneFleetManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

class TopRightStats extends StackPane {

    // VBox uses in the Stats

    /**
     * VBox containing the little boxes that show each drone's stats (holds myDroneBoxes).
     */
    private final VBox myLiveTelemetry;
    /**
     * The text area showing Drone statistics.
     */
    private final VBox myLargeStatsView;
    /**
     * Represnt the box that contains all the drones stats.
     */
    private final VBox myDroneHolder;


    // FIELDS

    /**
     * Used by the method rightSwapPanel(), if true, then show the big stats box.
     * Otherwise, show the little ones.
     */
    private boolean myShowingStats = false;

    /**
     * Represent the user selected drone data stats.
     */
    private int mySelectedDroneID = -1;

    /**
     * Map of all the small RegionBoxes for displaying their stats in the top-right.
     */
    private final Map<Integer, VBox> myDroneBoxes = new ConcurrentHashMap<>();


    // Constant UI Design

    /**
     * Represent the UI big tech font.
     */
    private static final String FONT_UI = "Helvetica";
    /**
     * Represent the UI text section.
     */
    private static final String FONT_MONO = "Monospace";

    // The constructor for the TopRightStats class
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

        // Int that represent the # of pre-made small stats
        final int defaultCount =  3;

        // Looping through and making the # drone card inside the stat panel
        for (int i = 1; i <= defaultCount; i++) {
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

    /* ====================================
      Following methods to create either small stats, detail stats, and the stats value
     ====================================*/

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

        // Potential feature where if anomaly, will flash red
        String borderColor = false ? "#FF1744" : "#333333";

        // Styling the drone statsCardHolder data itself color
        statsCardHolder.getStyleClass().add("stats-card-holder");

        // Creating the Top section of the card (aka topRow) of the stats Card: Contains the Drone ID & Drone Status
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Creating and Styling the Drone card ID Label
        Label droneCardLabel = new Label(theDroneID);
        droneCardLabel.setFont(Font.font(FONT_UI, FontWeight.BOLD, 14));
        droneCardLabel.getStyleClass().add("stats-card-label");

        // The spacer between the Drone ID label and the Status label
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Setting up the Label for the drone
        Label droneStatusLabel = new Label("INACTIVE");
        String statusColor = getDroneStatusColor("INACTIVE");

        // Styling the Drone Status Label
        droneStatusLabel.setFont(Font.font(FONT_MONO, FontWeight.EXTRA_BOLD, 10));
        droneStatusLabel.getStyleClass().add("drone-status");
        droneStatusLabel.setStyle("-fx-background-color: " + statusColor);

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
            // Getting which drone card we got
            int droneId = Integer.parseInt(theDroneID.replace("DRONE-", ""));
            // Getting the drone info base on the drone card we click
            Drone droneClick = MonitorDash.getInstance().myDrones.get(droneId);
            // Only show details stats if such drone exist
            if (droneClick != null) {
                MonitorDash.getInstance().updateStatsTextLarge(droneClick);
                MonitorDash.getInstance().swapRightPanel(true);
                MonitorDash.getInstance().selectDroneOnMap(droneId);
            }
        });

        // Returning the new built statsCardHolder for the drone
        return statsCardHolder;
    }

    // Create a detailed card for large view

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

        // Add nodes to topRow
        topRow.getChildren().addAll(droneIDLabel, spacer, droneStatusLabel);

        // Stats Grid
        GridPane gridStats = new GridPane();
        gridStats.getStyleClass().add("stats-grid"); // optional: use CSS for spacing if needed
        gridStats.setHgap(15);
        gridStats.setVgap(15);

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
        card.setOnMouseClicked(_ -> MonitorDash.getInstance().swapRightPanel(false));

        return card;
    }

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
        statsLabel.setFont(Font.font(FONT_UI, 9));

        // Creating the Stats Value label on the right side
        Label statsValue = new Label(theValue);
        // Styling the stats value label
        statsValue.getStyleClass().add("stats-card-label");
        statsValue.setFont(Font.font(FONT_MONO, 11));

        // Adding both labels to the grid
        theGridStats.add(statsLabel, 0, theRow);
        theGridStats.add(statsValue, 1, theRow);
    }


    /* LOGIC: Switching Back and Forth Big & Small Data */

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
     */
    public void recreateDroneCards() {
        // Getting the Fleet Manager Controller
        DroneFleetManager fleetManger = MonitorDash.getInstance().getFleetManager();
        // Null safety check
        if (fleetManger == null) return;

        // Getting the drone array
        Drone[] currentFleet = fleetManger.getDroneFleet();

        // Running to clear, and updating the Drone Stats Card
        Platform.runLater(() -> {
            myDroneBoxes.clear();
            myDroneHolder.getChildren().clear();

            // Looping through to the # of drones in order to make the # of stats card to make
            for (Drone drone : currentFleet) {
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
                Label statusLabel = (Label) topRow.getChildren().get(2); // Index 2 is the Status Label

                // Only updating if changed to save performance
                if (!statusLabel.getText().equals(droneStatusStr)) {
                    statusLabel.setText(droneStatusStr);
                    statusLabel.setStyle("-fx-background-color: " + getDroneStatusColor(droneStatusStr) + ";");
                }


                // UPDATING THE STATS GRID (Bottom Row)
                GridPane grid = (GridPane) box.getChildren().get(2);

                // Update Battery
                ((Label) grid.getChildren().get(1)).setText(battery);
                // Update Altitude
                ((Label) grid.getChildren().get(3)).setText(altitude);
                // Update Speed
                ((Label) grid.getChildren().get(5)).setText(velocity);
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

    /** Helper method to get the status color of the drone status */
    private String getDroneStatusColor(String stateStr) {
        // Convert string back to Enum safely, or just check string values
        try {
            Drone.DroneState state = Drone.DroneState.valueOf(stateStr);
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

    /** Helper method to help refresh the detail card to update */
    private void refreshDetailedCard(Drone drone) {
        // Safety checks
        if (myLargeStatsView.getChildren().isEmpty()) return;

        // Get the VBox Card (It's the first child of myLargeStatsView)
        VBox card = (VBox) myLargeStatsView.getChildren().getFirst();

        TelemetryData data = drone.getDroneTelemetry();
        String droneStatusStr = drone.isDroneOn().toString();

        // 1. Update Status Header
        HBox topRow = (HBox) card.getChildren().getFirst();
        Label statusLabel = (Label) topRow.getChildren().get(2);

        if (!statusLabel.getText().equals(droneStatusStr)) {
            statusLabel.setText(droneStatusStr);
            statusLabel.setStyle("-fx-background-color: " + getDroneStatusColor(droneStatusStr) + "; ");
        }

        // 2. Update Grid Stats
        // Grid is index 2 (TopRow is 0, Separator is 1, Grid is 2)
        GridPane grid = (GridPane) card.getChildren().get(2);

        // Helper to update grid row safely
        updateGridLabel(grid, 0, drone.getBatteryLevel() + "%");
        updateGridLabel(grid, 1, String.valueOf(data.getAltitude()));
        updateGridLabel(grid, 2, String.valueOf(data.getLatitude()));
        updateGridLabel(grid, 3, String.valueOf(data.getLongitude()));
        updateGridLabel(grid, 4, String.valueOf(data.getVelocity()));
        updateGridLabel(grid, 5, data.getOrientation() + "°");
    }

    // Small helper to grab the Value Label (index 1) from a specific row
    private void updateGridLabel(GridPane grid, int row, String newValue) {
        /*
         In the grid, we added (Label, 0, row) and (Value, 1, row).
         The children's list is flat.
         If we added them strictly in order:
         Row 0: index 0 (Label), index 1 (Value)
         Row 1: index 2 (Label), index 3 (Value)
         Formula for Value index: (row * 2) + 1
        */

        int nodeIndex = (row * 2) + 1;
        if (nodeIndex < grid.getChildren().size()) {
            Label valueLabel = (Label) grid.getChildren().get(nodeIndex);
            valueLabel.setText(newValue);
        }
    }

    /**
     * Getter method whether showing stats
     *
     * @return true if it is showing Detail view stats, otherwise, false.
     */
    public boolean getShowingStats() {
        return myShowingStats;
    }

    void applyStylesheet(String cssName) {
        this.getStylesheets().clear();
        this.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource(cssName)).toExternalForm()
        );
    }
}
