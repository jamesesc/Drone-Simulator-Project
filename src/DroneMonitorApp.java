import Model.Drone;
import Model.TelemetryData;
import Simulation.TelemetryGenerator;

import java.util.Timer;

public class DroneMonitorApp {

    private final TelemetryGenerator myTelemetryGen = new TelemetryGenerator();

    private final AnomalyDectector myAnomalyDectector = new AnomalyDectector();

    private final AnomalyDB myAnomalyDB = new AnomalyDB();

    private final Timer myTime = new Timer();

    private boolean mySimRunning = false;

    private final int myUpdateInterval = 3;

    private final Drone[] myDroneFleet;

    DroneMonitorApp() {
        myDroneFleet = new Drone[3];
        droneCreate();
        updateDroneTelemetryData();
        start();
    }

    private void droneCreate() {
        for (int i = 0; i < 3; i++) {
            Drone newDrone = new Drone();
            myDroneFleet[i] = newDrone;
        }
    }

    private void updateDroneTelemetryData() {
        for (Drone droneSim : myDroneFleet) {
            TelemetryData telemetryData = myTelemetryGen.generateTelemetryData();
            droneSim.updateTelemetryData(telemetryData);
        }

    }

    private void start() {
        mySimRunning = true;
    }

    private void end() {
        mySimRunning = false;
    }

    /* ADDED based on the UML Design */
    private void storeAnomalies() {

    }


    // Just testing out the configuration between Drone, TelemetryData, and TelemetryGenerator
    public static void main(String[] args) {
        DroneMonitorApp app = new DroneMonitorApp();

        printDroneData(app);

        app.updateDroneTelemetryData();

        System.out.println("\n");
        printDroneData(app);
    }

    private static void printDroneData(final DroneMonitorApp myDroneApp) {
        for (Drone droneData : myDroneApp.myDroneFleet) {
            TelemetryData droneTelemetryData = droneData.getMyDroneTelemetryData();
            System.out.println("Drone ID: " + droneData.getDroneID());
            System.out.println("Altitude: " + droneTelemetryData.getAltitude());
            System.out.println("Longitude: " + droneTelemetryData.getLongitude());
            System.out.println("Orientation: " + droneTelemetryData.getOrientation());
            System.out.println("Velocity: " + droneTelemetryData.getVelocity());
            System.out.println("Latitude: " + droneTelemetryData.getLatitude());
        }
    }
}