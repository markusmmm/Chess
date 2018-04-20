import management.Board;
import org.junit.jupiter.api.Test;
import resources.BoardMode;
import resources.Move;
import resources.Piece;
import resources.Vector2;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CloneBoardTest {
    BoardMode boardMode = BoardMode.DEFAULT;
    @Test
    public void CloneCantChangeOrgTest() {
        Board org = new Board(8,0,false,boardMode);
        Board clone = org.clone();

        Vector2 start = new Vector2(0,1);
        Vector2 end = new Vector2(0,2);

        clone.movePiece(new Move(start, end));

        assertTrue(org.vacant(end));
        assertTrue(!org.vacant(start));
    }
    @Test
    public void CloneOrgAgainResets() {
        Board org = new Board(8,0,false,boardMode);
        Board clone = org.clone();

        Vector2 start = new Vector2(0,1);
        Vector2 end = new Vector2(0,2);

        clone.movePiece(new Move(start, end));
        clone = org.clone();

        assertTrue(clone.vacant(end));
        assertTrue(!clone.vacant(start));
    }
}
