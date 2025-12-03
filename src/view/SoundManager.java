package view;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

/**
 * Handles the Simulation Sound and all sound related functionality.
 *
 * @version Autumn 2025
 */
public class SoundManager {
    /* ====================================
    FIELDS
     ====================================*/

    /**
     * Whether the audio for our UI is muted or not.
     */
    public boolean myIsMuted = false;

    /**
     * Volume of our UI audio
     */
    private int myVolume = 50;

    /**
     * A media player instance to help play the notification sound
     */
    private MediaPlayer myNotificationPlayer;


    /* ====================================
    GETTERS and SETTERS Methods
     ====================================*/

    /**
     * Gets the current volume.
     *
     * @return the current volume as an int percent.
     */
    public int getVolume() {
        return myVolume;
    }

    /**
     * Gets the muted status.
     *
     * @return the current mute status as a boolean.
     */
    public boolean isMuted() {
        return myIsMuted;
    }

    /**
     * Sets the Mute status.
     *
     * @param theMuted represent whether the mute is true or false.
     */
    public void setMuted(final boolean theMuted) {
        myIsMuted = theMuted;
    }

    /**
     * Sets the volume.
     *
     * @param theNewVolume represents the new volume level.
     */
    public void setVolume(Integer theNewVolume) {
        myVolume = theNewVolume;
    }


    /* ====================================
    FUNCTIONALITY METHODS
     ====================================*/

    /**
     * Plays the notification sound only given sound manager isn't muted.
     */
    public void playNotificationSound() {
        // Return early if mute status is true
        if (myIsMuted) return;

        // Ensuring Notification isn't null
        if (myNotificationPlayer == null) {
            // Getting the sound, and checking if it's not null
            URL url = getClass().getResource("Assets/notification.wav");
            if (url == null) {
                System.out.println("ERROR in playNotificationSound(): no notification.wav found");
                return;
            }

            // Getting the media to play
            try {
                Media media = new Media(url.toExternalForm());
                myNotificationPlayer = new MediaPlayer(media);

                myNotificationPlayer.setOnEndOfMedia(() -> myNotificationPlayer.stop());

            } catch (Throwable theException) {
                System.err.println("Audio not working: Ask Oisin (that's me!) for his Run Configuration");

                //Mute when there's an error
                myIsMuted = true;
                return;
            }
        }

        // Ensuring no overlapping playing
        if (myNotificationPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            myNotificationPlayer.stop();
        }

        // Setting the volume and then playing the sound
        myNotificationPlayer.setVolume(myVolume / 100.0);
        myNotificationPlayer.play();
    }
}
