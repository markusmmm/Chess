import management.Board;
import management.ChessComputerMedium;
import org.junit.Test;

import java.io.FileNotFoundException;

public class MediumAI {
    private ChessComputerMedium minmax;
    @Test
    public void pawn() {
        test("default");
    }
    @Test
    public void king() {
        test("king");
    }
    private void test(String fileName) {
        try {
            Board chessB = new Board(fileName);
            minmax = new ChessComputerMedium(chessB);
            minmax.getMove();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
