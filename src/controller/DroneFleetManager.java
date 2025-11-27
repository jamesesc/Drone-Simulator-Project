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

    /** Constant that represent the default numbers of drones in the fleet */
    private static int myDroneCount = 3;

    /** A TelemetryGenerator Object responsible for generating telemetry data */
    private final TelemetryGenerator myTelemetryGen;

    /** Represent the current drone being simulated on */
    private Drone[] myDroneFleet;


    /* CONSTRUCTOR */

    /** Public constructor for initializing the drone fleet and telemetry generator */
    public DroneFleetManager() {
        myDroneFleet = new Drone[myDroneCount];
        myTelemetryGen = new TelemetryGenerator();

        initializeFleet();
    }

    /**
     * To change the number of drones to make int the fleet.
     *
     * @param theNewCount is the new amount of drones to make in the fleet.
     * @throws IllegalArgumentException if the theNewCount is less than 1.
     */
    public void updateDroneCount(int theNewCount) {
        if (theNewCount <= 0) {
            throw new IllegalArgumentException("Drone count must be greater than 0, got: " + theNewCount);
        }

        Drone.resetIdCounter();
        myDroneCount = theNewCount;
        myDroneFleet = new Drone[myDroneCount];
        initializeFleet();
    }


    /* GETTERS */

    /**
     * Getter method that returns the drone fleet of the simulation.
     *
     * @return an array of Drone objects representing the fleet.
     */
    public Drone[] getDroneFleet() {
        return myDroneFleet.clone();
    }

    /**
     * Getter method that returns a specific drone from the fleet based on the provided index.
     *
     * @param theIndex represents the index of the drone in the Fleet array.
     * @return the Drone Object based on the index that was given.
     * @throws IndexOutOfBoundsException if theIndex is negative or greater than or equal to the fleet size.
     */
    public Drone getSpecificDrone(final int theIndex) {
         if (theIndex < 0 || theIndex >= myDroneFleet.length) {
            throw new IndexOutOfBoundsException(
                "Invalid Drone Index chosen: : " + theIndex +
                ". Index must be between 0 and " + (myDroneFleet.length - 1) + "."
            );
        }
        return myDroneFleet[theIndex];
    }

    /**
     * Getter method that returns the number of drones in the fleet.
     *
     * @return the number of drones in the fleet as an int.
     */
    public int getDroneCount() {
        return myDroneCount;
    }


    /* DRONE FLEET LOGIC METHODS */

    /**  Initialize the drone fleet array with new Drone objects */
    private void initializeFleet() {
        for (int i = 0; i < myDroneCount; i++) {
            myDroneFleet[i] = new Drone();
        }
    }

    /**  Initialize every Drone object in the fleet to their starting position */
    public void initializeFleetPosition() {
        for (Drone drone : myDroneFleet) {
            drone.updateDroneNextMove(myTelemetryGen.generateStartPosition());
        }
    }

    /** Initialize all drones in the fleet with their starting attitude */
    public void initializeFleetAltitude() {
        for (Drone drone : myDroneFleet) {
            TelemetryData droneCurrData = drone.getDroneTelemetry();
            TelemetryData altitudeData = myTelemetryGen.generateStartAltitude();
            droneCurrData.setAltitude(altitudeData.getAltitude());
            drone.updateDroneNextMove(droneCurrData);
        }
    }

    /**
     * Generate new telemetry data for all drones in the fleet.
     *
     * @return an array of TelemetryData containing new telemetry data for each drone.
     */
    public TelemetryData[] generateFleetData() {
        TelemetryData[] newTelemetryDataArray = new TelemetryData[myDroneCount];

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
     * @throws NullPointerException if theNewTelemetry is null.
     * @throws ArrayIndexOutOfBoundsException if theNewTelemetry array length is less than the fleet size.
     */
    public void updateFleetData(final TelemetryData[] theNewTelemetry) {
        if (theNewTelemetry == null) {
            throw new NullPointerException("Telemetry data array cannot be null");
        }

        if (theNewTelemetry.length < myDroneFleet.length) {
            throw new ArrayIndexOutOfBoundsException(
                    "Telemetry array length (" + theNewTelemetry.length + ") +" +
                    "is less than fleet size (" + myDroneFleet.length + ")"
            );
        }

        for (int i = 0; i < myDroneFleet.length;i++) {
            // Updating the Drone Telemetry Data
            myDroneFleet[i].updateDroneNextMove(theNewTelemetry[i]);
            // Updating the Drone Battery
            myDroneFleet[i].simulateBatteryDrain();
        }
    }
}