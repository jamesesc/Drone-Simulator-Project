public class TelemetryData {
    double myLatitude;
    double myLongitude;
    double myAltitude;
    double myOrientation;
    double myVelocity;

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
        myVelocity = theNewLatitude;
    }

    public void setLongitude(final double theNewLongitude) {
        myLatitude = theNewLongitude;
    }

    public void setAltitude(final double theNewAltitude) {
        myLatitude = theNewAltitude;
    }

    public void setOrientation(final double theNewOrientation) {
        myOrientation = theNewOrientation;
    }

    public void setVelocity(final double theNewVelocity) {
        myVelocity = theNewVelocity;
    }
}