package controller;

/**
 * A class that handles and manges the simulation logic.
 * The class uses many other helper classes to help function and operate the simulation all together
 *
 * @author James Escudero
 * @version Fall 2025
 */
public class DroneMonitorApp {
    /* Represent the 1 Object instance */
    private static DroneMonitorApp instance;

    /* A class that help manges the time system of the simulation */
    private final TimerManger myTimerManger;

    /* A class that help manges the tasks that needs be done */
    private final SimulationScheduler mySchedulerOperator;

    /** Private constructor to create the DroneMonitorApp */
    private DroneMonitorApp() {
        myTimerManger = TimerManger.getInstance();
        mySchedulerOperator = SimulationScheduler.getInstance();
    }

    /** A method to ensure only 1 DroneMonitorApp is created */
    public static DroneMonitorApp getInstance() {
        if (instance == null) {
            instance = new DroneMonitorApp();
        }
        return instance;
    }

    /* METHOD */

    /** Method to startTimer the simulation */
    public void startSim() {
        myTimerManger.startTimer();
        System.out.println("Working: DroneMonitorApp");
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