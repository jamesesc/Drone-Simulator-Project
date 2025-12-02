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
    public void start(Stage stage) throws Exception {
        TimerManager timerManager = new TimerManager();
        DroneFleetManager fleetManager = new DroneFleetManager();
        MonitorDash monitorDash = new MonitorDash();
        UpdateUIManager updateUIManager = new UpdateUIManager(monitorDash, fleetManager);
        SimulationScheduler scheduler = new SimulationScheduler(timerManager, fleetManager, updateUIManager);
        DroneMonitorApp controller = new DroneMonitorApp(timerManager, scheduler, fleetManager);

        monitorDash.setController(controller);

        // Connects the View's "Request" to the Controller's "Action"
        monitorDash.setMyDroneCountChangeRequest(controller::changeDroneCount);

        monitorDash.initializeSimulation(stage);
    }
}
