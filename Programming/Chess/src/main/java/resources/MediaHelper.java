package resources;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;
import java.util.HashMap;

/**
 * Handles loading and playing of sounds
 * Sounds are stored in a hash-map once they are loaded, so that they only need to be loaded once
 * All media interactions must happen via 'MediaHelper'. This is to prevent exceptions from occurring
 * (e.g. trying to play a sound that doesn't exist)
 */
public class MediaHelper {
    private HashMap<String, MediaPlayer> sounds = new HashMap<>();

    /**
     * Returns the corresponding stored media player. If the player doesn't already exist in the library, it will be loaded and added
     * @param fileName Name of sound-file
     * @return Playable sound
     */
    private MediaPlayer getMedia(String fileName) {
        //if(!Main.hasLaunched()) return null;

        if(sounds.containsKey(fileName))
            return sounds.get(fileName);

        String path = "sounds/" + fileName;
        ClassLoader cLoader = getClass().getClassLoader();
        try {
            MediaPlayer sound = new MediaPlayer(new Media(cLoader.getResource(path).toURI().toString()));
            sounds.put(fileName, sound);
            return sound;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return null;
    }

    /**
     * Plays a given file, if it exists
     * @param fileName Name of sound-file
     * @return If the sound was played successfully
     */
    public boolean play(String fileName) {
        MediaPlayer mp = getMedia(fileName);
        if(mp == null) return false;
        mp.play();
        return true;
    }

    public boolean setCycleCount(String fileName, int value) {
        MediaPlayer mp = getMedia(fileName);
        if(mp == null) return false;
        mp.setCycleCount(value);
        return true;
    }
    public boolean setVolume(String fileName, double value) {
        MediaPlayer mp = getMedia(fileName);
        if(mp == null) return false;
        mp.setVolume(value);
        return true;
    }
    public boolean setMute(String fileName, boolean state) {
        MediaPlayer mp = getMedia(fileName);
        if(mp == null) return false;
        mp.setMute(state);
        return true;
    }
}
