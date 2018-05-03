package management;

import resources.MoveScore;

import java.util.HashMap;

/**
 * stores best moves from both sides with hensyn to boardarrangement
 */
public abstract class GeneralAI {
    private HashMap<String, MoveScore> moves = new HashMap<>();

    private String switchSides(String board) {
        return null;
    }
    private int scoreBoard(String board) {
        return 0;
    }
    private Move getRandomMove(String board)

}
