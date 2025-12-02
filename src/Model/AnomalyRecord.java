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
     * Details about the anomaly
     */
    private String myDetails;

    private static final String EMPTY_STRING = "";

    //Constructors
    public AnomalyRecord() {
        myType = EMPTY_STRING;
        myID = null;
        myDetails = EMPTY_STRING;
        myTime = 0;
    }

    public AnomalyRecord(String theType, double theTime) {
        myType = theType;
        myID = null;
        myTime = theTime;
        myDetails = EMPTY_STRING;
    }

    public AnomalyRecord(String theType, int theID, double theTime) {
        myType = theType;
        myID = theID;
        myTime = theTime;
        myDetails = EMPTY_STRING;
    }

    public AnomalyRecord(String theType, int theID, double theTime,
                         String theDetails) {
        myType = theType;
        myID = theID;
        myTime = theTime;
        myDetails = theDetails;
    }

    // Getters
    public String getType() { return myType; }
    public Integer getID() { return myID; }
    public double getTime() { return myTime; }
    public String getDetails() { return myDetails; }

    // Setters
    public void setType(String theMethod) { myType = theMethod; }
    public void setID(int theID) { myID = theID; }
    public void setTime(double theTime) { myTime = theTime; }
    public void setDetails(String theDetails) { myDetails = theDetails; }
}
