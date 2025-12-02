package controller;

import Model.AnomalyRecord;
import Model.Drone;
import Model.TelemetryData;
import database.AnomalyDB;
import service.AnomalyDetector;
import service.DroneFleetManager;
import service.TimerManager;
import view.SimulationListener;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A class that connects and ties all the connecting logic into the reoccurring tasks it needs to do.
 * It is used by DroneMonitorApp to help and assist in the simulation to run and start.
 *
 * @author Autumn 2025
 */
public class SimulationController {
    /*-- Constant --*/

    /** Number of threads for scheduled task execution */
    private static final int THREAD_POOL_SIZE = 4;

    /** Timeout duration for graceful shutdown (seconds) */
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;


    /*-- Dependency Injection --*/

    /** Represents the java import scheduler use to schedule a task */
    private ScheduledExecutorService myScheduleOperation;

    /** Represents the timer object to manage the simulation timer */
    private final TimerManager myTimerManger;

    /** Represents the Fleet manger object that manges all drone fleet */
    private final DroneFleetManager myFleetManger;

    /** Represent the AnomalyDetector object that detects any anomalies with the telemetry data */
    private final AnomalyDetector myAnomalyDetector;

    /** Represent the AnomalyDB object that stores and handles all the anomalies records */
    private final AnomalyDB myAnomalyDB;

    /** Represent the UpdateUIManager object that sends and update to the UI */
    private SimulationListener myListener;


    /*-- State --*/

    /** Represents the current status of the simulation; true = running, false for stopped */
    private volatile boolean myPausedStatus = false;


    /*-- Constructor --*/

    /**
     * Creates a SimulationController with required dependencies.
     *
     * @param theTimerManager manages simulation time
     * @param theFleetManager manages the drone fleet
     * @param theAnomalyDetector detects anomalies in telemetry
     * @param theAnomalyDB stores anomaly records
     * @throws NullPointerException if any parameter is null
     */
    public SimulationController(final TimerManager theTimerManager, final DroneFleetManager theFleetManager,
                                final AnomalyDetector theAnomalyDetector, final AnomalyDB theAnomalyDB) {
        // Safety check if the follow objects pass is not null
        myTimerManger = Objects.requireNonNull(theTimerManager, "TimeManger can't be null");
        myFleetManger = Objects.requireNonNull(theFleetManager, "FleetManager can't be null");
        myAnomalyDetector = Objects.requireNonNull(theAnomalyDetector, "AnomalyDetector can't be null");
        myAnomalyDB = Objects.requireNonNull(theAnomalyDB, "AnomalyDB can't be null");

    }


    /*-- Configuration --*/

    /**
     * Setter for the listener (Observer Pattern)
     *
     * @param theListener is the observer to notify of simulation events.
     */
    public void setSimulationListener(SimulationListener theListener) {
        myListener = theListener;
    }

    /**
     * Sets the simulation status to pause.
     *
     * @param thePausedStatus is true to pause the simulation, otherwise false to resume.
     */
    public void setPausedStatus(final boolean thePausedStatus) {
        // Updating only if there's an update change
        if (myPausedStatus != thePausedStatus) {
            myPausedStatus = thePausedStatus;
        }
    }


    /*-- Simulation Cycle --*/

    /**
     * Method to start the simulation logic and schedule task
     */
    public void startSimulationTask() {
        myScheduleOperation = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);

        final int updateInterval = myTimerManger.getUpdateInterval();
        final int timerInterval = myTimerManger.getTimerInterval();

        myFleetManger.initializeFleetPosition();

        // Update the drone display for initial drone stats
        notifyDroneUpdate();


        // Update drone when 3 seconds is up, meaning when it flies up
        myScheduleOperation.schedule(() -> {
            myFleetManger.initializeFleetAltitude();

            if (myListener != null) {
                myListener.onDroneUpdate(myFleetManger.getDroneFleet());
            }
        }, updateInterval, TimeUnit.SECONDS);

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

    /**
     * To stop the reoccurring schedule tasks
     */
    public void stopSimulationSchedule() {

        // Handles the thread safety in shutting down
        if (myScheduleOperation != null) {
            myScheduleOperation.shutdownNow();
            try {
                if (!myScheduleOperation.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    System.err.println("The Simulation Scheduler did not terminate");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Simulation scheduler shutdown was interrupted");
            }
        }
    }



    /*-- Schedule Tasks --*/

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
            TelemetryData[] newTelemetry = generateTelemetry();

            // 2) Detect anomalies
            AnomalyRecord[] anomalies = detectAnomalies(newTelemetry);

            // 3) Save anomalies
            saveAnomalies(anomalies);

            // 4) Update fleet data
            updateFleet(newTelemetry);

            // 5) Notifying listeners for the new anomalies
            notifyListeners(anomalies);

        } catch (Exception e) {
            System.err.println("Theres a ERROR in updateDronesTask:" + e.getMessage());
        }
    }

    /**
     * Updates the timer display.
     * This method is called periodically by the scheduler.
     */
    private void updateTime() {
        if (myListener != null) {
            int elapsedTime = myTimerManger.getElapsedTime();
            myListener.onTimeUpdate(elapsedTime);
        }
    }


    /*-- Helper method --*/

    /**
     * Notifies listeners of drone updates (convenience method).
     */
    private void notifyDroneUpdate() {
        if (myListener != null) {
            myListener.onDroneUpdate(myFleetManger.getDroneFleet());
        }
    }

    /**
     * Generates new telemetry data for all drones in the fleet.
     *
     * @return array of new telemetry data
     */
    private TelemetryData[] generateTelemetry() {
        return myFleetManger.generateFleetData();
    }

    /**
     * Detects anomalies in the provided telemetry data.
     *
     * @param newTelemetry the telemetry data to analyze
     * @return array of detected anomaly records
     */
    private AnomalyRecord[] detectAnomalies(TelemetryData[] newTelemetry) {
        return myAnomalyDetector.analyzeDrones(
                newTelemetry,
                myFleetManger.getDroneFleet(),
                myTimerManger.getElapsedTime(),
                myTimerManger.getUpdateInterval()
        );
    }

    /**
     * Saves all detected anomalies to the database.
     *
     * @param anomalies the anomaly records to save
     */
    private void saveAnomalies(AnomalyRecord[] anomalies) {
        for (AnomalyRecord anomaly : anomalies) {
            // get id of drone that had the anomaly
            int droneID = anomaly.getID();

            // find which drone in the fleet matches the drone id
            Drone affectedDrone = myFleetManger.getDroneById(droneID);

            // save the anomaly
            if (affectedDrone != null) {
                myAnomalyDB.saveAnomaly(anomaly, affectedDrone);
            } else {
                System.err.println("Warning: Could not save anomaly - Drone ID " + droneID + " not found in fleet");
            }
        }
    }

    /**
     * Updates the fleet with new telemetry data.
     *
     * @param newTelemetry the new telemetry data
     */
    private void updateFleet(TelemetryData[] newTelemetry) {
        myFleetManger.updateFleetData(newTelemetry);
    }

    /**
     * Notifies listeners of drone updates and any detected anomalies.
     *
     * @param anomalies the anomalies detected in this update cycle
     */
    private void notifyListeners(AnomalyRecord[] anomalies) {
        if (myListener != null) {
            // Update the display
            myListener.onDroneUpdate(myFleetManger.getDroneFleet()); // Pass the fleet

            if (anomalies.length > 0) {
                // Update the anomaly
                myListener.onAnomaliesDetected(anomalies);
            }
        }
    }
}