package Model;

/**
 * An object class that contains all the telemetry data for the drone.
 */
public class TelemetryData {
    /* FIELDS */

    /** Represent the drone Latitude */
    double myLatitude;

    /** Represent the drone Longitude */
    double myLongitude;

    /** Represent the drone ALatitude */
    double myAltitude;

    /** Represent the drone Orientation */
    double myOrientation;

    /** Represent the drone Velocity */
    double myVelocity;


    /* CONSTRUCTOR */

    /**
     * No-arg constructor to help initialize default
     * values for the Telemetry Data in each drone
     */
    public TelemetryData() {
        myLatitude = 0;
        myLongitude = 0;
        myAltitude = 0;
        myOrientation = 0;
        myVelocity = 0;
    }

    public TelemetryData(double theLatitude, double theLongitude,
                         double theAltitude, double theOrientation, double theVelocity) {
        myLatitude = theLatitude;
        myLongitude = theLongitude;
        myAltitude = theAltitude;
        myOrientation = theOrientation;
        myVelocity = theVelocity;
    }

    /* GETTERS */

    /** Getter for drone latitude */
    public double getLatitude() {
        return myLatitude;
    }

    /** Getter for drone longitude */
    public double getLongitude() {
        return myLongitude;
    }

    /** Getter for drone altitude */
    public double getAltitude() {
        return myAltitude;
    }

    /** Getter for drone orientation */
    public double getOrientation() {
        return myOrientation;
    }

    /** Getter for drone velocity */
    public double getVelocity() {
        return myVelocity;
    }


    /* SETTERS */

    /**
     * A setter for drone latitude.
     *
     * @param theNewLatitude represent the new drone latitude.
     */
    public void setLatitude(final double theNewLatitude) {
        myLatitude = theNewLatitude;
    }

    /**
     * A setter for drone longitude.
     *
     * @param theNewLongitude represent the new drone longitude.
     */
    public void setLongitude(final double theNewLongitude) {
        myLongitude = theNewLongitude;
    }

    /**
     * A setter for drone altitude.
     *
     * @param theNewAltitude represent the new drone altitude.
     */
    public void setAltitude(final double theNewAltitude) {
        myAltitude = theNewAltitude;
    }

    /**
     * A setter for drone orientation.
     *
     * @param theNewOrientation represent the new drone orientation.
     */
    public void setOrientation(final double theNewOrientation) {
        myOrientation = theNewOrientation;
    }

    /**
     * A setter for drone velocity.
     *
     * @param theNewVelocity represents the new drone velocity.
     */
    public void setVelocity(final double theNewVelocity) {
        myVelocity = theNewVelocity;
    }
}