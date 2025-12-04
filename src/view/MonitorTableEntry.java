package view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * An entry in a table (so far used in BottomTable and DatabasePopup)
 * used to represent an AnomalyRecord.
 *
 * This has been made a separate class for JavaFX reasons (we cannot
 * put the AnomalyRecords themselves into a JavaFX table)
 *
 * @version Autumn 2025
 */
class MonitorTableEntry {
    /**
     * A StringProperty representing the time of the anomaly
     */
    private final StringProperty myTimestamp;
    /**
     * The ID of the drone experiencing an anomaly
     */
    private final StringProperty myDroneId;
    /**
     * The type of anomaly being experienced
     */
    private final StringProperty myType;
    /**
     * Details about the anomaly
     */
    private final StringProperty myDetails;

    /**
     * Constructor for the MonitorTableEntry class
     *
     * @param theTimestamp The timestamp of this instance
     * @param theDroneId The drone ID of this instance
     * @param theType The type of this instance
     * @param theDetails The details of this instance
     */
    MonitorTableEntry(String theTimestamp, String theDroneId,
                      String theType, String theDetails) {
        myTimestamp = new SimpleStringProperty(theTimestamp);
        myDroneId = new SimpleStringProperty(theDroneId);
        myType = new SimpleStringProperty(theType);
        myDetails = new SimpleStringProperty(theDetails);
    }

    /**
     * Getter for the timestamp
     * @return The timestamp
     */
    String getTimestamp() { return myTimestamp.get(); }

    /**
     * Getter for the Drone ID
     * @return The drone ID
     */
    String getDroneId() { return myDroneId.get(); }

    /**
     * Getter for the Type
     * @return The type
     */
    String getType() { return myType.get(); }

    /**
     * Getter for the details
     * @return The details
     */
    String getDetails() { return myDetails.get(); }
}
