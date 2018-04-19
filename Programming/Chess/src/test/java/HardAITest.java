import management.Board;
import management.ChessComputerHard;
import management.ChessComputerMedium;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

public class HardAITest {
    private ChessComputerHard ai;
    private Board board;
    @Before
    public void setUp() {
        try {
            board = new Board("default");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ai = new ChessComputerHard(board);
    }
    @Test
    public void getMove(){
        ai.getMove();
    }

}
