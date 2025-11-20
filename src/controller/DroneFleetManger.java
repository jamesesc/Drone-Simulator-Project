package controller;

import Model.Drone;
import Model.TelemetryData;

/**
 * A helper class for the DroneMonitorApp that handles the Fleet of drones.
 *
 * @author James Escudero
 * @version Autumn 2025
 */
public class DroneFleetManger {

    /** Represent the Object instance */
    private static DroneFleetManger instance;

    /** A constant to represent the numbers of drones in the fleet */
    private static final int DRONE_COUNT = 3;

    /** A Telemetry generator object to generate telemetry data */
    private final TelemetryGenerator myTelemetryGen;

    /** Represent the current drone being sim on      */
    private static Drone[] myDroneFleet = new Drone[DRONE_COUNT];

    /** Private constructor to create the Object and initialize setup */
    private DroneFleetManger() {
        myDroneFleet = new Drone[DRONE_COUNT];
        myTelemetryGen = new TelemetryGenerator();
        initializeFleet();
        initializeFleetPosition();
    }

    /** Method that ensures only one instance of the object is created */
    public static DroneFleetManger getInstance() {
        if (instance == null) {
            instance = new DroneFleetManger();
        }

        return instance;
    }

    /**
     * A getter to get the DroneFleet array.
     *
     * @return an array of Drone, which represents the Drone Fleet.
     */
    public Drone[] getDroneFleet() {
        return myDroneFleet;
    }

    /**
     * A getter to get a specific drone in the fleet.
     *
     * @param theIndex represents the specific index in the Fleet array they want to return.
     * @return a Drone Object which is based on the specific index that they pass over.
     */
    public Drone getSpecificDrone(final int theIndex) {
        return myDroneFleet[theIndex];
    }

    /**
     * A getter method to return the number of drones in the fleet.
     *
     * @return an int which represents the number of drones in the fleet.
     */
    public int getDroneCount() {
        return DRONE_COUNT;
    }

    /**  Method that initialize drone fleet array with new drone objects */
    public void initializeFleet() {
        System.out.println("Working: DroneFleetManger - InitializeFleet");
        for (int i = 0; i < DRONE_COUNT; i++) {
            myDroneFleet[i] = new Drone();
        }
    }

    /** Method that initialize all drones in the fleet with their starting attitude */
    public void initializeFleetAltitude() {
        for (Drone drone : myDroneFleet) {
            TelemetryData droneCurrData = drone.getDroneTelemetry();
            TelemetryData altitudeData = myTelemetryGen.generateStartAltitude();
            droneCurrData.setAltitude(altitudeData.getAltitude());
            drone.updateTelemetryData(droneCurrData);
        }
    }

    private void initializeFleetPosition() {
        for (Drone drone : myDroneFleet) {
            drone.updateTelemetryData(myTelemetryGen.generateStartPosition());
        }
    }

    /**
     * Method that generate new telemetry data for the number of drones in the fleet.
     *
     * @return a TelemetryData array full of new telemetry data for each drone.
     */
    public TelemetryData[] generateFleetData() {
        TelemetryData[] newGeneratedTelemetryData = new TelemetryData[DRONE_COUNT];

        for (int i = 0; i < myDroneFleet.length; i++) {
            // Passing in the TelemetryGen with the current drone telemetry and receive new, fresh telemetry
            TelemetryData newDroneTelemetry = myTelemetryGen.generateTelemetryData(myDroneFleet[i].getDroneTelemetry());
            // Inserting that new generated telemetry to our array of newGeneratedTelemetryData
            newGeneratedTelemetryData[i] = newDroneTelemetry;
        }

        return newGeneratedTelemetryData;
    }

    /**
     * Method that update each drone in the fleet with the new given telemetry data array.
     *
     * @param theNewTelemetry represents an array of telemetry data for the fleet to update each drone with.
     */
    public void updateFleetData(final TelemetryData[] theNewTelemetry) {
        for (int i = 0; i < myDroneFleet.length;i++) {
            myDroneFleet[i].updateTelemetryData(theNewTelemetry[i]);
        }
    }
}