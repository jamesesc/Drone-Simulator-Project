package controller;

import Model.Drone;
import service.SimulationEngine;
import service.DroneFleetManager;
import service.TimerManager;
import view.SimulationListener;
import java.util.Objects;

/**
 * A Controller class that handles and manage the core system of the simulation.
 * The class uses many helper classes to help build the functionality and operation
 * of the simulation altogether.
 *
 * @version Fall 2025
 */
public class DroneMonitorApp {
    /*-- Dependency Injection --*/

    /* A class object that help manage the time system of the simulation. */
    private final TimerManager myTimerManager;

    /* A class object that help manage run the tasks needed for the simulation. */
    private final SimulationEngine mySimulationController;

    /** A class object that helps manage the fleet of drones for the simulation. */
    private final DroneFleetManager myDroneFleet;

    /** A class that is used to push data to the front end. */
    private SimulationListener myListener;


    /*-- Constructor --*/

    /**
     *  Public constructor to create the DroneMonitorApp instance.
     *
     * @param theTimerManager represents the TimeManager object to manage timer functionality.
     * @param theSimulationController represents the SimulationEngine object to handle the simulation tasks.
     * @param theDroneFleet represents the drones that's being used in the simulation.
     * @throws NullPointerException if any of the parameters are null.
     */
    public DroneMonitorApp(final TimerManager theTimerManager, final SimulationEngine theSimulationController,
                           final DroneFleetManager theDroneFleet) {
        myTimerManager = Objects.requireNonNull(theTimerManager, "TimeManager can't be null");
        mySimulationController = Objects.requireNonNull(theSimulationController, "SimulationEngine can't be null");
        myDroneFleet = Objects.requireNonNull(theDroneFleet, "DroneFleet can't be null");
    }


    /*-- Setter --*/

    /**
     * Method that sets the simulation listener for any simulation events.
     *
     * @param theListener represents the listener to notified when a simulation event occurred.
     */
    public synchronized void setSimulationListener(SimulationListener theListener) {
        myListener = theListener;
        mySimulationController.setSimulationListener(theListener);
        myTimerManager.setListener(theListener);
    }


    /*-- Different Simulation "Stages/Phases" Methods --*/

    /**
     * Method to start the simulation
     */
    public void startSim() {
        myDroneFleet.resetFleet();
        myTimerManager.startTimer();
        mySimulationController.startSimulationTask();
        notifyFleetReloaded();
    }

    /**
     * Set pause to the simulation.
     */
    public void setPaused(){
        myTimerManager.pauseTimer();
    }

    /**
     * Toggles the simulation between paused and running states.
     */
    public void togglePause() {
        TimerManager.Status currentStatus = myTimerManager.getSimStatus();

        if (currentStatus == TimerManager.Status.PAUSED) {
            myTimerManager.resumeTimer();
        } else if (currentStatus == TimerManager.Status.RUNNING) {
            myTimerManager.pauseTimer();
        }
    }

    /**
     * Method to stop the simulation altogether.
     */
    public void stopSim() {
        myTimerManager.stopTimer();
        mySimulationController.stopSimulationSchedule();
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
        notifyFleetReloaded();
    }

    /**
     * Helps allow the configuration and changing of the tick speed.
     *
     * @param theNewTickSpeed represents the new tick speed in seconds.
     */
    public void changeTickSpeed(final int theNewTickSpeed) {
        myTimerManager.setTickSpeed(theNewTickSpeed);
    }


    /*-- Helper Methods --*/

    /**
     * Helper method to push Drone Fleet data.
     */
    private void notifyFleetReloaded() {
        // Checks if listener and fleet isn't null, if not, then notify the update
        if (myListener != null) {
            Drone[] fleet = myDroneFleet.getDroneFleet();
            if (fleet != null) {
                myListener.onFleetReloaded(fleet);
            }
        }
    }
}