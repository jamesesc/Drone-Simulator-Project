package view;

import Model.AnomalyRecord;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;
import java.util.Objects;

class BottomTable extends VBox {
    /**
     * The text area showing Drone anomalies.
     */
    private final TableView<MonitorTableEntry> myAnomalyTable;

    BottomTable() {
        //Self setup
        setPrefHeight(250);
        setMinHeight(250);
        setMaxHeight(250);
        VBox.setVgrow(this, Priority.NEVER);
        HBox.setHgrow(this, Priority.ALWAYS);

        //The table that'll show us all our AnomalyRecords
        myAnomalyTable = new TableView<>();
        myAnomalyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
        VBox.setVgrow(myAnomalyTable, Priority.ALWAYS);

        //Setting up each column of the Anomaly Table
        TableColumn<MonitorTableEntry, String> col1 = new TableColumn<>("Timestamp");
        TableColumn<MonitorTableEntry, String> col2 = new TableColumn<>("Drone ID");
        TableColumn<MonitorTableEntry, String> col3 = new TableColumn<>("Type");
        TableColumn<MonitorTableEntry, String> col4 = new TableColumn<>("Severity");
        TableColumn<MonitorTableEntry, String> col5 = new TableColumn<>("Details");

        col1.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        col2.setCellValueFactory(new PropertyValueFactory<>("droneId"));
        col3.setCellValueFactory(new PropertyValueFactory<>("type"));
        col4.setCellValueFactory(new PropertyValueFactory<>("severity"));
        col5.setCellValueFactory(new PropertyValueFactory<>("details"));

        myAnomalyTable.getColumns().addAll(List.of(col1, col2, col3, col4, col5));
        myAnomalyTable.getStyleClass().add("dark-table");

        //Box that'll be holding everything at the bottom
        VBox anomalyBox = new VBox();
        anomalyBox.getStyleClass().add("rounded-box");
        VBox.setVgrow(anomalyBox, Priority.ALWAYS);

        //Header to make the bottom section look nice :)
        Label anomalyHeader = new Label("Anomaly Log");
        anomalyHeader.getStyleClass().add("box-header");

        //Size stuff
        VBox.setVgrow(myAnomalyTable, Priority.ALWAYS);
        anomalyBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(anomalyBox, Priority.ALWAYS);

        //Giving the main box for the bottom its children
        anomalyBox.getChildren().addAll(anomalyHeader, myAnomalyTable);

        //setFillHeight(true);
        getChildren().add(anomalyBox);
        setMaxWidth(Double.MAX_VALUE);
    }

    /**
     * Add an anomaly record to the table in the GUI.
     *
     * @param theRecord The anomaly record we'll be adding.
     */
    void addAnomalyRecord(AnomalyRecord theRecord) {
        //Whether or not the ID is null, otherwise turn it into a String
        String idString = (theRecord.getID() == null) ? "—" : String.valueOf(theRecord.getID());

        //Turn the time into a string
        String timeString = Double.toString(theRecord.getTime());

        //Make a new AnomalyEntry record for our table
        MonitorTableEntry entry = new MonitorTableEntry(
                timeString,
                idString,
                theRecord.getType(),
                theRecord.getSeverity(),
                theRecord.getDetails()
        );

        Platform.runLater(() -> {
            //Add our entry and scroll to it
            myAnomalyTable.getItems().add(entry);
            myAnomalyTable.scrollTo(entry);
        });
    }

    /**
     * Clears the anomaly table in the GUI, then replaces its contents with the given List.
     *
     * @param theRecords What we want the contents to be.
     */
    void refreshAnomalyRecords(List<AnomalyRecord> theRecords) {
        if (theRecords == null || theRecords.isEmpty()) { return; }

        myAnomalyTable.getItems().clear();

        for (AnomalyRecord record : theRecords) {
            addAnomalyRecord(record);
        }
    }

    TableView<MonitorTableEntry> getAnomalyTable() {
        return myAnomalyTable;
    }

    public void exportToCSVDialog(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV");

        // Set initial folder to current working directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        // Set default file name
        fileChooser.setInitialFileName("anomalies.csv");

        // Restrict file types to CSV
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            exportToCSV(file.getAbsolutePath());
        }
    }

    public void exportToTXTDialog(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save TXT");

        // Default to the working directory (same folder as application)
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setInitialFileName("anomalies.txt");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            exportToTXT(file.getAbsolutePath());
        }
    }

    private void exportToTXT(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            // Header line
            writer.write("Timestamp | Drone ID | Type | Severity | Details");
            writer.newLine();
            writer.write("-------------------------------------------------------------");
            writer.newLine();

            // Table entries
            for (MonitorTableEntry entry : myAnomalyTable.getItems()) {
                String line = entry.getTimestamp() + " | "
                        + entry.getDroneId() + " | "
                        + entry.getType() + " | "
                        + entry.getSeverity() + " | "
                        + entry.getDetails();
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportToCSV(String filePath) {
        List<MonitorTableEntry> entries = myAnomalyTable.getItems();

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.append("Timestamp,Drone ID,Type,Severity,Details\n");

            // Write each row
            for (MonitorTableEntry entry : entries) {
                writer.append(entry.getTimestamp()).append(",");
                writer.append(entry.getDroneId()).append(",");
                writer.append(entry.getType()).append(",");
                writer.append(entry.getSeverity()).append(",");
                writer.append(entry.getDetails().replaceAll(",", ";")).append("\n");
            }

            writer.flush();
            System.out.println("CSV Exported to: " + filePath);
        } catch (IOException e) {
            System.out.println("Error in exportToCSV: " + e);
        }
    }

    void applyStylesheet(String cssName) {
        this.getStylesheets().clear();
        this.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource(cssName)).toExternalForm()
        );
    }
}
