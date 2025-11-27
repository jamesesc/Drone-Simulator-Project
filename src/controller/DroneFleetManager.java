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
    private static int myDroneCount = 3;

    /** A TelemetryGenerator Object responsible for generating telemetry data */
    private TelemetryGenerator myTelemetryGen;

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
     * To change the new update drone count
     */
    public void updateDroneCount(int newCount) {
        if (newCount <= 0) return;

        Drone.resetIdCounter();

        myDroneCount = newCount;
        myDroneFleet = new Drone[myDroneCount];
        // New generator
        myTelemetryGen = new TelemetryGenerator();
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
     */
    public Drone getSpecificDrone(final int theIndex) {
         if (theIndex < 0 || theIndex >= myDroneFleet.length - 1) {
            throw new IndexOutOfBoundsException(
                "Invalid Drone Index chosen: : " + theIndex + ". Index must be between" +
                " 0 and " + (myDroneFleet.length - 1) + "."
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
     */
    public void updateFleetData(final TelemetryData[] theNewTelemetry) {
       if (theNewTelemetry == null) {
           throw new NullPointerException();
       }

        for (int i = 0; i < myDroneFleet.length;i++) {
            // Updating the Drone Telemetry Data
            myDroneFleet[i].updateTelemetryData(theNewTelemetry[i]);
            // Updating the Drone Battery
            myDroneFleet[i].simulateBatteryDrain();
        }
    }
}