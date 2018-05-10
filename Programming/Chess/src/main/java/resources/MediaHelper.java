package resources;


import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import main.Main;

import javax.swing.*;
import java.io.File;

public class MediaHelper {

    /**
     * @param fileName Name of sound-file
     * @return Playable sound
     */
    public MediaPlayer getMedia(String fileName) {
        if(!Main.hasLaunched()) return null;
        
        Media sound;
        try {
            sound = new Media((new File(Main.RESOURCES_DIR, "sounds/")).getAbsolutePath());
            return new MediaPlayer(sound);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return null;
    }

    /**
     * Loads a given file, and plays it (if it exists)
     * @param fileName Name of sound-file
     * @return If the sound was played successfully
     */
    public boolean play(String fileName) {
        MediaPlayer mp = getMedia(fileName);
        if(mp == null) return false;
        mp.play();
        return true;
    }
}
