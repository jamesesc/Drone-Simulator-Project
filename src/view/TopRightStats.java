package view;

import Model.Drone;
import Model.TelemetryData;
import javafx.application.Platform;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class TopRightStats extends StackPane {
    /**
     * How many drone stats are we going to have to display in the top-right.
     * NOTE: Constant for now, but in the future I want to be able to adjust for
     * adjustable drone counts (in case we simulate a drone crashing or something).
     */
    private final int DRONE_COUNT = 4;
    /**
     * VBox containing the little boxes that show each drone's stats (holds myDroneBoxes).
     */
    private final VBox myLiveTelemetry;
    /**
     * The text area showing Drone statistics.
     */
    private final TextArea myStatsText;
    /**
     * Used by the method rightSwapPanel(), if true then show the big stats box.
     * Otherwise, show the little ones.
     */
    private boolean myShowingStats = false;
    /**
     * Map of all the small RegionBoxes for displaying their stats in the top-right.
     */
    private final Map<Integer, RegionBox> myDroneBoxes = new ConcurrentHashMap<>();
    /**
     * Record that makes our little stats boxes in the top-right.
     * NOTE: We may want to make this a separate class at some point.
     *
     * @param theId
     * @param theHeader
     * @param theTextArea
     * @param theContainer
     */
    private record RegionBox(int theId, Label theHeader, TextArea theTextArea, VBox theContainer) {
        RegionBox(int theID, String theTitle) {
            this(
                    theID,
                    new Label(theTitle),
                    new TextArea(),
                    new VBox()
            );

            theHeader.getStyleClass().add("box-header");

            theTextArea.setText("Battery:\nAltitude:\nPosition:");
            theTextArea.setWrapText(true);
            theTextArea.setEditable(false);
            theTextArea.setFocusTraversable(false);
            theTextArea.getStyleClass().add("dark-text-area");
            theTextArea.setFont(Font.font("Helvetica", 14));

            theContainer.setPrefHeight(120);
            theContainer.setMinHeight(Control.USE_PREF_SIZE);
            theContainer.setMaxHeight(Control.USE_PREF_SIZE);
            theContainer.getChildren().addAll(theHeader, theTextArea);

            theContainer.setOnMouseClicked(_ -> {
                Drone d = MonitorDash.getInstance().myDrones.get(theId);
                if (d != null) {
                    MonitorDash.getInstance().updateStatsTextLarge(d);
                    MonitorDash.getInstance().swapRightPanel(true);
                }
            });
        }

        void setText(String theText) {
            Platform.runLater(() -> theTextArea.setText(theText));
        }
    }


    TopRightStats() {
        HBox.setHgrow(this, Priority.NEVER);

        //Setup for the VBox that's going to hold our small drone stats boxes.
        myLiveTelemetry = new VBox();
        myLiveTelemetry.setPrefWidth(275);
        HBox.setHgrow(myLiveTelemetry, Priority.NEVER);
        myLiveTelemetry.getStyleClass().add("rounded-box");

        //Header for our main vbox
        Label header = new Label("Live Telemetry");
        header.getStyleClass().add("box-header");

        //VBox that holds each RegionBox
        VBox droneHolder = new VBox();
        droneHolder.setSpacing(10);

        //Make it so we can scroll through all our stats
        ScrollPane sPane = new ScrollPane();
        sPane.getStyleClass().add("dark-scroll-pane");
        sPane.setFitToHeight(true);
        sPane.setFitToWidth(true);
        sPane.setContent(droneHolder);
        VBox.setVgrow(sPane, Priority.ALWAYS);

        //Add a region box based on the number of drone boxes
        for (int i = 1; i <= DRONE_COUNT; i++) {
            RegionBox rb = new RegionBox(i, "Drone " + i);
            myDroneBoxes.put(i, rb);
            droneHolder.getChildren().add(rb.theContainer());
        }

        //Add children to our main top-right vbox
        myLiveTelemetry.getChildren().addAll(header, sPane);

        //Adjusting the big stats box
        myStatsText = new TextArea();
        myStatsText.setEditable(false);
        myStatsText.setWrapText(true);
        myStatsText.setVisible(false);
        myStatsText.getStyleClass().add("dark-text-area");
        myStatsText.setFont(Font.font("Helvetica", 14));
        myStatsText.setOnMouseClicked(_ -> swapRightPanel(false));

        //Makes it so the large stats box doesn't take up more width than it needs (bug fix)
        myStatsText.prefWidthProperty().bind(myLiveTelemetry.prefWidthProperty());

        //The pane that allows us to swap between big stats box and small stats boxes
        getChildren().addAll(myLiveTelemetry, myStatsText);
    }

    /**
     * Swaps between displaying the small stats boxes and the big stats box.
     *
     * @param theBigStatsBox True = show big stats box, False = show small stats boxes.
     */
    void swapRightPanel(boolean theBigStatsBox) {
        if (theBigStatsBox == myShowingStats) return;

        myShowingStats = theBigStatsBox;

        //Hide the correct box
        if (theBigStatsBox) {
            myLiveTelemetry.setVisible(false);
            myStatsText.setVisible(true);
        } else {
            myStatsText.setVisible(false);
            myLiveTelemetry.setVisible(true);
        }
    }

    /**
     * Update the large stats box at the top-right of the GUI.
     *
     * @param theDrone The drone whose data we are looking at.
     */
    void updateStatsTextLarge(final Drone theDrone) {
        if (theDrone == null || theDrone.getDroneTelemetry() == null) return;

        TelemetryData data = theDrone.getDroneTelemetry();

        String statsString = "Drone " + theDrone.getDroneID() +
                "\n==========================" +
                "\nBattery: " + theDrone.getBatteryLevel() +
                "\nAltitude: " + data.getAltitude() +
                "\nLatitude: " + data.getLatitude() +
                "\nLongitude: " + data.getLongitude() +
                "\nVelocity: " + data.getVelocity() +
                "\nOrientation: " + data.getOrientation() + "°";

        Platform.runLater(() -> myStatsText.setText(statsString));
        System.out.println("Working - MonitorDash - updateStatsTextLarge");
    }

    /**
     * Update the small stats boxes at the top right of the GUI.
     *
     * @param theDrone The drone whose data we want to display.
     */
    void updateStatsText(final Drone theDrone) {
        //If our drone or its data is null, return nothing
        if (theDrone == null || theDrone.getDroneTelemetry() == null) return;

        //Build a string for our little stats box to use
        StringBuilder statsString = new StringBuilder();
        TelemetryData data = theDrone.getDroneTelemetry();

        statsString.append("Battery: ").append(theDrone.getBatteryLevel());
        statsString.append("\nAltitude: ").append(data.getAltitude());
        statsString.append("\nPosition: ").append(data.getLatitude())
                .append(" ").append(data.getLongitude());

        //Set the text of the stats box w/ the matching ID of the drone
        Platform.runLater(() -> {
            //Get the box first
            var box = myDroneBoxes.get(theDrone.getDroneID());

            //Check if the box actually exists before using it
            if (box != null) {
                box.setText(statsString.toString());
            } else {
                System.out.println("Warning: Drone ID " + theDrone.getDroneID() + " not found in UI map.");
            }
        });
    }

    /**
     * Update the small stats boxes at the top right of the GUI.
     *
     * @param theDrones All the drones whose data we want to display.
     */
    void updateStatsText(final Drone[] theDrones) {
        //If the array is null do nothing
        if (theDrones == null) return;

        //For each drone, do the non-array equivalent of this function
        for (Drone drone : theDrones) {
            updateStatsText(drone);
        }
    }

    boolean getShowingStats() {
        return myShowingStats;
    }
}
