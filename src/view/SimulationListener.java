package controller;

import Model.AnomalyRecord;
import Model.Drone;

public interface SimulationListener {

    void onTimeUpdate(int theElapsedTime);

    void onDroneUpdate(Drone[] theFleet);

    void onAnomaliesDetected(AnomalyRecord[] anomaly);
}
