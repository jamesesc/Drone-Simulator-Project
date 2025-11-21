package controller;

import Model.Drone;
import Model.TelemetryData;

/**
 * A class that handles and manage all the drones in the simulation.
 * It is used by DroneMonitorApp to help and assist in the simulation logic and functionality.
 *
 * @author James Escudero
 * @version Autumn 2025
 */
public class DroneFleetManager {

    /** Constant that represent the numbers of drones in the fleet */
    private static final int DRONE_COUNT = 3;

    /** A TelemetryGenerator Object responsible for generating telemetry data */
    private final TelemetryGenerator myTelemetryGen;

    /** Represent the current drone being simulated on */
    private static Drone[] myDroneFleet;


    /* CONSTRUCTOR */

    /** Public constructor for initializing the drone fleet and telemetry generator */
    public DroneFleetManager() {
        myDroneFleet = new Drone[DRONE_COUNT];
        myTelemetryGen = new TelemetryGenerator();

        initializeFleet();
        initializeFleetPosition();
    }


    /* GETTERS */

    /**
     * Getter method that returns the drone fleet of the simulation.
     *
     * @return an array of Drone objects representing the fleet.
     */
    public Drone[] getDroneFleet() {
        return myDroneFleet;
    }

    /**
     * Getter method that returns a specific drone from the fleet based on the provided index.
     *
     * @param theIndex represents the index of the drone in the Fleet array.
     * @return the Drone Object based on the index that was given.
     */
    public Drone getSpecificDrone(final int theIndex) {
        if (theIndex > 0 && theIndex < myDroneFleet.length) {
           return myDroneFleet[theIndex];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Getter method that returns the number of drones in the fleet.
     *
     * @return the number of drones in the flee as an int.
     */
    public int getDroneCount() {
        return DRONE_COUNT;
    }


    /* DRONE FLEET LOGIC METHODS */

    /**  Initialize the drone fleet array with new Drone objects */
    private void initializeFleet() {
        for (int i = 0; i < DRONE_COUNT; i++) {
            myDroneFleet[i] = new Drone();
        }
    }

    /**  Initialize every Drone object in the fleet to their starting position */
    private void initializeFleetPosition() {
        for (Drone drone : myDroneFleet) {
            drone.updateTelemetryData(myTelemetryGen.generateStartPosition());
        }
    }

    /** Initialize all drones in the fleet with their starting attitude */
    public void initializeFleetAltitude() {
        for (Drone drone : myDroneFleet) {
            TelemetryData droneCurrData = drone.getDroneTelemetry();
            TelemetryData altitudeData = myTelemetryGen.generateStartAltitude();
            droneCurrData.setAltitude(altitudeData.getAltitude());
            drone.updateTelemetryData(droneCurrData);
        }
    }

    /**
     * Generate new telemetry data for all drones in the fleet.
     *
     * @return an array of TelemetryData containing new telemetry data for each drone.
     */
    public TelemetryData[] generateFleetData() {
        TelemetryData[] newTelemetryDataArray = new TelemetryData[DRONE_COUNT];

        for (int i = 0; i < myDroneFleet.length; i++) {
            TelemetryData droneTelemetry = myDroneFleet[i].getDroneTelemetry();
            TelemetryData newDroneTelemetryData = myTelemetryGen.generateTelemetryData(droneTelemetry);
            newTelemetryDataArray[i] = newDroneTelemetryData;
        }

        return newTelemetryDataArray;
    }

    /**
     * Update each drone in the fleet with the new given telemetry data array.
     *
     * @param theNewTelemetry is an array of telemetry data to update each drone in the fleet.
     */
    public void updateFleetData(final TelemetryData[] theNewTelemetry) {
        for (int i = 0; i < myDroneFleet.length;i++) {
            // Updating the Drone Telemetry Data
            myDroneFleet[i].updateTelemetryData(theNewTelemetry[i]);
            // Updating the Drone Battery
            myDroneFleet[i].simulateBatteryDrain();
        }
    }
}