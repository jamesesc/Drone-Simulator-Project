package App;

import controller.*;
import javafx.application.Application;
import javafx.stage.Stage;
import view.MonitorDash;

public class Simulation extends Application {

    //Start the application
    public static void main(final String[] theArgs) {
        launch(theArgs);
    }

    @Override
    public void start(Stage stage) {
        TimerManager timerManager = new TimerManager();
        DroneFleetManager fleetManager = new DroneFleetManager();
        SimulationScheduler scheduler = new SimulationScheduler(timerManager, fleetManager);
        DroneMonitorApp controller = new DroneMonitorApp(timerManager, scheduler, fleetManager);
        MonitorDash monitorDash = new MonitorDash(controller);
        UpdateUIManager updateUIManager = new UpdateUIManager(monitorDash);

        scheduler.setSimulationListener(updateUIManager);
        monitorDash.initializeSimulation(stage);
    }
}
