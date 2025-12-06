package service;

import Model.Drone;
import Model.DroneFactory;
import Model.TelemetryData;
import java.util.Objects;

/**
 * A class that handles and manage all the drones in the simulation.
 * It is used by DroneMonitorApp to help and assist in the simulation logic and functionality.
 *
 * @version Autumn 2025
 */
public final class DroneFleetManager {
    /*-- Constant --*/

    /** Constant that represent the default numbers of drones in the fleet. */
    private static final int DEFAULT_DRONE_COUNT = 3;


    /*-- Dependency Injection *--/

     */
    /** A TelemetryGenerator Object responsible for generating telemetry data. */
    private final TelemetryGenerator myTelemetryGen;


    /*-- Fields --*/

    /** Represent the current drone being simulated on. */
    private Drone[] myDroneFleet;

    /** Represent the current number of drones in the fleet. */
    private int myDroneCount;

    /** The Drone factory responsible for creating drones **/
    final private DroneFactory myDroneFactory;


    /*-- Constructor --*/

    /**
     * Public constructor for initializing the drone fleet and telemetry generator.
     *
     * @param theTelemetryGen represents the telemetry generator being in used.
     */
    public DroneFleetManager(final TelemetryGenerator theTelemetryGen, final DroneFactory theFactory) {
        myTelemetryGen = Objects.requireNonNull(theTelemetryGen, "theTelemetryGen can't be null");
        myDroneCount = DEFAULT_DRONE_COUNT;
        myDroneFleet = new Drone[myDroneCount];
        myDroneFactory = theFactory;

        initializeFleet();
    }


    /*-- Updating --*/

    /**
     * To change the number of drones to make int the fleet.
     *
     * @param theNewCount is the new amount of drones to make in the fleet.
     * @throws IllegalArgumentException if the theNewCount is less than 1.
     */
    public void updateDroneCount(final int theNewCount) {
        if (theNewCount <= 0) {
            throw new IllegalArgumentException("Drone count must be greater than 0, got: " + theNewCount);
        }

        myDroneFactory.resetIdCounter();
        myDroneCount = theNewCount;
        myDroneFleet = new Drone[myDroneCount];
        initializeFleet();
    }


    /* GETTERS */

    /**
     * Getter method that returns the number of drones in the fleet.
     *
     * @return the number of drones in the fleet as an int.
     */
    public int getDroneCount() {
        return myDroneCount;
    }

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
     * Method to find the id of a drone.
     *
     * @param theRequestDroneId represents the id of the drone we want to find.
     * @return the drone base on the id.
     */
    public Drone getDroneById(final int theRequestDroneId) {
        for (Drone drone : myDroneFleet) {
            if (drone.getDroneID() == theRequestDroneId) {
                return drone;
            }
        }
        return null; // Not found
    }


    /*-- Drone Fleet Logic Methods --*/

    /**
     * Initialize the drone fleet array with new Drone objects.
     */
    private void initializeFleet() {
        for (int i = 0; i < myDroneCount; i++) {
            Drone newDrone = DroneFactory.createDrone("Basic");
            myDroneFleet[i] = newDrone;
        }
    }

    /**
     * Method to reset the entire fleet.
     */
    public void resetFleet() {
        myDroneFactory.resetIdCounter();
        initializeFleet();
        initializeFleetPosition();
        initializeFleetAltitude();
    }


    /**
     * Initialize every Drone object in the fleet to their starting position.
     */
    public void initializeFleetPosition() {
        for (Drone drone : myDroneFleet) {
            drone.updateDroneNextMove(myTelemetryGen.generateStartPosition());
        }
    }

    /**
     * Initialize all drones in the fleet with their starting attitude.
     */
    public void initializeFleetAltitude() {
        for (Drone drone : myDroneFleet) {
            TelemetryData droneCurrData = drone.getDroneTelemetry();

            // Null safety check
            if (droneCurrData == null) {
                System.err.println("Warning: Drone " + drone.getDroneID() +
                        " has null telemetry, skipping altitude initialization");
                continue;
            }

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
     * @throws IllegalArgumentException if theNewTelemetry array length is less than the fleet size.
     */
    public void updateFleetData(final TelemetryData[] theNewTelemetry) {
        if (theNewTelemetry == null) {
            throw new NullPointerException("Telemetry data array cannot be null");
        }

        if (theNewTelemetry.length != myDroneFleet.length) {
            throw new IllegalArgumentException(
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