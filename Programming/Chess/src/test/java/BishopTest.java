import management.Board;
import org.junit.jupiter.api.Test;
import pieces.IChessPiece;
import resources.Piece;
import resources.Vector2;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BishopTest {

    public static void main(String[] args){
        Board board = new Board(8,false);
        IChessPiece bishop = board.getPiece(new Vector2(2,0));

    }

    @Test
    public void testGetPieceBishopPosition() {
        Board board = new Board(8, false);
        Vector2 bishopPos = new Vector2(2,0);
        IChessPiece bishop = board.getPiece(bishopPos);

        assertEquals(Piece.BISHOP,bishop.piece());


    }
/**
    @Test
    public void testPosibleMoves(){
        Board board = new Board(8, false );
        IChessPiece bishop = board.getPiece(new Vector2(2,0));
        List<Vector2> possibleMoves = bishop.getPossibleMoves();
        assertEquals(0,possibleMoves.size());
    }
    */
}

