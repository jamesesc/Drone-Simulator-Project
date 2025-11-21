package controller;

import Model.AnomalyRecord;
import Model.TelemetryData;
import database.AnomalyDB;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A class that connects and ties all the connecting logic into the reoccurring tasks it needs to do.
 * It is used by DroneMonitorApp to help and assist in the simulation to run and start.
 *
 * @author James Escudero
 * @author Autumn 2025
 */
public class SimulationScheduler {

    /** Represents the java import scheduler use to schedule a task */
    private final ScheduledExecutorService myScheduleOperation;

    /** Represents the timer object to manage the simulation timer */
    private final TimerManager myTimerManger;

    /** Represents the Fleet manger object that manges all drone fleet */
    private final DroneFleetManager myFleetManger;

    /** Represent the UpdateUIManager object that sends and update to the UI */
    private final UpdateUIManager myUIUpdater;

    /** Represent the AnomalyDetector object that detects any anomalies with the telemetry data */
    private final AnomalyDetector myAnomalyDetector = new AnomalyDetector();

    /** Represent the AnomalyDB object that stores and handles all the anomalies records */
    private final AnomalyDB myAnomalyDB = new AnomalyDB();

    /** Represents the current status of the simulation; true = running, false for stopped */
    private volatile boolean myPausedStatus = false;

    /** Public constructor to call and use the 1 instance of each object */
    public SimulationScheduler(final TimerManager theTimerManager, final DroneFleetManager theFleetManager,
                               UpdateUIManager theUIUpdater) {
        myScheduleOperation = Executors.newScheduledThreadPool(4);
        myTimerManger = theTimerManager;
        myFleetManger = theFleetManager;
        myUIUpdater = theUIUpdater;

    }

    /**
     * Sets the simulation status to pause.
     *
     * @param thePaused true to pause the simulation, otherwise false to resume.
     */
    public void setPausedStatus(final boolean thePaused) {
        myPausedStatus = thePaused;
    }

    /** Method to start the simulation logic and schedule task */
    public void startSimulationTask() {
        int updateInterval = myTimerManger.getUpdateInterval();
        int timerInterval = myTimerManger.getTimerInterval();

        // Update the drone display for initial drone stats
        myUIUpdater.updateDroneDisplay();

        // Update drone when 3 seconds is up, meaning when it flies up
        myScheduleOperation.schedule(
                myUIUpdater::updateDroneDisplay,
                updateInterval,
                TimeUnit.SECONDS
        );

        // First schedule task to initialize drone altitudes at 3 seconds (1st update)
        myScheduleOperation.schedule(
                myFleetManger::initializeFleetAltitude,
                updateInterval,
                TimeUnit.SECONDS
        );

        // Fixed Schedule Task that updates drones telemetry data every 3 seconds (it starts after 6 sec)
        myScheduleOperation.scheduleAtFixedRate(
                this::updateDronesTask,
                updateInterval * 2L,
                updateInterval,
                TimeUnit.SECONDS
        );


        // Timer Update to the UI
        myScheduleOperation.scheduleAtFixedRate(
                this::updateTime,
                0,
                timerInterval,
                TimeUnit.SECONDS
        );
    }

    /* Helper Methods to start the simulation */

    /**
     * The Main task method that handles the whole drone update
     * Generating telemetry data, detecting anomalies and updating the drone with the new telemetry.
     */
    private void updateDronesTask() {
        // If the Simulator is paused, don't update
        if (myPausedStatus) {
            return;
        }
        try {
            // 1) Generate new telemetry for all drones
            TelemetryData[] newTelemetry = myFleetManger.generateFleetData();

            // TODO: THE timeStamp is a STRING, and Osin wants a int in the AnomalyDetector

            // 2) Detect anomalies
            AnomalyRecord[] anomalies = myAnomalyDetector.analyzeDrones(
                    newTelemetry,
                    myFleetManger.getDroneFleet(),
                    myTimerManger.getElapsedTime(),
                    myTimerManger.getUpdateInterval()
            );

            // 3) Save anomalies
            //myAnomalyProcessor.saveAnomaliesToDB(anomalies);

            // 4) Update fleet data
            myFleetManger.updateFleetData(newTelemetry);

            // 5) Update the display
            myUIUpdater.updateDroneDisplay();

        } catch (Exception e) {
            System.err.println("Theres a ERROR in updateDronesTask:");
        }
    }

    /** Helper method to do update time task */
    private void updateTime() {
        int elapseTime = myTimerManger.getElapsedTime();
        myUIUpdater.updateTimer(elapseTime);
    }

    /** To stop the reoccurring schedule tasks */
    public void stopSimulationSchedule() {
        myScheduleOperation.shutdown();
    }
}