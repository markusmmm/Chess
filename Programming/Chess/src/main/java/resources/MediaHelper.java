package resources;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;

public class MediaHelper {

    /**
     * Method to help find and play sound
     *
     * @param fileName
     * @return Playable sound
     */
    public void playSound(String fileName) {
        String path = "sounds/" + fileName;
        ClassLoader cLoader = getClass().getClassLoader();
        Media sound;
        try {
            sound = new Media(cLoader.getResource(path).toURI().toString());
            new MediaPlayer(sound).play();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
}