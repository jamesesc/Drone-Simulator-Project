package controller;

import Model.Drone;
import service.DroneFleetManager;
import service.TimerManager;

import java.util.Objects;

/**
 * A Controller class that handles and manage the core system of the simulation.
 * The class uses many helper classes to help build the functionality and operation
 * of the simulation all together.
 *
 * @author James Escudero
 * @version Fall 2025
 */
public class DroneMonitorApp {
    /*-- Dependency Interjection Classes --*/

    /* A class object that help manage the time system of the simulation */
    private final TimerManager myTimerManager;

    /* A class object that help manage run the tasks needed for the simulation */
    private final SimulationController mySchedulerOperator;

    /** A class object that helps manage the fleet of drones for the simulation */
    private final DroneFleetManager myDroneFleet;


    /*-- Constructor --*/

    /**
     *  Public constructor to create the DroneMonitorApp instance.
     *
     * @param theTimerManager represents the TimeManager object to manage timer functionality.
     * @param theSchedulerOperator represents the SimulationController object to handle the simulation tasks.
     */
    public DroneMonitorApp(final TimerManager theTimerManager, final SimulationController theSchedulerOperator,
                           final DroneFleetManager theDroneFleet) {
        myTimerManager = Objects.requireNonNull(theTimerManager, "TimeManger can't be null");
        mySchedulerOperator = Objects.requireNonNull(theSchedulerOperator, "FleetManager can't be null");
        myDroneFleet = Objects.requireNonNull(theDroneFleet, "DroneFleet can't be null");
    }


    /*-- Different Simulation "Stages/Phases" Methods --*/

    /**
     * Method to start the simulation
     */
    public void startSim() {
        myTimerManager.startTimer();
        myDroneFleet.resetFleet();
        mySchedulerOperator.setPausedStatus(false);
        mySchedulerOperator.startSimulationTask();
    }

    /**
     * Method to pause the simulation
     */
    public void pauseSim() {
        mySchedulerOperator.setPausedStatus(true);
        myTimerManager.pauseTimer();
    }

    /**
     * Method to continue the simulation after simulation has been pauseTimer
     */
    public void continueSim() {
        mySchedulerOperator.setPausedStatus(false);
        myTimerManager.resumeTimer();
    }

    /**
     * Method to stop the simulation all together
     */
    public void stopSim() {
        myTimerManager.stopTimer();
        mySchedulerOperator.stopSimulationSchedule();
    }


    /*-- Configuration --*/

    /**
     * Helps allow the configuration and changing of the number of drones in the fleet.
     *
     * @param theNewCount represents the new number of drones in the fleet to be.
     */
    public void changeDroneCount(final int theNewCount) {
        stopSim();
        myDroneFleet.updateDroneCount(theNewCount);
    }


    /*-- Getters --*/

    /**
     * Get a copy of the current drone fleet.
     *
     * @return an array of all drones in the fleet.
     */
    public Drone[] getFleet() {
        return myDroneFleet.getDroneFleet();
    }
}