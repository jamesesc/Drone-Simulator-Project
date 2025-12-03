package service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A class that handles and manage the internal time system of the simulation.
 * It is used by DroneMonitorApp to help and assist in the simulation time management.
 *
 * @version Autumn 2025
 */
public class TimerManager {
    /*-- Constant --*/

    /** Represent the update interval time to update the drone telemetry data. */
    private static final int UPDATE_INTERVAL_SECONDS = 1;

    /** Represent the time interval. */
    private static final int TIMER_INTERVAL_SECONDS = 1;

    /** Conversion factor from milliseconds to seconds */
    private static final int MILLIS_TO_SECONDS = 1000;


    /*-- Fields --*/

    /** Represents the time when the simulation started. */
    private long myStartTime;

    /** Represent the time when the simulation is pauseTimer. */
    private long myPausedTime;

    /** Represent the state of the simulation. */
    private Status mySimStatus = Status.STOPPED;

     /** Represents the current operational state of the simulation. */
    public enum Status {
        RUNNING, PAUSED, STOPPED
    }


    /*-- Getters --*/

    /**
     * Getter method that returns the current state of the simulation.
     *
     * @return a string representing the current simulation status.
     */
    public Status getSimStatus() {
        return mySimStatus;
    }

    /**
     * Getter method that returns the update interval of the simulation.
     *
     * @return the update interval as an int.
     */
    public int getUpdateInterval() {
        return UPDATE_INTERVAL_SECONDS;
    }

    /**
     * Getter method that returns the time interval of the simulation.
     *
     * @return the timer interval as an int.
     */
    public int getTimerInterval() {
        return TIMER_INTERVAL_SECONDS;
    }


    /*-- Timer Control Methods --*/

    /**
     * Method to start the timer.
     */
    public void startTimer() {
        myStartTime = System.currentTimeMillis();
        mySimStatus = Status.RUNNING;
    }

    /**
     * Method to pause the Timer.
     */
    public void pauseTimer() {
        if (mySimStatus == Status.RUNNING) {
            myPausedTime = System.currentTimeMillis();
            mySimStatus = Status.PAUSED;
        }
    }

    /**
     * Method to resume the timer.
     */
    public void resumeTimer() {
        if (mySimStatus == Status.PAUSED) {
            long pausedDuration = System.currentTimeMillis() - myPausedTime;
            myStartTime += pausedDuration;
            mySimStatus = Status.RUNNING;
            myPausedTime = 0;
        }
    }

    /**
     * Method to stop the timer.
     */
    public void stopTimer() {
        mySimStatus = Status.STOPPED;
        myStartTime = 0;
        myPausedTime = 0;
    }

    /*-- Time Calculation Methods --*/

    /**
     * A method that calculates and returns the elapsed time since the simulation started.
     *
     * @return the elapsed time in seconds as an int.
     */
    public int getElapsedTime() {
        int elapsedTime;

        if (mySimStatus == Status.RUNNING) {
            final long now = System.currentTimeMillis();
            elapsedTime = (int) ((now - myStartTime) / MILLIS_TO_SECONDS);
        } else if (mySimStatus == Status.PAUSED ) {
            elapsedTime = (int) ((myPausedTime - myStartTime) / MILLIS_TO_SECONDS);
        } else {
            elapsedTime = 0;
        }

        return elapsedTime;
    }

    /**
     * A method that calculates and returns the current time when the method is called.
     *
     * @return the current time in military time as a String.
     */
    public String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}