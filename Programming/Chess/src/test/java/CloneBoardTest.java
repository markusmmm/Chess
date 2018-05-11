import main.Main;
import management.Board;
import management.BoardLibrary;
import org.junit.Test;
import resources.Move;
import resources.Vector2;

import static org.junit.Assert.assertTrue;

public class CloneBoardTest {
    private final BoardLibrary boards = new BoardLibrary(Main.CORE_DIR);

    @Test
    public void CloneCantChangeOrgTest() {
        Board org = boards.get("default");
        Board clone = org.clone();

        // Move the left-most white pawn one step up
        Vector2 start = new Vector2(0,6);
        Vector2 end = new Vector2(0,5);

        clone.movePiece(start, end);

        assertTrue(org.vacant(end));
        assertTrue(!org.vacant(start));
    }
    @Test
    public void CloneOrgAgainResets() {
        Board org = boards.get("default");
        Board clone = org.clone();

        // Move the left-most white pawn one step up
        Vector2 start = new Vector2(0,6);
        Vector2 end = new Vector2(0,5);

        clone.movePiece(new Move(start, end));
        clone = org.clone();

        assertTrue(clone.vacant(end));
        assertTrue(!clone.vacant(start));
    }
}
