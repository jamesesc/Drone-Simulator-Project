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
        MonitorDash monitorDash = new MonitorDash();
        UpdateUIManager updateUIManager = new UpdateUIManager(monitorDash);
        SimulationScheduler scheduler = new SimulationScheduler(timerManager, fleetManager);

        scheduler.setSimulationListener(updateUIManager);

        DroneMonitorApp controller = new DroneMonitorApp(timerManager, scheduler, fleetManager);

        monitorDash.setController(controller);

        // Connect the view "Request" to the Controller's "Action"
        monitorDash.setMyDroneCountChangeRequest((newCount) -> {
            // Controller action
            controller.changeDroneCount(newCount);

            // Returning new data back to MonitorDash
            return fleetManager.getDroneFleet();
        });
        monitorDash.initializeSimulation(stage);
    }
}
