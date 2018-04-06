import management.Board;
import org.junit.jupiter.api.Test;
import pieces.ChessPiece;
import pieces.Queen;
import resources.Alliance;
import resources.Vector2;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardTest {
    @Test
    public void getUsablePiecesAllIs() {
        int size = 8;
        int y = 1;
        Board board = new Board(size,false);
        for (int x = 0; x < size; x++) {
            board.pieces.remove(new Vector2(x,y));
        }
        assertEquals(board.getUsablePieces(Alliance.BLACK).size(), 16);

    }

    @Test
    public void getUsablePiecesValidSomeIs() {
        int size = 8;
        Board board = new Board(size,false);
        assertEquals(board.getUsablePieces(Alliance.WHITE).size(), 10);
    }
    @Test
    public void addPieceEmptyBoard() {
        Vector2 queenPos = new Vector2(1,1);
        Board board = new Board(3);
        ChessPiece queen = new Queen(queenPos, Alliance.WHITE, board);
        board.addPiece(queenPos, queen);
        assertEquals(queen, board.pieces.get(queenPos));
    }
    @Test
    public void MovePieceUpdatesPosInPiece() {
        Vector2 queenPos = new Vector2(1,1);
        Board board = new Board(3);
        Vector2 newQueenPos = new Vector2(2,1);

        ChessPiece queen = new Queen(queenPos, Alliance.WHITE, board);
        board.pieces.put(queenPos, queen);

        board.movePiece(queenPos, newQueenPos);

        assertEquals(queen.position(), newQueenPos);

    }


}
