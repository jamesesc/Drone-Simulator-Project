package controller;

import Model.AnomalyRecord;
import Model.Drone;
import Model.TelemetryData;
import database.AnomalyDB;

/**
 * A helper class for the DroneMonitorApp to help and process the AnomalyProcessing.
 */
public class AnomalyProcessor {

    /** An object that represents the AnomalyDetector */
    private final AnomalyDetector myAnomalyDetector;

    /** An object that represents the Anomaly Database */
    private final AnomalyDB myAnomalyDB;

    /** Private constructor to initialize the objects in used */
    public AnomalyProcessor() {
        myAnomalyDetector = new AnomalyDetector();
        myAnomalyDB = new AnomalyDB();
    }

    /**
     * Method that handles and analyze the given data.
     *
     * @param theNewTelemetryData represents an array full of new generated telemetry data for the drones.
     * @param theDroneFleet represents an array full of drones
     * @param theElapsedTime represents the current duration time of the simulation.
     * @param theTimeInterval represents the Time Update Interval.
     * @return an array full of AnomalyRecord objects.
     */
    public AnomalyRecord[] processAnomalies(final TelemetryData[] theNewTelemetryData, Drone[] theDroneFleet, int theElapsedTime, int theTimeInterval) {
        return myAnomalyDetector.analyzeDrones(theNewTelemetryData, theDroneFleet, theElapsedTime, theTimeInterval);
    }

    /**
     * Method that saves a list of theAnomaliesList given to the database.
     *
     * @param theAnomaliesList represents an array full of anomalies objects.
     */
    public void saveAnomaliesToDB(AnomalyRecord[] theAnomaliesList) {
        myAnomalyDB.saveAnomalies(theAnomaliesList);
    }
}