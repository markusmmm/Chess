import management.Board;
import org.junit.jupiter.api.Test;
import resources.Vector2;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardTest {
    @Test
    public void moveTest() {
        Board board = new Board(8, false);
        Vector2 piecePos = new Vector2(0,1);
        Vector2 move = new Vector2(0, 2);
        Vector2 result = new Vector2(0,3);
        board.movePiece(piecePos, move);
        assertEquals(board.getPiece(move).position(), move);


    }
}
