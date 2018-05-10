package resources;


import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import main.Main;

import javax.swing.*;

public class MediaHelper {

    /**
     * @param fileName Name of sound-file
     * @return Playable sound
     */
    public MediaPlayer getMedia(String fileName) {
        if(!Main.hasLaunched()) return null;

        String path = "sounds/" + fileName;
        ClassLoader cLoader = getClass().getClassLoader();
        Media sound;
        try {
            sound = new Media(cLoader.getResource(path).toURI().toString());
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
