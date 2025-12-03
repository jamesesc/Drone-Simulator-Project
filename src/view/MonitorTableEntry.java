package view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MonitorTableEntry {
    private final StringProperty myTimestamp;
    private final StringProperty myDroneId;
    private final StringProperty myType;
    private final StringProperty myDetails;

    MonitorTableEntry(String theTimestamp, String theDroneId,
                      String theType, String theDetails) {
        myTimestamp = new SimpleStringProperty(theTimestamp);
        myDroneId = new SimpleStringProperty(theDroneId);
        myType = new SimpleStringProperty(theType);
        myDetails = new SimpleStringProperty(theDetails);
    }

    // Property getters with correct names + public
    public String getTimestamp() { return myTimestamp.get(); }
    public String getDroneId() { return myDroneId.get(); }
    public String getType() { return myType.get(); }
    public String getDetails() { return myDetails.get(); }
}
