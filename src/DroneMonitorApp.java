import Model.Drone;
import Model.TelemetryData;
import Simulation.TelemetryGenerator;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

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
    private static String mySimStatus;


    /* CONSTANT */

    /** Represent the Time interval to update for drone telemetry data */
    private static final int UPDATE_INTERVAL = 3;

    /** Represent the Time Interval for the timer */
    private static final int TIMER_INTERVAL = 1;

    /** Represent the # of drones to create */
    private static final int DRONE_COUNT = 3;


    /* TIME */

    /** The timer schedule being in used to update drone telemetry data */
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /** Represents the time when the simulation started */
    private static long myStart;

    /** Represent te timer when the simulation is paused */
    private static long myPausedTime = 0;

    /* TEST */

    /* METHODS: Sim different phases */

    /* Method to start the sim */
    public static void startSim() {
        mySimStatus = "Running";
        myStart = System.currentTimeMillis();
        createDrones();

        // This to update the time every second
        scheduler.scheduleAtFixedRate(DroneMonitorApp::getElapsedTime, 0, TIMER_INTERVAL, TimeUnit.SECONDS);

        // To initialize drone attitude at the 3-second marks
        scheduler.schedule(DroneMonitorApp::initializeDroneAltitude, UPDATE_INTERVAL, TimeUnit.SECONDS);

        // Update base on the update interval of the drone
        scheduler.scheduleAtFixedRate(DroneMonitorApp::updateDrone, UPDATE_INTERVAL * 2, UPDATE_INTERVAL, SECONDS);


        scheduler.scheduleAtFixedRate(DroneMonitorApp::updateDisplay, 0, 1, SECONDS);


    }

    /** Method to pause the sim */
    public static void pausedSim() {
        if (!"Running".equals(mySimStatus)) return;
        mySimStatus = "Paused";
        myPausedTime = System.currentTimeMillis();
    }

    public static void continueSim() {
        if (!"Paused".equals(mySimStatus)) return;
        mySimStatus = "Running";
    }

    /** Method to end the sim */
    private void stopSim() {
        if ("End".equals(mySimStatus)) return;
        mySimStatus = "End";
    }

    /** Method start a testSim */
    public static void testSimContinuous() {
        mySimStatus = "Test";
        myStart = System.currentTimeMillis();

        // Task every second for the timer
        scheduler.scheduleAtFixedRate(DroneMonitorApp::getElapsedTime, 0, TIMER_INTERVAL, SECONDS);

        // Schedule the display update task with an initial delay of 0 seconds
        scheduler.scheduleAtFixedRate(() -> updateDisplayTester(DroneMonitorApp.sequenceTest()), 0, 1, SECONDS);
    }


    /* Helper Method to set up sim */

    /* TIMER SETUP */
    // Helper method to help handle the running stop watch
    private static int getElapsedTime() {
        long now = System.currentTimeMillis();
        long elapsedTime = now - myStart;

        if (mySimStatus.equals("Paused")) {
            elapsedTime -= (now - myPausedTime);
        }

        return (int) (elapsedTime / 1000);

    }

    private static void differentTypesOfDate() {
        // Print to show the time running working correctly and accurately
        System.out.println("Elapsed time: " + (int) getElapsedTime() + " seconds");
        System.out.println(LocalTime.now());
        System.out.println(LocalTime.now(ZoneId.of("America/Los_Angeles")));


        LocalDateTime now = LocalDateTime.now();
        String formatted = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(formatted);
    }

    /* DRONE SETUP */

    // Helper method to initialize drone objects in our drone fleet array
    private static void createDrones() {
        for (int i = 0; i < DRONE_COUNT; i++) {
            myDroneFleet[i] = new Drone();
        }
    }

    // Helper method to initialize each drone altitude (Getting drone in the air)
    private static void initializeDroneAltitude() {
        for (Drone indivDrone : myDroneFleet) {
            indivDrone.updateTelemetryData(myTelemetryGen.generateStartAltitude());
        }
    }

    // Helper method to update the drone task (The logic for the update in each interval)
    private static void updateDrone() {
        // An array of new telemetry data for each drone, which will be passed to anomaly detector
        TelemetryData[] droneNewTelemetry = new TelemetryData[DRONE_COUNT];

        // Used to track the array count
        int counter = 0;

        for (Drone indivDrone : myDroneFleet) {
            // Passing over current drone telemetry so we get a new one for the drone
            TelemetryData newDroneTelemetry = myTelemetryGen.generateTelemetryData(indivDrone.getDroneTelemetry());

            // Adding the new telemetry generated to an array
            droneNewTelemetry[counter] = newDroneTelemetry;

            counter++;
        }

        // Passing: new Telemetry, drone fleet, current time elapsed, and time interval
        AnomalyRecord[] anomalyList = myAnomalyDetector.analyzeDrones(droneNewTelemetry, myDroneFleet, getElapsedTime(), UPDATE_INTERVAL);

        // TODO: Handling the storing of anomaly to DB
        if (anomalyList.length != 0) {
            myAnomalyDB.saveAnomalies(anomalyList);
        }
        for (AnomalyRecord anomalyRecord : anomalyList) {
            MyJavaFXApp.getInstance().addAnomalyText(anomalyRecord);
        }
        // Reset the counter
        counter = 0;

        for (Drone indivDrone : myDroneFleet) {
            indivDrone.updateTelemetryData(droneNewTelemetry[counter]);
            counter++;
        }
        updateDisplay();
    }

    // Helper method to send new data to the front end
    private static void updateDisplay() {
        // TODO: Temp solution in passing just the first drone in the drone fleet array
        MyJavaFXApp.getInstance().updateStatsText(myDroneFleet[0]);

    }

    /* TESTER  METHODS */

    // Test method that generates new drone setup
    private static Drone sequenceTest() {
        Drone startDrone = new Drone();

        int elapsedTime = getElapsedTime();

        if (elapsedTime < 1) {
            startDrone = new Drone();
            startDrone.updateTelemetryData(myTelemetryGen.generateStartAltitude());
            return startDrone;
        } else if (elapsedTime >= 6) {
            Drone newDrone = new Drone();
            newDrone.updateTelemetryData(myTelemetryGen.generateStartAltitude());
            TelemetryData newTelemetry = myTelemetryGen.generateTelemetryData(newDrone.getDroneTelemetry());

            newDrone.updateTelemetryData(newTelemetry);
            return newDrone;
        } else {
            startDrone.updateTelemetryData(myTelemetryGen.generateStartAltitude());
            return startDrone;
        }
    }

    // Helper method to send over to the display the newly created drone
    private static void updateDisplayTester(final Drone theNewDroneTest) {
        MyJavaFXApp.getInstance().updateStatsText(theNewDroneTest);
    }


    /* MAIN */

    //Just testing out the configuration between Drone, TelemetryData, and TelemetryGenerator
    public static void main(String[] args) {
        DroneMonitorApp app = new DroneMonitorApp();
        for (int i = 0; i < 21; i++) {
            printDroneData(app);
        }
    }


    // Helper method to verify and test the accurate of the data
    private static void printDroneData(final DroneMonitorApp myDroneApp) {
        for (Drone droneData : myDroneFleet) {
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