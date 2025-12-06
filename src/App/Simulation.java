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
 * @version Autumn 2025.
 */
public class Simulation extends Application {

    //Start the application
    public static void main(final String[] theArgs) {
        launch(theArgs);
    }

    @Override
    public void start(Stage stage) {
        // Creating all the classes
        TelemetryGenerator telemetryGen = new TelemetryGenerator();
        TimerManager timerManager = new TimerManager();
        AnomalyDB anomalyDB = new AnomalyDB();
        AnomalyDetector anomalyDetector = new AnomalyDetector();
        DroneFactory droneFactory = new DroneFactory();
        DroneFleetManager fleetManager = new DroneFleetManager(telemetryGen, droneFactory);
        SimulationEngine scheduler = new SimulationEngine(timerManager, fleetManager, anomalyDetector, anomalyDB);
        DroneMonitorApp controller = new DroneMonitorApp(timerManager, scheduler, fleetManager, anomalyDB);
        MonitorDash monitorDash = new MonitorDash(controller);
        UpdateUIManager updateUIManager = new UpdateUIManager(monitorDash);

        controller.setSimulationListener(updateUIManager);
        monitorDash.initializeSimulation(stage);
    }
}
