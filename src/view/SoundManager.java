package view;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.Optional;

public class SoundManager {


    /**
     * Whether the audio for our UI is muted or not.
     */
    public boolean myIsMuted = false;
    /**
     * Volume of our UI audio
     */
    private int myVolume = 100;

    private MediaPlayer notificationPlayer;



    /* ====================================
    HELPER METHODS
     ====================================*/

    public void playNotificationSound() {
        if (myIsMuted) return;

        if (notificationPlayer == null) {
            URL url = getClass().getResource("Assets/notification.wav");
            if (url == null) {
                System.out.println("ERROR in playNotificationSound(): no notification.wav found");
                return;
            }

            try {
                Media media = new Media(url.toExternalForm());
                notificationPlayer = new MediaPlayer(media);

                notificationPlayer.setOnEndOfMedia(() -> {
                    notificationPlayer.stop();
                });

            } catch (Throwable theException) {
                System.err.println("Audio not working: Ask Oisin (that's me!) for his Run Configuration");

                //Mute when there's an error
                myIsMuted = true;
                return;
            }
        }

        MediaPlayer.Status status = notificationPlayer.getStatus();

        if (status == MediaPlayer.Status.PLAYING) {
            return;
        }

        notificationPlayer.setVolume(myVolume / 100.0);
        notificationPlayer.play();
    }

    public int getVolume() { return myVolume; }

    public void setMuted(boolean muted) { myIsMuted = muted; }
    public boolean isMuted() { return myIsMuted; }

    public void setVolume(Integer newVol) {
        myVolume = newVol;
    }
}
