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
    /* A class object that help manage the time system of the simulation */
    private final TimerManager myTimerManager;

    /* A class object that help manage run the tasks needed for the simulation */
    private final SimulationScheduler mySchedulerOperator;

    /**
     *  Public constructor to create the DroneMonitorApp instance
     *
     * @param theTimerManager represents the TimeManager object to manage timer functionality.
     * @param theSchedulerOperator represents the SimulationScheduler object to handle the simulation tasks.
     */
    public DroneMonitorApp(final TimerManager theTimerManager, final SimulationScheduler theSchedulerOperator) {
        myTimerManager = theTimerManager;
        mySchedulerOperator = theSchedulerOperator;
    }

    /* Methods: Different Simulation "Stages/Phases" */

    /** Method to start the simulation */
    public void startSim() {
        myTimerManager.startTimer();
        mySchedulerOperator.setPausedStatus(false);
        mySchedulerOperator.startSimulationTask();
    }

    /** Method to pause the simulation */
    public void pauseSim() {
        mySchedulerOperator.setPausedStatus(true);
        myTimerManager.pauseTimer();
    }

    /** Method to continue the simulation after simulation has been pauseTimer */
    public void continueSim() {
        mySchedulerOperator.setPausedStatus(false);
        myTimerManager.resumeTimer();
    }

    /** Method to stop the simulation all together */
    public void stopSim() {
        myTimerManager.stopTimer();
        mySchedulerOperator.stopSimulationSchedule();
    }
}