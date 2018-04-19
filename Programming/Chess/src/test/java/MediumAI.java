import management.Board;
import management.ChessComputerMedium;
import org.junit.Before;
import org.junit.Test;
import resources.Move;

import java.io.FileNotFoundException;

public class MediumAI {
    private ChessComputerMedium minmax;
    private Board chessB;
    @Before
    public void setup() {
        try {
            chessB = new Board("default");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        minmax = new ChessComputerMedium(chessB);
    }
    @Test
    public void startTest() {
        chessB.movePiece(minmax.getMove());
        minmax.getMove();
    }
}
