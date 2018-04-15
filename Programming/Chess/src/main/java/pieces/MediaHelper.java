package pieces;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;

public class MediaHelper {


    public void playSound39(String fileName) {

        String filePath = "sounds/" + fileName;
        ClassLoader classLoader = getClass().getClassLoader();

        Media hit;

        try {
            hit = new Media(classLoader.getResource(filePath).toURI().toString());
            new MediaPlayer(hit).play();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }


    }
}