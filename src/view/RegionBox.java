//package view;
//
//import Model.Drone;
//import javafx.geometry.Insets;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextArea;
//import javafx.scene.layout.Background;
//import javafx.scene.layout.BackgroundFill;
//import javafx.scene.layout.CornerRadii;
//import javafx.scene.layout.VBox;
//import javafx.scene.paint.Color;
//
///**
// * RegionBox displays telemetry for a single drone in the right-hand panel.
// * It includes a header label and a body text area whose contents are updated
// * when new drone telemetry arrives.
// */
//public class RegionBox extends VBox {
//
//    private final int droneId;
//    private final Label headerLabel;
//    private final TextArea bodyArea;
//
//    public RegionBox(String headerText, int id) {
//        this.droneId = id;
//
//        setPadding(new Insets(8));
//        setSpacing(6);
//        setPrefHeight(90);
//
//        // Background / styling (easily swapped to CSS)
//        setBackground(new Background(
//                new BackgroundFill(Color.WHITESMOKE, new CornerRadii(10), Insets.EMPTY)
//        ));
//
//        // ---------- Header ----------
//        headerLabel = new Label(headerText);
//        headerLabel.getStyleClass().add("box-header");
//        headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
//
//        // ---------- Body Text ----------
//        bodyArea = new TextArea();
//        bodyArea.setEditable(false);
//        bodyArea.setFocusTraversable(false);
//        bodyArea.setWrapText(true);
//        bodyArea.setPrefHeight(70);
//        bodyArea.setStyle("""
//                -fx-control-inner-background: transparent;
//                -fx-background-insets: 0;
//                -fx-background-color: transparent;
//                -fx-font-size: 12;
//                """);
//
//        // Initial idle state:
//        bodyArea.setText("Waiting for telemetry...");
//
//        getChildren().addAll(headerLabel, bodyArea);
//    }
//
//    /**
//     * Update this box with a new Drone’s telemetry.
//     */
//    public void updateDroneRegionInfo(Drone drone) {
//        if (drone == null) {
//            bodyArea.setText("No data available");
//            return;
//        }
//
//        bodyArea.setText(
//                "Battery:   " + drone.getBattery() + "%\n" +
//                        "Altitude:  " + drone.getAltitude() + " m\n" +
//                        "Position:  (" + drone.getLatitude() + ", " + drone.getLongitude() + ")\n" +
//                        "Heading:   " + drone.getOrientation() + "°\n" +
//                        "Speed:     " + drone.getSpeed() + " m/s"
//        );
//    }
//
//    /**
//     * Style update for status (optional extension).
//     */
//    public void setHighlight(boolean on) {
//        if (on) {
//            setBackground(new Background(
//                    new BackgroundFill(Color.LIGHTYELLOW, new CornerRadii(10), Insets.EMPTY)
//            ));
//        } else {
//            setBackground(new Background(
//                    new BackgroundFill(Color.WHITESMOKE, new CornerRadii(10), Insets.EMPTY)
//            ));
//        }
//    }
//
//    public int getDroneId() {
//        return droneId;
//    }
//}
