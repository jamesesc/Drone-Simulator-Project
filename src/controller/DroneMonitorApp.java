package controller;

/**
 * A Controller class that handles and mange the core system of the simulation.
 * The class uses many helper classes to help build the functionality and operation
 * of the simulation all together.
 *
 * @author James Escudero
 * @version Fall 2025
 */
public class DroneMonitorApp {
    /* Represent the DroneMonitorApp instance */
    private static DroneMonitorApp instance;

    /* A class object that help manage the time system of the simulation */
    private final TimerManger myTimerManger;

    /* A class object that help manage run the tasks needed for the simulation */
    private final SimulationScheduler mySchedulerOperator;

    /** Private constructor to create the DroneMonitorApp instance */
    private DroneMonitorApp() {
        myTimerManger = TimerManger.getInstance();
        mySchedulerOperator = SimulationScheduler.getInstance();
    }

    /**
     * A method to ensure only one TimeManger instance is created.
     *
     * @return the single instance of the DroneMonitorApp.
     */
    public static DroneMonitorApp getInstance() {
        if (instance == null) {
            instance = new DroneMonitorApp();
        }
        return instance;
    }

    /* Methods: Different Simulation "Stages/Phases" */

    /** Method to start the simulation */
    public void startSim() {
        myTimerManger.startTimer();
        mySchedulerOperator.startSimulationTask();
    }

    /** Method to pause the simulation */
    public void pauseSim() {
        myTimerManger.paused();
    }

    /** Method to continue the simulation after simulation has been paused */
    public void continueSim() {
        myTimerManger.resume();
    }

    /** Method to stop the simulation all together */
    public void stopSim() {
        myTimerManger.stopTimer();
    }
}