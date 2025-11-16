package view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MonitorTableEntry {
    private final StringProperty myTimestamp;
    private final StringProperty myDroneId;
    private final StringProperty myType;
    private final StringProperty mySeverity;
    private final StringProperty myDetails;

    public MonitorTableEntry(String theTimestamp, String theDroneId, String theType, String theSeverity, String theDetails) {
        myTimestamp = new SimpleStringProperty(theTimestamp);
        myDroneId = new SimpleStringProperty(theDroneId);
        myType = new SimpleStringProperty(theType);
        mySeverity = new SimpleStringProperty(theSeverity);
        myDetails = new SimpleStringProperty(theDetails);
    }

    public StringProperty timestampProperty() {
        return myTimestamp;
    }

    public StringProperty droneIdProperty() {
        return myDroneId;
    }

    public StringProperty typeProperty() {
        return myType;
    }

    public StringProperty severityProperty() {
        return mySeverity;
    }

    public StringProperty detailsProperty() {
        return myDetails;
    }
}