package view;

import Model.AnomalyRecord;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Objects;

/**
 * A popup for the database manager of our GUI.
 *
 * @version Autumn 2025
 */
class DatabasePopup {
    /**
     * The stage of the database popup.
     */
    private final Stage myStage;
    /**
     * The table of the database popup, showing the various
     */
    private final TableView<MonitorTableEntry> myTable;
    /**
     * Observable List that lets myFilteredList know of any changes
     */
    private final ObservableList<MonitorTableEntry> myObservableList;
    /**
     * List used for searching through Anomalies of the Database
     */
    private final FilteredList<MonitorTableEntry> myFilteredList;
    /**
     * The MonitorDash GUI that owns this instance.
     */
    private final MonitorDash myMonitor;

    /**
     * Constructor for the Database popup.
     *
     * @param theMonitor The MonitorDash GUI that owns this instance.
     * @param thePrimaryStage The stage of the MonitorDash GUI that owns this instance.
     */
    DatabasePopup(final MonitorDash theMonitor, Stage thePrimaryStage) {
        myMonitor = Objects.requireNonNull(theMonitor, "Monitor is null");

        //Setting up our stage
        myStage = new Stage();
        myStage.initOwner(thePrimaryStage);
        myStage.initModality(Modality.NONE);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setTitle("SQLite Database");

        // When user x, it closes the stage, but resume the program
        myStage.setOnCloseRequest(_ -> {
            myMonitor.togglePauseGame();
            myStage.close();
        });


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

    /**
     * Let the user see the Database Popup.
     */
    void show() {
        myStage.show();
    }

    /**
     * Adding an anomaly record to the table of the Database Popup.
     *
     * @param theRecord The anomaly record we are adding to the table of our popup
     */
    public void addAnomalyRecord(AnomalyRecord theRecord) {
        if (theRecord == null) return;

        //Whether or not the ID is null, otherwise turn it into a String
        String idString = (theRecord.getID() == null) ? "—" : String.valueOf(theRecord.getID());

        //Turn the time into a string
        String timeString = Double.toString(theRecord.getTime());

        //Make a new AnomalyEntry record for our table
        MonitorTableEntry entry = new MonitorTableEntry(
                timeString,
                idString,
                theRecord.getType(),
                theRecord.getDetails()
        );

        Platform.runLater(() -> {
            myObservableList.add(entry);
            myTable.scrollTo(entry);
        });
    }

    /**
     * Clear the table, then fill it with a list of Anomaly Records.
     *
     * @param theRecords The records the table will be filled with
     */
    public void refreshAnomalyRecords(List<String[]> theRecords) {
        if (theRecords == null) return;

        List<MonitorTableEntry> entries = theRecords.stream()
                .map(this::convert)
                .toList();

        Platform.runLater(() -> {
            myObservableList.setAll(entries);
            if (!entries.isEmpty()) {
                myTable.scrollTo(entries.getLast());
            }
        });
    }

    /**
     * Helper method, converts AnomalyRecords to MonitorTableEntries
     *
     * @param theAnomalyRecord The anomaly record we are converting
     * @return The converted anomaly record as a monitor table entry.
     */
    private MonitorTableEntry convert(String[] theAnomalyRecord) {
        return new MonitorTableEntry(
                theAnomalyRecord[0],
                theAnomalyRecord[1],
                theAnomalyRecord[2],
                theAnomalyRecord[3]
        );
    }

    /**
     * Search through the filtered list.
     *
     * @param theCategory The category in which we are looking
     * @param theText What we are looking for
     */
    private void applySearch(String theCategory, String theText) {
        myFilteredList.setPredicate(entry -> {
            if (theText == null || theText.isEmpty() || theCategory == null) return true;

            String checking = theText.toLowerCase();

            return switch (theCategory) {
                case "Timestamp" -> entry.getTimestamp().toLowerCase().contains(checking);
                case "Drone ID" -> entry.getDroneId().toLowerCase().contains(checking);
                case "Type" -> entry.getType().toLowerCase().contains(checking);
                case "Details" -> entry.getDetails().toLowerCase().contains(checking);
                default -> true;
            };
        });
    }

    /**
     * Helper method for our database popup's menubar.
     * Decided against making its own class, since it's a lot simpler.
     *
     * @param thePopupStage The main stage belonging to this instance
     * @return The built MenuBar
     */
    private MenuBar buildMenuBar(Stage thePopupStage) {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem closeItem = new MenuItem("Close");
        closeItem.setOnAction(_ -> {
            thePopupStage.close();
            myMonitor.togglePauseGame();
        });

        fileMenu.getItems().addAll(closeItem);
        menuBar.getMenus().addAll(fileMenu);

        menuBar.getStyleClass().add("menu-bar");

        return menuBar;
    }

    /**
     * Helper Method that builds the control strip for the
     * database popup (the search bar pretty much).
     *
     * @return A HBox containing all elements of our control strip
     */
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

        // Container holding our stuff
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