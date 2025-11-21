package controller;

import Model.AnomalyRecord;
import Model.TelemetryData;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulationScheduler {

    /** Represents the object instance */
    private static SimulationScheduler instance;

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

    /** Private constructor to call and use the 1 instance of each object */
    private SimulationScheduler() {
        myScheduleOperation = Executors.newScheduledThreadPool(4);
        myTimerManger = TimerManager.getInstance();
        myFleetManger = DroneFleetManager.getInstance();
        myAnomalyProcessor = AnomalyProcessor.getInstance();
        myUIUpdater = UpdateUIManager.getInstance();
    }

    /** Method to ensure only one object is made */
    public static SimulationScheduler getInstance() {
        if (instance == null) {
            instance = new SimulationScheduler();
        }
        return instance;
    }

    /** Method to start the simulation logic and schedule task */
    public void startSimulationTask() {
        System.out.println("Working: startSimulationTask");
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
        int elapseTime = TimerManager.getInstance().getElapsedTime();

        myUIUpdater.updateTimer(elapseTime);
    }
}