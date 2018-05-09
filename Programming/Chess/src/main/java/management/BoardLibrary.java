package management;

import main.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * Used to store a library of multiple boards (useful for tests)
 */
public class BoardLibrary {
    private HashMap<String, Board> boards = new HashMap<>();

    public BoardLibrary() {
    }

    private boolean set(String boardName) {
        try {
            boards.put(boardName, new Board(new File(Main.CORE_DIR, boardName + Main.SAVE_EXTENSION)));
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
