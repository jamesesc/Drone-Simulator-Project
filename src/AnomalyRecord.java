import Model.TelemetryData;

public class AnomalyRecord {
    /**
     * Name of method you had an error in.
     */
    private String myMethod;
    /**
     * ID of the drone w/ an error, null if it's multiple drones.
     */
    private int myID;
    /**
     * Time that the error happened.
     */
    private double myTime;
    /**
     * Telemetry data for anomaly
     */
    private TelemetryData myTelemetry;


    //Constructors
    public AnomalyRecord() {
        myMethod = "";
        myTime = 0;
    }

    public AnomalyRecord(String theMethod, int theID, double theTime) {
        myMethod = theMethod;
        myID = theID;
        myTime = theTime;
    }

    public AnomalyRecord(String theMethod, double theTime) {
        myMethod = theMethod;
        myTime = theTime;
    }

    // Getters
    public String getMethod() {
        return myMethod;
    }

    public int getID() {
        return myID;
    }

    public double getTime() {
        return myTime;
    }

    public TelemetryData getTelemetryData() {return myTelemetry; }


    // Setters
    public void setMethod(String theMethod) {
        myMethod = theMethod;
    }

    public void setID(int theID) {
        myID = theID;
    }

    public void setTime(double theTime) {
        myTime = theTime;
    }

    public void setTelemetryData(TelemetryData theTelemetry) { myTelemetry = theTelemetry; }
}
