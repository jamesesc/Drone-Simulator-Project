package App;

import Model.DroneFactory;
import controller.DroneMonitorApp;
import service.SimulationEngine;
import database.AnomalyDB;
import javafx.application.Application;
import javafx.stage.Stage;
import service.AnomalyDetector;
import service.DroneFleetManager;
import service.TelemetryGenerator;
import service.TimerManager;
import view.UpdateUIManager;
import view.MonitorDash;

/**
 * A program that simulates drones.
 *
 * @version Fall 2025.
 */
public class Simulation extends Application {

    /**
     * Starts the simulation.
     *
     * @param theArgs represents the string of arguments.
     */
    public static void main(final String[] theArgs) {
        launch(theArgs);
    }

    @Override
    public void start(Stage stage) {
        // Creating all the classes
        TelemetryGenerator telemetryGen = new TelemetryGenerator();
        final DroneMonitorApp controller = createDroneMonitorApp(telemetryGen);
        MonitorDash monitorDash = new MonitorDash(controller);
        UpdateUIManager updateUIManager = new UpdateUIManager(monitorDash);

        controller.setSimulationListener(updateUIManager);
        monitorDash.initializeSimulation(stage);
    }

    /**
     * Helper method to initialize all the backend components.
     *
     * @param theTelemetryGen represent the telemetry generator being used in the back end.
     * @return a DroneMonitorApp object, which is the back end controller.
     */
    private static DroneMonitorApp createDroneMonitorApp(final TelemetryGenerator theTelemetryGen) {
        TimerManager timerManager = new TimerManager();
        AnomalyDB anomalyDB = new AnomalyDB();
        AnomalyDetector anomalyDetector = new AnomalyDetector();
        DroneFactory droneFactory = new DroneFactory();
        DroneFleetManager fleetManager = new DroneFleetManager(theTelemetryGen, droneFactory);
        SimulationEngine scheduler = new SimulationEngine(timerManager, fleetManager, anomalyDetector, anomalyDB);
        return new DroneMonitorApp(timerManager, scheduler, fleetManager, anomalyDB);
    }
}
