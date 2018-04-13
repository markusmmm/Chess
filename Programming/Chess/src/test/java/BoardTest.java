import management.Board;
import org.junit.Test;
import pieces.ChessPiece;
import pieces.King;
import pieces.Queen;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class BoardTest {
    @Test
    public void getUsablePiecesAllIsTest() {
        int size = 8;
        int y = 1;
        Board board = new Board(size);
        for (int x = 0; x < size; x++) {
            board.removePieces(new Vector2(x,y));
        }
        assertEquals(board.getUsablePieces(Alliance.BLACK).size(), 16);

    }

    @Test
    public void getUsablePiecesValidSomeIsTest() {
        int size = 8;
        Board board = new Board(size);
        assertEquals(board.getUsablePieces(Alliance.WHITE).size(), 10);
    }
    @Test
    public void addPieceEmptyBoardTest() {
        Vector2 queenPos = new Vector2(1,1);
        Board board = new Board(3);
        ChessPiece queen = board.addPiece(queenPos, Piece.QUEEN, Alliance.WHITE);
        assertEquals(queen, board.getPiece(queenPos));
    }
    @Test
    public void MovePieceUpdatesPosInPieceTest() {
        Vector2 queenPos = new Vector2(1,1);
        Vector2 kingPos = new Vector2(1,1);
        Board board = new Board(3);
        Vector2 newQueenPos = new Vector2(2,1);

        ChessPiece queen = board.addPiece(queenPos, Piece.QUEEN, Alliance.WHITE);
        ChessPiece king = board.addPiece(kingPos, Piece.KING, Alliance.WHITE); //can't move without a king

        board.movePiece(queenPos, newQueenPos);

        assertTrue(queen.position().equals(newQueenPos));
    }

    @Test
    public void getPieceTest() {
        Vector2 queenPos = new Vector2(1,1);
        Board board = new Board(3);

        ChessPiece queen = board.addPiece(queenPos, Piece.QUEEN, Alliance.WHITE);

        assertTrue(board.getPiece(queenPos) instanceof Queen);
    }
}
