package view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

class MonitorTableEntry {
    private final StringProperty myTimestamp;
    private final StringProperty myDroneId;
    private final StringProperty myType;
    private final StringProperty mySeverity;
    private final StringProperty myDetails;

    MonitorTableEntry(String theTimestamp, String theDroneId, String theType, String theSeverity, String theDetails) {
        myTimestamp = new SimpleStringProperty(theTimestamp);
        myDroneId = new SimpleStringProperty(theDroneId);
        myType = new SimpleStringProperty(theType);
        mySeverity = new SimpleStringProperty(theSeverity);
        myDetails = new SimpleStringProperty(theDetails);
    }

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