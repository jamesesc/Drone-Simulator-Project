package controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * A helper class for DroneMonitorApp that handles and manges the simulation time.
 *
 * @author James Escudero
 * @author Autuman 2025
 */
public class TimerManger {

    /** Represent the Object instance */
    private static TimerManger instance;

    /** Represent the Time interval to update for drone telemetry data */
    private static final int UPDATE_INTERVAL = 3;

    /** Represent the Time Interval for the timer */
    private static final int TIMER_INTERVAL = 1;

    /** Represents the time when the simulation started */
    private long myStartTime;

    /** Represent te timer when the simulation is paused */
    private long myPausedTime = 0;

    /** Represent the state of the sim */
    private String mySimStatus;

    /** Private constructor to set up and initialize the TimeManger object */
    private TimerManger() {
        mySimStatus = "Stopped";
    }

    /** Helper method to ensure that only one instance of the object is ever made. */
    public static TimerManger getInstance() {
        if (instance == null) {
            instance = new TimerManger();
        }
        return instance;
    }

    /** Method to start the timer up */
    public void startTimer() {
        myStartTime = System.currentTimeMillis();
        mySimStatus = "Running";
    }

    /** Method to pause the Timer */
    public void paused() {
        if ("Running".equals(mySimStatus)) {
            myPausedTime = System.currentTimeMillis();
            mySimStatus = "Paused";
        }
    }

    /** Method to correctly resume the timer in the right and correct time */
    public void resume() {
        if ("Paused".equals(mySimStatus)) {
            long pausedDuration = System.currentTimeMillis() - myPausedTime;
            myStartTime += pausedDuration;
            mySimStatus = "Running";
            myPausedTime = 0;
        }
    }

    /**
     * A getter method that returns the current elapsed time when method is call
     *
     * @return an int that represents the current time elapsed of the simulation.
     */
    public int getElapsedTime() {
        final long now = System.currentTimeMillis();
        final long elapsedTime = now - myStartTime;
        return (int) (elapsedTime / 1000);
    }

    /**
     * A getter method that return the sim status of the simulation based on the timer
     *
     * @return a string that represents the current status of the simulation.
     */
    public String getSimStatus() {
        return mySimStatus;
    }

    /**
     * A getter method to know if the sim is running or not
     *
     * @return a true if the sim is "Running", otherwise false.
     */
    public boolean isSimRunning() {
        return "Running".equals(mySimStatus);
    }

    public void stopTimer() {
        mySimStatus = "Stopped";
    }

    /**
     * A getter method that returns the update interval of the simulation.
     *
     * @return an int that represents the update interval of the simulation.
     */
    public int getUpdateInterval() {
        return UPDATE_INTERVAL;
    }

    public int getTimerInterval() {
        return TIMER_INTERVAL;
    }

    // Method that list of different types of output of date and time format
    private void differentTypesOfDate() {
        // Print to show the time running working correctly and accurately
        System.out.println("Elapsed time: " + (int) getElapsedTime() + " seconds");
        System.out.println(LocalTime.now());
        System.out.println(LocalTime.now(ZoneId.of("America/Los_Angeles")));


        LocalDateTime now = LocalDateTime.now();
        String formatted = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(formatted);
    }
}
