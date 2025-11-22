package database;

import Model.AnomalyRecord;
import Model.TelemetryData;
import java.sql.*;

public class AnomalyDB {
    //database file location
    //to do: put this into a db.property file, or configuration file for db lite
    private static final String DB_URL = "jdbc:sqlite:drone_anomalies.db";
    //connection to database
    private Connection conn;

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
        altitude: altitude at time of anomaly
        longitude: longitude at time of anomaly
        latitude: latitude at time of anomaly
        orientation: orientation at time of anomaly
        velocity: velocity at time of anomaly
        timestamp: when anomaly occured
         */
        String sql = """
            CREATE TABLE IF NOT EXISTS drone_anomalies (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                drone_id INTEGER,
                altitude REAL,
                longitude REAL,
                latitude REAL,
                orientation REAL,
                velocity REAL,
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

    public boolean saveAnomaly(AnomalyRecord record) {
        String sql = "INSERT INTO drone_anomalies (drone_id, altitude, longitude, latitude, orientation, velocity) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            //TelemetryData data = record.getTelemetryData();

            pstmt.setInt(1, record.getID());
//            pstmt.setDouble(2, data.getAltitude());
//            pstmt.setDouble(3, data.getLongitude());
//            pstmt.setDouble(4, data.getLatitude());
//            pstmt.setDouble(5, data.getOrientation());
//            pstmt.setDouble(6, data.getVelocity());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving anomaly: " + e.getMessage());
            return false;
        }
    }

    public void saveAnomalies(AnomalyRecord[] records) {
        for (AnomalyRecord record : records) {
            saveAnomaly(record);
        }
    }

    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        AnomalyDB db = new AnomalyDB();
        db.close();
    }
}