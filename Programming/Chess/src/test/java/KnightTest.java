import management.Board;
import org.junit.jupiter.api.Test;
import pieces.Knight;
import resources.Alliance;
import resources.BoardMode;
import resources.Vector2;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class KnightTest {
    BoardMode boardMode = BoardMode.DEFAULT;
    @Test
    public void MoveLegalRight() {
        Knight knight = new Knight(new Vector2(3,3), Alliance.WHITE, new Board(8,0,false,boardMode));
        assertTrue(knight.legalMove(new Vector2(5,2)));
        assertTrue(knight.legalMove(new Vector2(5,4)));
    }
    @Test
    public void MoveLegalLeft() {
        Knight knight = new Knight(new Vector2(3,3), Alliance.WHITE, new Board(8,0,false,boardMode));
        assertTrue(knight.legalMove(new Vector2(1,2)));
        assertTrue(knight.legalMove(new Vector2(1,4)));
    }
    @Test
    public void MoveLegalUp() {
        Knight knight = new Knight(new Vector2(3,3), Alliance.WHITE, new Board(8,0,false,boardMode));
        assertTrue(knight.legalMove(new Vector2(2,1)));
        assertTrue(knight.legalMove(new Vector2(4,1)));
    }
    @Test
    public void MoveLegalDown() {
        Knight knight = new Knight(new Vector2(3,3), Alliance.WHITE, new Board(8,0,false,boardMode));
        assertTrue(knight.legalMove(new Vector2(2,5)));
        assertTrue(knight.legalMove(new Vector2(4,5)));
    }
}
