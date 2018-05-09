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
    public MediaPlayer getMediaPlayer(String fileName) {
        String path = "sounds/" + fileName;
        ClassLoader cLoader = getClass().getClassLoader();
        Media sound;
        try {
           sound = new Media(cLoader.getResource(path).toURI().toString());
           MediaPlayer mp =  new MediaPlayer(sound);
            return mp;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return null;
    }

    public boolean play(String fileName) {
        MediaPlayer mp = getMediaPlayer(fileName);
        if(mp == null) return false;
        mp.play();
        return true;
    }
}