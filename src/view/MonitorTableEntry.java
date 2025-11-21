package view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class used by the Anomaly Table in the Graphical User Interface.
 * Used as an entry for each anomaly in the table.
 */
class MonitorTableEntry {
    /**
     * The timestamp of the anomaly.
     */
    private final StringProperty myTimestamp;
    /**
     * The ID of the drone which had an error.
     */
    private final StringProperty myDroneId;
    /**
     * What type of anomaly it is.
     */
    private final StringProperty myType;
    /**
     * The severity of the anomaly.
     */
    private final StringProperty mySeverity;
    /**
     * Any details you want to provide.
     */
    private final StringProperty myDetails;

    /**
     * Constructor for the MonitorTableEntry class.
     *
     * @param theTimestamp Timestamp of the anomaly.
     * @param theDroneId ID of drone with the anomaly.
     * @param theType The type of anomaly.
     * @param theSeverity The severity of the anomaly.
     * @param theDetails Any details you want to provide.
     */
    MonitorTableEntry(String theTimestamp, String theDroneId, String theType, String theSeverity, String theDetails) {
        myTimestamp = new SimpleStringProperty(theTimestamp);
        myDroneId = new SimpleStringProperty(theDroneId);
        myType = new SimpleStringProperty(theType);
        mySeverity = new SimpleStringProperty(theSeverity);
        myDetails = new SimpleStringProperty(theDetails);
    }

    // GETTERS FOR FIELDS
    StringProperty getTimestampProperty() {
        return myTimestamp;
    }
    StringProperty getDroneIdProperty() {
        return myDroneId;
    }
    StringProperty getTypeProperty() {
        return myType;
    }
    StringProperty getSeverityProperty() {
        return mySeverity;
    }
    StringProperty getDetailsProperty() {
        return myDetails;
    }
}