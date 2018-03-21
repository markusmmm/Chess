import management.Board;
import org.junit.jupiter.api.Test;
import pieces.Pawn;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PawnTest
{
    @Test
    public void movePawnForward()
    {

        Piece[] testBoard = new Piece[]
                {
                Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
                Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
                Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
                Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
                Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN
                };
        Board board = new Board(8, false, testBoard);
        Vector2 pawnIWantToMove = new Vector2(0,4);
        Pawn testWhitePawn = (Pawn) board.getPiece(pawnIWantToMove);
        Vector2 amountOfYTilesIWantToMove = new Vector2(0, -1);

        System.out.println("Position before moving" + testWhitePawn.position());
        board.movePiece(testWhitePawn.position(), testWhitePawn.position().add(amountOfYTilesIWantToMove));
        System.out.println("Position after moving" + testWhitePawn.position());
        System.out.println();

        assert(testWhitePawn != null);
    }
}
