import management.Board;
import org.junit.jupiter.api.Test;
import resources.Alliance;
import resources.Vector2;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardTest {
    @Test
    public void getUsablePiecesValidAmountTest() {
        int size = 8;
        int yStart = 1;
        int yStop = 3;
        Board board = new Board(size,false);
        for (int x = 0; x < size; x++) {
            board.movePiece(new Vector2(x,yStart), new Vector2(x, yStop));
        }
        assertEquals(board.getUsablePieces(Alliance.BLACK).size(), 16);
        assertEquals(board.getUsablePieces(Alliance.WHITE).size(), 10);

    }

}
