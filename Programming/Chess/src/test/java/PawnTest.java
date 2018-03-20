import management.Board;
import org.junit.jupiter.api.Test;
import pieces.Pawn;
import resources.Vector2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PawnTest {
    @Test
    public void PawnMoveForwardTest() {
        Board board = new Board(8, false);
        Vector2 piecePos = new Vector2(0,1);
        Vector2 move = new Vector2(0, 2);
        board.movePiece(piecePos, move);
        assertEquals(board.getPiece(move).position(), move);
    }
    @Test
    public void PawnMoveToFarTest() {
        Board board = new Board(8, false);
        Vector2 piecePos = new Vector2(0,1);
        Vector2 move = new Vector2(0, 3);
        board.movePiece(piecePos, move);
        if (board.getPiece(move) instanceof Pawn){
            fail("");
        }
    }
}
