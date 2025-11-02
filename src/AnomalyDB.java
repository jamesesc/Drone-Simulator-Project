import java.sql.*;

public class AnomalyDB {
    private static final String DB_URL = "jdbc:sqlite:drone_anomalies.db";
    private Connection conn;

    public AnomalyDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
            createTable();
            System.out.println("Database initialized successfully");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void createTable() {
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
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
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