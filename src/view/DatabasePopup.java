package view;

import Model.AnomalyRecord;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.beans.property.SimpleStringProperty;


import java.util.List;
import java.util.Objects;

public class DatabasePopup {
    private final Stage myStage;
    private final TableView<MonitorTableEntry> myTable;

    private final ObservableList<MonitorTableEntry> myObservableList;
    private final FilteredList<MonitorTableEntry> myFilteredList;

    DatabasePopup(Stage thePrimaryStage) {
        //Setting up our stage
        myStage = new Stage();
        myStage.initOwner(thePrimaryStage);
        myStage.initModality(Modality.NONE);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setTitle("SQLite Database");

        //Building out our sections
        MenuBar menuBar = buildMenuBar(myStage);
        HBox controlStrip = buildControls();

        myObservableList = FXCollections.observableArrayList();
        myFilteredList = new FilteredList<>(myObservableList, _ -> true);

        //Table setup
        myTable = new TableView<>();

        TableColumn<MonitorTableEntry, String> colTimestamp = new TableColumn<>("Timestamp");
        TableColumn<MonitorTableEntry, String> colDroneId = new TableColumn<>("Drone ID");
        TableColumn<MonitorTableEntry, String> colType = new TableColumn<>("Type");
        TableColumn<MonitorTableEntry, String> colDetails = new TableColumn<>("Details");

        colTimestamp.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTimestamp()));
        colDroneId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDroneId()));
        colType.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));
        colDetails.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDetails()));

        myTable.getColumns().addAll(List.of(colTimestamp, colDroneId, colType, colDetails));
        myTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
        myTable.getStyleClass().add("dark-table");
        myTable.setItems(myFilteredList);

        //Setting the scene
        BorderPane root = new BorderPane();
        root.setTop(new VBox(menuBar, controlStrip));
        root.setCenter(myTable);

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("dark_theme.css")).toExternalForm()
        );

        root.getStyleClass().add("root");

        myStage.setScene(scene);
    }

    void show() {
        myStage.show();
    }

    public void addAnomalyRecord(AnomalyRecord record) {
        if (record == null) return;

        //Whether or not the ID is null, otherwise turn it into a String
        String idString = (record.getID() == null) ? "—" : String.valueOf(record.getID());

        //Turn the time into a string
        String timeString = Double.toString(record.getTime());

        //Make a new AnomalyEntry record for our table
        MonitorTableEntry entry = new MonitorTableEntry(
                timeString,
                idString,
                record.getType(),
                record.getDetails()
        );

        Platform.runLater(() -> {
            myObservableList.add(entry);
            myTable.scrollTo(entry);
        });
    }

    public void refreshAnomalyRecords(List<AnomalyRecord> records) {
        if (records == null) return;

        List<MonitorTableEntry> entries = records.stream()
                .map(this::convert)
                .toList();

        Platform.runLater(() -> {
            myObservableList.setAll(entries);
            if (!entries.isEmpty()) {
                myTable.scrollTo(entries.getLast());
            }
        });
    }

    private MonitorTableEntry convert(AnomalyRecord r) {
        String id = (r.getID() == null) ? "—" : r.getID().toString();
        return new MonitorTableEntry(
                Double.toString(r.getTime()),
                id,
                r.getType(),
                r.getDetails()
        );
    }

    private void applySearch(String category, String text) {
        myFilteredList.setPredicate(entry -> {
            if (text == null || text.isEmpty() || category == null) return true;

            String needle = text.toLowerCase();

            return switch (category) {
                case "Timestamp" -> entry.getTimestamp().toLowerCase().contains(needle);
                case "Drone ID" -> entry.getDroneId().toLowerCase().contains(needle);
                case "Type" -> entry.getType().toLowerCase().contains(needle);
                case "Details" -> entry.getDetails().toLowerCase().contains(needle);
                default -> true;
            };
        });
    }

    private MenuBar buildMenuBar(Stage thePopupStage) {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem closeItem = new MenuItem("Close");
        closeItem.setOnAction(_ -> thePopupStage.close());

        fileMenu.getItems().addAll(closeItem);
        menuBar.getMenus().addAll(fileMenu);

        menuBar.getStyleClass().add("menu-bar");

        return menuBar;
    }

    private HBox buildControls() {
        //Search section
        Label searchLabel = new Label("Search:  ");
        searchLabel.getStyleClass().add("time-label");

        ComboBox<String> searchBox = new ComboBox<>();
        searchBox.getItems().addAll("Timestamp", "Drone ID", "Type", "Details");
        searchBox.setPrefWidth(150);
        searchBox.getSelectionModel().selectFirst();

        TextField searchField = new TextField();
        searchField.setPromptText("Enter text here...");
        //Search per key press (we'll see how this does performance wise)
        searchField.textProperty().addListener((_, _, newVal) -> applySearch(searchBox.getValue(), newVal));
        //Search by pressing enter
        searchField.setOnAction(_ -> {
            searchField.clear();
            applySearch(searchBox.getValue(), searchField.getText()); });
        searchField.getStyleClass().add("dark-text-area");

        searchBox.setOnAction(_ -> applySearch(searchBox.getValue(), searchField.getText()));

        HBox searchContainer = new HBox();
        searchContainer.getChildren().addAll(searchLabel, searchBox, searchField);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchContainer.setSpacing(10);
        searchContainer.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(searchContainer, Priority.ALWAYS);
        searchContainer.getStyleClass().add("main-box");

        //HBox we'll be returning
        HBox controlStrip = new HBox();
        controlStrip.getChildren().addAll(searchContainer);
        controlStrip.setMaxWidth(Double.MAX_VALUE);
        controlStrip.setSpacing(10);
        HBox.setHgrow(controlStrip, Priority.ALWAYS);
        controlStrip.getStyleClass().add("rounded-box");

        return controlStrip;
    }
}