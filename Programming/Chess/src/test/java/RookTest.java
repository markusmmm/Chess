import management.Board;
import org.junit.jupiter.api.Test;
import pieces.Rook;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

public class RookTest {

    @Test
    public void rookCanMoveVertical() {
        Piece[] testBoard = new Piece[]{};
        Board board = new Board(8, false, testBoard);
        Vector2 oldPos = new Vector2(0,7);
        Vector2 newPos = new Vector2(0,6);
        board.addPiece(oldPos, Piece.ROOK, Alliance.WHITE);
        board.movePiece(oldPos, newPos);
        assert(board.getPiece(newPos).piece() == Piece.ROOK);
    }

    @Test
    public void rookCanMoveHorizontal() {
        Piece[] testBoard = new Piece[]{};
        Board board = new Board(8, false, testBoard);
        Vector2 oldPos = new Vector2(0,7);
        Vector2 newPos = new Vector2(7,7);
        board.addPiece(oldPos, Piece.ROOK, Alliance.WHITE);
        Rook rook1 = (Rook) board.getPiece(oldPos);
        board.movePiece(oldPos, newPos);
        assert(board.getPiece(newPos).piece() == Piece.ROOK);
    }

    @Test
    public void rookCanAttackVertical() {
        Piece[] testBoard = new Piece[]{};
        Board board = new Board(8, false, testBoard);
        Vector2 oldPos = new Vector2(0,7);
        Vector2 newPos = new Vector2(0,6);
        board.addPiece(oldPos, Piece.ROOK, Alliance.WHITE);
        board.addPiece(newPos, Piece.PAWN, Alliance.BLACK);
        board.movePiece(oldPos, newPos);
        assert(board.nPieces() == 1);
        assert(board.getPiece(newPos).piece() == Piece.ROOK);
        assert(board.getPiece(newPos).alliance() == Alliance.WHITE);
    }

    @Test
    public void rookCanAttackHorizontal() {
        Piece[] testBoard = new Piece[]{};
        Board board = new Board(8, false, testBoard);
        Vector2 oldPos = new Vector2(0,7);
        Vector2 newPos = new Vector2(1,7);
        board.addPiece(oldPos, Piece.ROOK, Alliance.WHITE);
        board.addPiece(newPos, Piece.PAWN, Alliance.BLACK);
        board.movePiece(oldPos, newPos);
        assert(board.nPieces() == 1);
        assert(board.getPiece(newPos).piece() == Piece.ROOK);
        assert(board.getPiece(newPos).alliance() == Alliance.WHITE);
    }

    @Test
    public void rookCanNotJumpOverPiece() {
        Piece[] testBoard = new Piece[]{};
        Board board = new Board(8, false, testBoard);
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
        Piece[] testBoard = new Piece[]{};
        Board board = new Board(8, false, testBoard);
        Vector2 pos = new Vector2(0,7);
        board.addPiece(pos, Piece.ROOK, Alliance.WHITE);
        assert(board.getPiece(pos).getPossibleDestinations().size() == 14);
    }

    @Test
    public void rookPossibleMovesSizeTest2() {
        Piece[] testBoard = new Piece[]{};
        Board board = new Board(8, false, testBoard);
        Vector2 pos = new Vector2(3,3);
        board.addPiece(pos, Piece.ROOK, Alliance.WHITE);
        assert(board.getPiece(pos).getPossibleDestinations().size() == 13);
    }

}
