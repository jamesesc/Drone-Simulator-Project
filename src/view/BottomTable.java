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

import java.util.List;

public class BottomTable extends VBox {
    /**
     * The text area showing Drone anomalies.
     */
    private final TableView<MonitorTableEntry> myAnomalyTable;

    public BottomTable() {
        //Self setup
        setPrefHeight(150);
        setMinHeight(150);
        setMaxHeight(150);
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
    public void addAnomalyRecord(AnomalyRecord theRecord) {
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
    public void refreshAnomalyRecords(List<AnomalyRecord> theRecords) {
        if (theRecords == null || theRecords.isEmpty()) { return; }

        myAnomalyTable.getItems().clear();

        for (AnomalyRecord record : theRecords) {
            addAnomalyRecord(record);
        }
    }

    TableView<MonitorTableEntry> getAnomalyTable() {
        return myAnomalyTable;
    }
}
