import management.Board;
import org.junit.jupiter.api.Test;
import pieces.ChessPiece;
import pieces.King;
import pieces.Queen;
import resources.Alliance;
import resources.Vector2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardTest {
    @Test
    public void getUsablePiecesAllIsTest() {
        int size = 8;
        int y = 1;
        Board board = new Board(size,false);
        for (int x = 0; x < size; x++) {
            board.pieces.remove(new Vector2(x,y));
        }
        assertEquals(board.getUsablePieces(Alliance.BLACK).size(), 16);

    }

    @Test
    public void getUsablePiecesValidSomeIsTest() {
        int size = 8;
        Board board = new Board(size,false);
        assertEquals(board.getUsablePieces(Alliance.WHITE).size(), 10);
    }
    @Test
    public void addPieceEmptyBoardTest() {
        Vector2 queenPos = new Vector2(1,1);
        Board board = new Board(3);
        ChessPiece queen = new Queen(queenPos, Alliance.WHITE, board);
        board.addPiece(queenPos, queen);
        assertEquals(queen, board.pieces.get(queenPos));
    }
    @Test
    public void MovePieceUpdatesPosInPieceTest() {
        Vector2 queenPos = new Vector2(1,1);
        Vector2 kingPos = new Vector2(1,1);
        Board board = new Board(3);
        Vector2 newQueenPos = new Vector2(2,1);

        ChessPiece queen = new Queen(queenPos, Alliance.WHITE, board);
        ChessPiece king = new King(kingPos, Alliance.WHITE, board);

        board.addPiece(queenPos, queen);
        board.addPiece(kingPos, king); //can't move without a king

        board.movePiece(queenPos, newQueenPos);

        assertTrue(queen.position().equals(newQueenPos));
    }

    @Test
    public void getPieceTest() {
        Vector2 queenPos = new Vector2(1,1);
        Board board = new Board(3);

        ChessPiece queen = new Queen(queenPos, Alliance.WHITE, board);
        board.pieces.put(queenPos, queen);

        assertTrue(board.getPiece(queenPos) instanceof Queen);
    }
}
