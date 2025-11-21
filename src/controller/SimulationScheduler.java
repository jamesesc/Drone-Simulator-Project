package controller;

import Model.AnomalyRecord;
import Model.TelemetryData;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulationScheduler {

    /** Represents the java import scheduler use to schedule a task */
    private final ScheduledExecutorService myScheduleOperation;

    /** Represents the timer object to manage the simulation timer */
    private final TimerManager myTimerManger;

    /** Represents the Fleet manger object that manges all drone fleet */
    private final DroneFleetManager myFleetManger;

    /** Represents the AnomalyProcessing object to handle all the anomalies*/
    private final AnomalyProcessor myAnomalyProcessor;

    /** Represent the UpdateUIManager object that sends and update to the UI */
    private final UpdateUIManager myUIUpdater;

    /** Represents the current status of the simulation; true = running, false for stopped */
    private volatile boolean myPausedStatus = false;

    /** Private constructor to call and use the 1 instance of each object */
    public SimulationScheduler(final TimerManager theTimerManager, final DroneFleetManager theFleetManager,
                                AnomalyProcessor theAnomalyProcessor, UpdateUIManager theUIUpdater) {
        myScheduleOperation = Executors.newScheduledThreadPool(4);
        myTimerManger = theTimerManager;
        myFleetManger = theFleetManager;
        myAnomalyProcessor = theAnomalyProcessor;
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

        myUIUpdater.updateDroneDisplay();

        myScheduleOperation.schedule(
                myUIUpdater::updateDroneDisplay,
                3,
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
                updateInterval * 2,
                updateInterval,
                TimeUnit.SECONDS
        );


        /* UI UPDATE TASK */

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
        if (myPausedStatus){
            return;
        }

        try {
            // Checking if it does this
            System.out.println("DEBUG: Executing updateDronesTask...");

            // 1) Generate new telemetry for all drones
            TelemetryData[] newTelemetry = myFleetManger.generateFleetData();

            // 2) Detect anomalies
            AnomalyRecord[] anomalies = myAnomalyProcessor.processAnomalies(
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
            e.printStackTrace();
        }
    }

    private void updateTime() {
        int elapseTime = myTimerManger.getElapsedTime();
        myUIUpdater.updateTimer(elapseTime);
    }

    public void stopSimulationSchedule() {
        myScheduleOperation.shutdown();
    }
}