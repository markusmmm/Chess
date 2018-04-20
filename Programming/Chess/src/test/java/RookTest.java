import management.Board;
import org.junit.Before;
import org.junit.Test;
import pieces.Rook;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

public class RookTest {

    private Board board;

    @Before
    public void initObj() {
        board = new Board(8, 0);

        Vector2 king1 = new Vector2(1,1);
        Vector2 king2 = new Vector2(6,6);
        board.addPiece(king1, Piece.KING, Alliance.WHITE);
        board.addPiece(king2, Piece.KING, Alliance.BLACK);
    }

    @Test
    public void rookCanMoveVertical() {
        Vector2 oldPos = new Vector2(0,7);
        Vector2 newPos = new Vector2(0,6);
        board.addPiece(oldPos, Piece.ROOK, Alliance.WHITE);
        board.movePiece(oldPos, newPos);
        assert(board.getPiece(newPos).piece() == Piece.ROOK);
    }

    @Test
    public void rookCanMoveHorizontal() {
        Vector2 oldPos = new Vector2(0,7);
        Vector2 newPos = new Vector2(7,7);
        board.addPiece(oldPos, Piece.ROOK, Alliance.WHITE);
        Rook rook1 = (Rook) board.getPiece(oldPos);
        board.movePiece(oldPos, newPos);
        assert(board.getPiece(newPos).piece() == Piece.ROOK);
    }

    @Test
    public void rookCanAttackVertical() {
        Vector2 oldPos = new Vector2(0,7);
        Vector2 newPos = new Vector2(0,6);
        board.addPiece(oldPos, Piece.ROOK, Alliance.WHITE);
        board.addPiece(newPos, Piece.PAWN, Alliance.BLACK);
        board.movePiece(oldPos, newPos);
        // assert(board.pieces.size() == 3);
        assert(board.nPieces() == 3);
        assert(board.getPiece(newPos).piece() == Piece.ROOK);
        assert(board.getPiece(newPos).alliance() == Alliance.WHITE);
    }

    @Test
    public void rookCanAttackHorizontal() {
        Vector2 oldPos = new Vector2(0,7);
        Vector2 newPos = new Vector2(1,7);
        board.addPiece(oldPos, Piece.ROOK, Alliance.WHITE);
        board.addPiece(newPos, Piece.PAWN, Alliance.BLACK);
        board.movePiece(oldPos, newPos);
        assert(board.nPieces() == 3);
        assert(board.getPiece(newPos).piece() == Piece.ROOK);
        assert(board.getPiece(newPos).alliance() == Alliance.WHITE);
    }

    @Test
    public void rookCanNotJumpOverPiece() {
        Vector2 oldPos = new Vector2(0,7);
        Vector2 newPos = new Vector2(0,6);
        board.addPiece(oldPos, Piece.ROOK, Alliance.WHITE);
        board.addPiece(newPos, Piece.PAWN, Alliance.WHITE);
        Rook rook1 = (Rook) board.getPiece(oldPos);
        board.movePiece(oldPos, newPos);
        assert(rook1.position() != newPos);
    }

    @Test
    public void rookPossibleMovesSizeTest() {
        Vector2 pos = new Vector2(0,7);
        board.addPiece(pos, Piece.ROOK, Alliance.WHITE);
        assert(board.getPiece(pos).getPossibleDestinations().size() == 14);
    }

    @Test
    public void rookPossibleMovesSizeTest2() {
        Vector2 pos = new Vector2(3,3);
        board.addPiece(pos, Piece.ROOK, Alliance.WHITE);
        assert(board.getPiece(pos).getPossibleDestinations().size() == 14);
    }

}
