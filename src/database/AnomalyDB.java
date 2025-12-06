package database;

import Model.AnomalyRecord;
import Model.Drone;
import Model.TelemetryData;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles SQLite database operations for storing and retrieving drone anomalies.
 * This class is responsible for initializing database, saving anomaly records,
 * getting anomaly details, and clearing or closing the database connection.
 */
public class AnomalyDB {
    //database file location
    //to do: put this into a db.property file, or configuration file for db lite
    private static final String DB_URL = "jdbc:sqlite:drone_anomalies.db";
    /** Connection to the SQLite database. */
    private static Connection conn;

    /**
     * Constructs an AnomalyDB object, loads the SQLite driver,
     * establishes a connection, and creates the anomaly table if it does not exist.
     */
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

    /**
     * Creates the 'drone_anomalies' table if it does not already exist.
     * The table stores telemetry and anomaly information for drones.
     */
    private void createTable() {
        //will not create table if one is already there
        /*
        id: increments automatically for each record
        drone id: which drone had anomaly
        anomaly_method: method where anomaly happened
        anomaly_time: time when anomaly occurred
        altitude: altitude at time of anomaly
        longitude: longitude at time of anomaly
        latitude: latitude at time of anomaly
        orientation: orientation at time of anomaly
        velocity: velocity at time of anomaly
        type: type of anomaly
        severity: severity of anomaly
        details: anomaly details
        timestamp: when anomaly occurred
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

    /**
     * Saves a single anomaly record for a specific drone into the database.
     *
     * @param record the anomaly record to save
     * @param drone the drone associated with the anomaly
     * @return true if the record was saved successfully, false otherwise
     */
    public boolean saveAnomaly(AnomalyRecord record, Drone drone) {
        // SLQ insert command with placeholders for inserting data
        String sql = "INSERT INTO drone_anomalies (drone_id, anomaly_method, anomaly_time, altitude, longitude, latitude, orientation, velocity, anomaly_type, details) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Get telemetry data from drone
            TelemetryData data = drone.getDroneTelemetry();

            // Set the in prepared statementfrom anomaly record and telemetry data
            pstmt.setInt(1, record.getID());
            pstmt.setString(2, record.getType());
            pstmt.setDouble(3, record.getTime());
            pstmt.setDouble(4, data.getAltitude());
            pstmt.setDouble(5, data.getLongitude());
            pstmt.setDouble(6, data.getLatitude());
            pstmt.setDouble(7, data.getOrientation());
            pstmt.setDouble(8, data.getVelocity());
            pstmt.setString(9, record.getType());
            pstmt.setString(10, record.getDetails());

            // Execute SQL insert command
            pstmt.executeUpdate();
            return true; // Success

        } catch (SQLException e) {
            // Print error if something gos wrong
            System.err.println("Error saving anomaly: " + e.getMessage());
            return false; // False if fail
        }
    }

    /**
     * Saves an array of anomaly records for a specific drone into the database.
     *
     * @param records the array of anomaly records to save
     * @param drone the drone associated with the anomalies
     */
    public void saveAnomalies(AnomalyRecord[] records, Drone drone) {
        // Loop through each record and save it
        for (AnomalyRecord record : records) {
            saveAnomaly(record, drone);
        }
    }

    /**
     * Closes the database connection.
     */
    public static void close() {
        try {
            if (conn != null) conn.close(); // Close connection if it exists
        } catch (SQLException e) {
            // Print error if closing fails
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Get all anomaly records from the database.
     *
     * @return a list of String arrays, each representing an anomaly record with
     *         timestamp, drone ID, anomaly type, and details
     */
    public List<String[]> getAnomalyDetails() {
        List<String[]> details = new ArrayList<>();
        // SQL query to get all anomaly records sorted by timestamp (newest first)
        String sql = "SELECT timestamp, drone_id, anomaly_type, details FROM drone_anomalies ORDER BY timestamp DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Loop through each row in the result set
            while (rs.next()) {
                // Store each row's data in a String array
                String[] record = {
                        rs.getString("timestamp"),           // get timestamp
                        String.valueOf(rs.getInt("drone_id")), // get drone ID
                        rs.getString("anomaly_type"),       // get anomaly type
                        rs.getString("details")             // get additional details
                };
                // Add this record to the list
                details.add(record);
            }

        } catch (SQLException e) {
            // Print error if something goes wrong
            System.err.println("Error retrieving anomaly details: " + e.getMessage());
        }
        // Return the list of anomalies
        return details;
    }

    /**
     * Deletes all records from the anomaly database.
     */
    public static void clearDatabase() {
        // SQL command to delete all rows
        String sql = "DELETE FROM drone_anomalies";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql); // Execute delete command
            System.out.println("Database cleared successfully");
        } catch (SQLException e) {
            // Print error if something goes wrong
            System.err.println("Error clearing database: " + e.getMessage());
        }
    }
}
