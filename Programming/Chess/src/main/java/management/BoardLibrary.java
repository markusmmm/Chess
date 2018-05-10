package management;

import main.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * Stores and manages a library of multiple boards (useful for tests)
 * Prevents the same board from being loaded twice into the library
 */
public class BoardLibrary {
    private HashMap<String, Board> boards = new HashMap<>();
    private final File directory;

    public BoardLibrary(File directory) {
        this.directory = directory;
    }

    private boolean set(String boardName) {
        try {

            File file = new File(directory, boardName + Main.SAVE_EXTENSION);
            if(!file.exists()) return false;

            boards.put(boardName, new Board(file));
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    public Board get(String boardName) {
        if(!boards.containsKey(boardName) && !set(boardName)) return null;
        return boards.get(boardName).clone();
    }
}
