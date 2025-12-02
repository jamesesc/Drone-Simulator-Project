package database;

import Model.AnomalyRecord;
import Model.TelemetryData;
import Model.Drone;
import java.sql.*;

public class AnomalyDB {
    //database file location
    //to do: put this into a db.property file, or configuration file for db lite
    private static final String DB_URL = "jdbc:sqlite:drone_anomalies.db";
    //connection to database
    private static Connection conn;

    public AnomalyDB() {
        try {
            Class.forName("org.sqlite.JDBC"); //load sqlite driver
            conn = DriverManager.getConnection(DB_URL); //connect to database
            createTable(); //create table structure
            System.out.println("Database initialized successfully");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage()); //msg if something goes wrong
        }
    }

    private void createTable() {
        //sql command to create table
        //will not create table if one is already there
        /*
        id: increments automatically for each record
        drone id: which drone had anomaly
        anomaly_method: mathod where anomaly happened
        anomaly_time: time when anomaly occured
        altitude: altitude at time of anomaly
        longitude: longitude at time of anomaly
        latitude: latitude at time of anomaly
        orientation: orientation at time of anomaly
        velocity: velocity at time of anomaly
        type: type of anomaly
        severity: severity of anomaly
        details: anomaly details
        timestamp: when anomaly occured
         */
        String sql = """
            CREATE TABLE IF NOT EXISTS drone_anomalies (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                drone_id INTEGER,
                anomaly_method TEXT,
                anomaly_time REAL,
                altitude REAL,
                longitude REAL,
                latitude REAL,
                orientation REAL,
                velocity REAL,
                anomaly_type TEXT,
                severity TEXT,
                details TEXT,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
            )
            """;

        try (Statement stmt = conn.createStatement()) {
            //sql command to create table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    public boolean saveAnomaly(AnomalyRecord record, Drone drone) {
        String sql = "INSERT INTO drone_anomalies (drone_id, anomaly_method, anomaly_time, altitude, longitude, latitude, orientation, velocity, anomaly_type, severity, details) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            TelemetryData data = drone.getDroneTelemetry();

            pstmt.setInt(1, record.getID());
            pstmt.setString(2, record.getType());
            pstmt.setDouble(3, record.getTime());
            pstmt.setDouble(4, data.getAltitude());
            pstmt.setDouble(5, data.getLongitude());
            pstmt.setDouble(6, data.getLatitude());
            pstmt.setDouble(7, data.getOrientation());
            pstmt.setDouble(8, data.getVelocity());
            pstmt.setString(9, record.getType());
            pstmt.setString(11, record.getDetails());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error saving anomaly: " + e.getMessage());
            return false;
        }
    }

    public void saveAnomalies(AnomalyRecord[] records, Drone drone) {
        for (AnomalyRecord record : records) {
            saveAnomaly(record, drone);
        }
    }

    public static void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        AnomalyDB db = new AnomalyDB();

        // Create a drone with telemetry data
        Drone drone = new Drone();
        TelemetryData t = drone.getDroneTelemetry();

        t.setAltitude(150.75);
        t.setLatitude(47.251);
        t.setLongitude(-122.440);
        t.setOrientation(135.0);
        t.setVelocity(22.8);

        // Create an anomaly record
        AnomalyRecord record = new AnomalyRecord("EngineFailure", 1, 325.8);
        record.setDetails("Engine temperature exceeded safe limit.");

        // Save the anomaly
        boolean saved = db.saveAnomaly(record, drone);

        System.out.println("Test anomaly saved? " + saved);

        db.close();
    }
}
