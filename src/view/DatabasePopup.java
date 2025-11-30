package view;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Objects;

public class DatabasePopup {
    private final int CONTROL_WIDTH = 150;
    private Stage stage;
    private TableView<MonitorTableEntry> table;
    private FilteredList<MonitorTableEntry> filteredList;
    private SortedList<MonitorTableEntry> sortedList;

    private TableColumn<MonitorTableEntry, String> colTimestamp;
    private TableColumn<MonitorTableEntry, String> colDroneId;
    private TableColumn<MonitorTableEntry, String> colType;
    private TableColumn<MonitorTableEntry, String> colSeverity;
    private TableColumn<MonitorTableEntry, String> colDetails;


    DatabasePopup(Stage thePrimaryStage) {
        //Setting up our stage
        stage = new Stage();
        stage.initOwner(thePrimaryStage);
        stage.initModality(Modality.NONE);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("SQLite Database");

        //Building out our sections
        MenuBar menuBar = buildMenuBar(stage);
        HBox controlStrip = buildControls();

        //Table setup
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);

        colTimestamp = new TableColumn<>("Timestamp");
        colDroneId = new TableColumn<>("Drone ID");
        colType = new TableColumn<>("Type");
        colSeverity = new TableColumn<>("Severity");
        colDetails = new TableColumn<>("Details");

        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colDroneId.setCellValueFactory(new PropertyValueFactory<>("droneId"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colSeverity.setCellValueFactory(new PropertyValueFactory<>("severity"));
        colDetails.setCellValueFactory(new PropertyValueFactory<>("details"));

        table.getColumns().addAll(List.of(colTimestamp, colDroneId, colType, colSeverity, colDetails));
        table.getStyleClass().add("dark-table");

        // Later, when data is loaded, wrap in filteredList
        filteredList = new FilteredList<>(FXCollections.observableArrayList(), p -> true);
        //filteredList.getSource().setAll(resultsFromDB);

        sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sortedList);

        //Setting the scene
        BorderPane root = new BorderPane();
        root.setTop(new VBox(menuBar, controlStrip));
        root.setCenter(table);

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("dark_theme.css")).toExternalForm()
        );

        root.getStyleClass().add("root");

        stage.setScene(scene);
    }

    void show() {
        stage.show();
    }

    TableView<MonitorTableEntry> getTable() {
        return table;
    }

    //TODO:
    /*
    public void loadData(List<MonitorTableEntry> items) {
    if (filteredList == null) {
        filteredList = new FilteredList<>(FXCollections.observableArrayList(items), p -> true);
        sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);
    } else {
        filteredList.getSource().setAll(items);
    }
}
     */

    private void applySort(ComboBox<String> sortBox) {
        TableColumn<MonitorTableEntry, ?> column = switch (sortBox.getValue()) {
            case "Timestamp" -> colTimestamp;
            case "Drone ID" -> colDroneId;
            case "Type" -> colType;
            case "Severity" -> colSeverity;
            case "Details" -> colDetails;
            default -> null;
        };

        if (column != null) {
            table.getSortOrder().setAll(column);
            column.setSortType(TableColumn.SortType.ASCENDING);
        }
    }


    private void applySearch(String category, String text) {
        if (filteredList == null) return;

        filteredList.setPredicate(entry -> {
            if (text == null || text.isEmpty() || category == null) return true;

            String lower = text.toLowerCase();

            return switch (category) {
                case "Timestamp" -> entry.getTimestamp().toLowerCase().contains(lower);
                case "Drone ID" -> entry.getDroneId().toLowerCase().contains(lower);
                case "Type" -> entry.getType().toLowerCase().contains(lower);
                case "Severity" -> entry.getSeverity().toLowerCase().contains(lower);
                case "Details" -> entry.getDetails().toLowerCase().contains(lower);
                default -> true;
            };
        });
    }


    private MenuBar buildMenuBar(Stage thePopupStage) {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem exportItem = new MenuItem("Export");
        MenuItem closeItem = new MenuItem("Close");
        closeItem.setOnAction(_ -> thePopupStage.close());

        fileMenu.getItems().addAll(exportItem, closeItem);
        menuBar.getMenus().addAll(fileMenu);

        menuBar.getStyleClass().add("menu-bar");

        return menuBar;
    }

    private HBox buildControls() {
        //Sort by section
        Label sortLabel = new Label("Sort by:  ");
        sortLabel.getStyleClass().add("time-label");

        ComboBox<String> sortBox = new ComboBox<>();
        sortBox.getItems().addAll("Timestamp", "Drone ID", "Type", "Severity", "Details");
        sortBox.setOnAction(_ -> applySort(sortBox)); //The action
        sortBox.setPrefWidth(CONTROL_WIDTH);

        HBox sortContainer = new HBox();
        sortContainer.getChildren().addAll(sortLabel, sortBox);
        sortContainer.getStyleClass().add("main-box");

        //Search section
        Label searchLabel = new Label("Search:  ");
        searchLabel.getStyleClass().add("time-label");

        ComboBox<String> searchBox = new ComboBox<>();
        searchBox.getItems().addAll("Timestamp", "Drone ID", "Type", "Severity", "Details");
        searchBox.setPrefWidth(CONTROL_WIDTH);

        TextField searchField = new TextField();
        searchField.setPromptText("Enter text here...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            applySearch(searchBox.getValue(), newVal);
        });
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
        controlStrip.getChildren().addAll(sortContainer, searchContainer);
        controlStrip.setMaxWidth(Double.MAX_VALUE);
        controlStrip.setSpacing(10);
        HBox.setHgrow(controlStrip, Priority.ALWAYS);
        controlStrip.getStyleClass().add("rounded-box");

        return controlStrip;
    }
}
