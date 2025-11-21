package controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A class that handles and manage the internal time system of the simulation.
 * It is used by DroneMonitorApp to help and assist in the simulation time management.
 *
 * @author James Escudero
 * @author Autuman 2025
 */
public class TimerManager {

    /**
     * Represent the TimeManger instance
     */
    private static TimerManager instance;

    /**
     * Represent the update interval time to update the drone telemetry data
     */
    private static final int UPDATE_INTERVAL = 3;

    /**
     * Represent the time interval
     */
    private static final int TIMER_INTERVAL = 1;

    /**
     * Represents the time when the simulation started
     */
    private long myStartTime;

    /**
     * Represent the time when the simulation is pauseTimer
     */
    private long myPausedTime = 0;

    /**
     * Represent the state of the simulation
     */
    private String mySimStatus;

    /**
     * Private constructor to set up and initialize the TimeManger object
     */
    private TimerManager() {
        mySimStatus = "Stopped";
    }

    /**
     * A method to ensure only one TimeManger instance is created.
     *
     * @return the single instance of the TimeManger.
     */
    public static TimerManager getInstance() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }


    /* GETTERS */

    /**
     * Getter method that returns the current state of the simulation.
     *
     * @return a string representing the current simulation status.
     */
    public String getSimStatus() {
        return mySimStatus;
    }

    /**
     * Getter method that returns the update interval of the simulation.
     *
     * @return the update interval as an int.
     */
    public int getUpdateInterval() {
        return UPDATE_INTERVAL;
    }

    /**
     * Getter method that returns the time interval of the simulation.
     *
     * @return the timer interval as an int.
     */
    public int getTimerInterval() {
        return TIMER_INTERVAL;
    }


    /* TIMER METHODS */

    /**
     * Method to start the timer
     */
    public void startTimer() {
        myStartTime = System.currentTimeMillis();
        mySimStatus = "Running";
    }

    /**
     * Method to pause the Timer
     */
    public void pauseTimer() {
        if ("Running".equals(mySimStatus)) {
            myPausedTime = System.currentTimeMillis();
            mySimStatus = "Paused";
        }
    }

    /**
     * Method to resume the timer
     */
    public void resumeTimer() {
        if ("Paused".equals(mySimStatus)) {
            long pausedDuration = System.currentTimeMillis() - myPausedTime;
            myStartTime += pausedDuration;
            mySimStatus = "Running";
            myPausedTime = 0;
        }
    }

    /**
     * Method to stop the timer
     */
    public void stopTimer() {
        mySimStatus = "Stopped";
    }


    /* TIME METHODS */

    /**
     * A method that calculates and returns the elapsed time since the simulation started.
     *
     * @return the elapsed time in seconds as an int.
     */
    public int getElapsedTime() {
        final long now = System.currentTimeMillis();
        final long elapsedTime = now - myStartTime;
        return (int) (elapsedTime / 1000);
    }

    /**
     * A method that calculates and returns the current time when the method is called.
     *
     * @return the current time in military time as a String.
     */

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}