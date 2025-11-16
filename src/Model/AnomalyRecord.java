package Model;

public class AnomalyRecord {
    /**
     * Type of anomaly we're getting
     */
    private String myType;
    /**
     * ID of the drone w/ an error, null if it's multiple drones.
     */
    private Integer myID;
    /**
     * Time that the error happened.
     */
    private double myTime;
    /**
     * Severity of the Anomaly
     */
    private String mySeverity;
    /**
     * Details about the anomaly
     */
    private String myDetails;

    private static final String EMPTY_STRING = "";

    //Constructors
    public AnomalyRecord() {
        myType = EMPTY_STRING;
        myID = null;
        mySeverity = EMPTY_STRING;
        myDetails = EMPTY_STRING;
        myTime = 0;
    }

    public AnomalyRecord(String theType, double theTime) {
        myType = theType;
        myID = null;
        myTime = theTime;
        mySeverity = EMPTY_STRING;
        myDetails = EMPTY_STRING;
    }

    public AnomalyRecord(String theType, int theID, double theTime) {
        myType = theType;
        myID = theID;
        myTime = theTime;
        mySeverity = EMPTY_STRING;
        myDetails = EMPTY_STRING;
    }

    public AnomalyRecord(String theType, double theTime,
                         String theSeverity, String theDetails) {
        myType = theType;
        myID = null;
        myTime = theTime;
        mySeverity = theSeverity;
        myDetails = theDetails;
    }

    public AnomalyRecord(String theType, int theID, double theTime,
                         String theSeverity, String theDetails) {
        myType = theType;
        myID = theID;
        myTime = theTime;
        mySeverity = theSeverity;
        myDetails = theDetails;
    }

    // Getters
    public String getType() { return myType; }
    public Integer getID() { return myID; }
    public double getTime() { return myTime; }
    public String getSeverity() { return mySeverity; }
    public String getDetails() { return myDetails; }

    // Setters
    public void setType(String theMethod) { myType = theMethod; }
    public void setID(int theID) { myID = theID; }
    public void setTime(double theTime) { myTime = theTime; }
    public void setSeverity(String theSeverity) { mySeverity = theSeverity; }
    public void setDetails(String theDetails) { myDetails = theDetails; }
}
