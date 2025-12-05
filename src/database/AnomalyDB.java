package database;

import Model.AnomalyRecord;
import Model.TelemetryData;
import Model.Drone;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        String sql = "INSERT INTO drone_anomalies (drone_id, anomaly_method, anomaly_time, altitude, longitude, latitude, orientation, velocity, anomaly_type, details) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            pstmt.setString(10, record.getDetails());

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

    public static void clearDatabase() {
        String sql = "DELETE FROM drone_anomalies";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Database cleared successfully");
        } catch (SQLException e) {
            System.err.println("Error clearing database: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        AnomalyDB db = new AnomalyDB();
        db.clearDatabase();
        AnomalyDB.close();
    }
}
