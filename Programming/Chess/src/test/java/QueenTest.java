import management.Board;
import org.junit.jupiter.api.Test;
import pieces.Queen;
import resources.Alliance;
import resources.Vector2;

public class QueenTest {
    @Test
    public void queenMooveDiagonalTest() {
        Queen queen = new Queen(new Vector2(3,3), Alliance.BLACK, new Board(8,false));
        queen.legalMove(new Vector2(7,7));
        queen.legalMove(new Vector2(7,0));
        queen.legalMove(new Vector2(0,7));
        queen.legalMove(new Vector2(0,0));
    }
}
