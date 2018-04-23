import management.Board;
import org.junit.jupiter.api.Test;
import pieces.Queen;
import resources.Alliance;
import resources.BoardMode;
import resources.Vector2;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueenTest {
    BoardMode boardMode = BoardMode.DEFAULT;

    @Test
    public void queenMoveDiagonalTest() {
        Queen queen = new Queen(new Vector2(3,3), Alliance.BLACK, new Board(8,0, false, boardMode));

        assertTrue(queen.legalMove(new Vector2(5,5)));
        assertTrue(queen.legalMove(new Vector2(2,2)));
        assertTrue(queen.legalMove(new Vector2(2,4)));
        assertTrue(queen.legalMove(new Vector2(1,5)));
    }

    @Test
    public void queenMoveStraightTest() {
        Queen queen = new Queen(new Vector2(3,3), Alliance.BLACK, new Board(8,0,false, boardMode));
        assertTrue(queen.legalMove(new Vector2(0,3)));
        assertTrue(queen.legalMove(new Vector2(3,2)));
        assertTrue(queen.legalMove(new Vector2(7,3)));
        assertTrue(queen.legalMove(new Vector2(3,5)));
    }
}
