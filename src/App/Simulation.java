package App;

import javafx.application.Application;
import javafx.stage.Stage;
import view.MonitorDash;

public class Simulation extends Application {

    //Start the application
    public static void main(final String[] theArgs) {
        launch(theArgs);
    }

    @Override
    public void start(Stage stage) throws Exception {
        MonitorDash monitorDash = new MonitorDash();
        monitorDash.initializeSimulation(stage);
    }
}
