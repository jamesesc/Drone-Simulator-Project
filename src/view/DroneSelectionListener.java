package view;

/**
 * Interface for handling drone selection events in the UI.
 *
 * @version Autumn 2025
 */
public interface DroneSelectionListener {
    /**
     * Called when a specific drone is selected by the user.
     *
     * @param droneId the unique ID of the selected drone.
     */
    void onDroneSelected(int droneId);
}
