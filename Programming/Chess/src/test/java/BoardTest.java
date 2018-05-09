import management.Board;
import management.BoardLibrary;
import org.junit.Test;
import pieces.AbstractChessPiece;
import pieces.Queen;
import resources.Alliance;
import resources.Vector2;

import static org.junit.Assert.*;

public class BoardTest {
    private final Vector2 queenPos = new Vector2(3,7);
    private final BoardLibrary boards = new BoardLibrary();

    @Test
    public void piecesAreLoadedOntoBoardTest() {
        assertNotEquals(boards.get("default").getPiece(new Vector2(0,7)), null);
    }
    @Test
    public void getUsablePiecesAllIsTest() {
        assertEquals(boards.get("default_noPawns").getUsablePieces(Alliance.BLACK).size(), 8);

    }

    @Test
    public void allUsablePiecesAreValidTest() {
        // On a default board (standard setup, without any performed moves),
        // there should be 10 usable white pieces: 8 pawns, 2 knights
        assertEquals(boards.get("default").getUsablePieces(Alliance.WHITE).size(), 10);
    }
    @Test
    public void MovePieceUpdatesPosInPieceTest() {
        Vector2 newQueenPos = new Vector2(2,7);
        Board board = boards.get("queen");

        board.movePiece(queenPos, newQueenPos);
        AbstractChessPiece piece = board.getPiece(newQueenPos);

        assertTrue(board.getPiece(queenPos) == null &&
                piece != null && piece.position().equals(newQueenPos));
    }

    @Test
    public void getPieceTest() {
        assertTrue(boards.get("queen").getPiece(queenPos) instanceof Queen);
    }
}
