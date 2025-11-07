import Model.Drone;
import Model.TelemetryData;
import Simulation.TelemetryGenerator;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A class that handles, manage and manipulate the drone to act and behave their functionality.
 * The class creates drones, generate and update drone telemetry data, analysis the telemetry data,
 * and insert in an anomaly database if needed to.
 *
 * @author James Escudero
 * @version Fall 2025
 */
public class DroneMonitorApp {
    /* Object Classes */

    /** Represent a TelemetryGenerator Object to use */
    private static final TelemetryGenerator myTelemetryGen = new TelemetryGenerator();

    /** Represent a AnomalyDetector object to use */
    private static final AnomalyDetector myAnomalyDetector = new AnomalyDetector();

    /** Represent a AnomalyDB object to use */
    private static final AnomalyDB myAnomalyDB = new AnomalyDB();


    /* VARIABLES */

    /** Represent the current drone being sim on */
    private final static Drone[] myDroneFleet = new Drone[3];;

    /** Represent the state of the sim */
    private static boolean mySimRunning = false;


    /** Represent like a stopwatch of the time that sim has been running for */
    private static int elapsedTimeInSeconds;


    /* CONSTANT */

    /** Represent the Time interval to update for drone telemetry data */
    private static final int UPDATE_INTERVAL = 3;

    /** Represent the # of drones to create */
    private static final int DRONE_COUNT = 3;




    /* TIME */

    /** The timer schedule being in used to update drone telemetry data */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /** Represents the time when the simulation started */
    private static final long myStart = System.currentTimeMillis();


    /* CONSTRUCTOR */

    /**
     * Constructor for the DroneMonitorApp.
     * Initialize the structure of the simulation.
     */
    public DroneMonitorApp() {
        // Tracking the running time when sim started
        runStopWatch();

        startSim();

        endSim();
    }

    // Helper method to handle starting the sim
    private static void startSim() {
        mySimRunning = true;
    }

    // Helper method to handle ending the sim
    private void endSim() {
        mySimRunning = false;
    }


    // Helper method to help handle the running stop watch
    private static void runStopWatch() {
        long end = System.currentTimeMillis();
        elapsedTimeInSeconds = (int) ((int) (end - myStart) / 1000.0);

//        // Print to show the time running working correctly and accurately
//        System.out.println("Elapsed time: " + (int) elapsedTimeInSeconds + " seconds");
//        System.out.println(LocalTime.now());
//        System.out.println(LocalTime.now(ZoneId.of("America/Los_Angeles")));
//
//
//
//        LocalDateTime now = LocalDateTime.now();
//        String formatted = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        System.out.println(formatted);
    }

    /* Create the # of drones' base on the constant DRONE_COUNT,
       and then add the newly created Drone Object to the Drone Fleet Array.
     */
    private void createDroneObject() {
        for (int i = 1; i <= DRONE_COUNT; i++) {
            myDroneFleet[i] = new Drone();
        }
    }

    /* Helper method to update each Drone in the array of its telemetry data.
       Generate a new telemetry data object from telemetry generator, and
       then pass it through the drone method to update its telemetry data.
    */
    private void updateDroneTelemetry() {

        TelemetryData[] droneCurrTelemetry = new TelemetryData[3];


        for (int i = 0; i < 3; i++) {
            // Grabbing the current drone telemetry data
            TelemetryData currentTelemetry = myDroneFleet[i].getDroneTelemetry();
            droneCurrTelemetry[i] = currentTelemetry;
            // Generate new telemetry data for each drone
            TelemetryData newTelemetry = myTelemetryGen.generateTelemetryData(currentTelemetry);

            int droneID = myDroneFleet[i].getDroneID();
            int droneBatteryLevel = myDroneFleet[i].getBatteryLevel();

            // My thinking, One method that does analyze drone, then you handle all the analyzing,
            // I shouldn't be tracking to what detection I should be doing...
            // Anomaly detection should be doing all of that inside, plus, its good idea not for me to
            // know the process of AnomalyDetector

            // What im passing, Current Telemetry Data, New Telemetry Data, Drone ID, Battery
            // TODO: FIX the parameter passing

            //List<AnomalyRecord> newRecord = myAnomalyDetector.analyzeDrones(droneCurrTelemetry, newTelemetry, 5, 4);

        }



//      other ideas to handle the updateDrone
//        for (Drone drone : myDroneFleet) {
//            for (TelemetryData telemetryData : myNewTelemetryData) {
//               drone.updateTelemetryData(telemetryData);
//            }
//        }
//
//        for (int i = 0; i < 3; i++) {
//            Drone currDrone = myDroneFleet[i];
//            TelemetryData currTelemetryData = myNewTelemetryData[i];
//            currDrone.updateTelemetryData(currTelemetryData);
//
//        }
    }

    // TODO
    // Case holder to add the anomaly record to the Database
    private void addToDB(List<AnomalyRecord> theAnomalyRecord) {

    }

    /* MAIN */

    // Just testing out the configuration between Drone, TelemetryData, and TelemetryGenerator
    public static void main(String[] args) {
//        DroneMonitorApp app = new DroneMonitorApp();
//
//        printDroneData(app);
//
//        app.updateDroneTelemetry();
//
//        System.out.println("\n");
//        printDroneData(app);
    }

    // Helper method to verify and test the accurate of the data
    private static void printDroneData(final DroneMonitorApp myDroneApp) {
        for (Drone droneData : myDroneApp.myDroneFleet) {
            TelemetryData droneTelemetryData = droneData.getDroneTelemetry();
            System.out.println("Drone ID: " + droneData.getDroneID());
            System.out.println("Altitude: " + droneTelemetryData.getAltitude());
            System.out.println("Longitude: " + droneTelemetryData.getLongitude());
            System.out.println("Orientation: " + droneTelemetryData.getOrientation());
            System.out.println("Velocity: " + droneTelemetryData.getVelocity());
            System.out.println("Latitude: " + droneTelemetryData.getLatitude());
        }
    }
}