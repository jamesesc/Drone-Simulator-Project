package Model;

public class TelemetryData {
    double myLatitude;
    double myLongitude;
    double myAltitude;
    double myOrientation;
    double myVelocity;

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
    public double getLatitude() {
        return myLatitude;
    }
    public double getLongitude() {
        return myLongitude;
    }
    public double getAltitude() {
        return myAltitude;
    }
    public double getOrientation() {
        return myOrientation;
    }
    public double getVelocity() {
        return myVelocity;
    }


    /* SETTERS */

    public void setLatitude(final double theNewLatitude) {
        myLatitude = theNewLatitude;
    }
    public void setLongitude(final double theNewLongitude) {
        myLongitude = theNewLongitude;
    }
    public void setAltitude(final double theNewAltitude) {
        myAltitude = theNewAltitude;
    }
    public void setOrientation(final double theNewOrientation) {
        myOrientation = theNewOrientation;
    }
    public void setVelocity(final double theNewVelocity) {
        myVelocity = theNewVelocity;
    }
}