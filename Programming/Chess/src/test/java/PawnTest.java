import management.Board;
import org.junit.Test;
import pieces.Pawn;
import resources.Alliance;
import resources.Console;
import resources.Piece;
import resources.Vector2;

public class PawnTest
{
    @Test
    public void movePawnForward()
    {
        Board board = new Board(8, 0);

        Vector2 posPawn0 = new Vector2(0, 6);
        Vector2 posPawn1 = new Vector2(1, 6);
        Vector2 posPawn2 = new Vector2(2, 6);
        Vector2 posPawn3 = new Vector2(3, 6);
        Vector2 posPawn4 = new Vector2(4, 6);
        Vector2 posPawn5 = new Vector2(5, 6);
        Vector2 posPawn6 = new Vector2(6, 6);
        Vector2 posPawn7 = new Vector2(7, 6);

        board.addPiece(posPawn0, Piece.PAWN, Alliance.WHITE);
        board.addPiece(posPawn1, Piece.PAWN, Alliance.WHITE);
        board.addPiece(posPawn2, Piece.PAWN, Alliance.WHITE);
        board.addPiece(posPawn3, Piece.PAWN, Alliance.WHITE);
        board.addPiece(posPawn4, Piece.PAWN, Alliance.WHITE);
        board.addPiece(posPawn5, Piece.PAWN, Alliance.WHITE);
        board.addPiece(posPawn6, Piece.PAWN, Alliance.WHITE);
        board.addPiece(posPawn7, Piece.PAWN, Alliance.WHITE);

        Pawn whiteTest0 = (Pawn) board.getPiece(posPawn0);
        Pawn whiteTest1 = (Pawn) board.getPiece(posPawn1);
        Pawn whiteTest2 = (Pawn) board.getPiece(posPawn2);
        Pawn whiteTest3 = (Pawn) board.getPiece(posPawn3);
        Pawn whiteTest4 = (Pawn) board.getPiece(posPawn4);
        Pawn whiteTest5 = (Pawn) board.getPiece(posPawn5);
        Pawn whiteTest6 = (Pawn) board.getPiece(posPawn6);
        Pawn whiteTest7 = (Pawn) board.getPiece(posPawn7);

        for(int y = 5; y > 1; y--)
        {
            Vector2 moveForward = new Vector2(0, -1);
            board.movePiece(whiteTest0.position(), whiteTest0.position().add(moveForward));
            assert(whiteTest0 != null);
        }

        for(int y = 5; y > 1; y--)
        {
            Vector2 moveForward = new Vector2(1, -1);
            board.movePiece(whiteTest1.position(), whiteTest1.position().add(moveForward));
            assert(whiteTest1 != null);
        }

        for(int y = 5; y > 1; y--)
        {
            Vector2 moveForward = new Vector2(0, -1);
            board.movePiece(whiteTest2.position(), whiteTest2.position().add(moveForward));
            assert(whiteTest2 != null);
        }

        for(int y = 5; y > 1; y--)
        {
            Vector2 moveForward = new Vector2(0, -1);
            board.movePiece(whiteTest3.position(), whiteTest3.position().add(moveForward));
            assert(whiteTest3 != null);
        }

        for(int y = 5; y > 1; y--)
        {
            Vector2 moveForward = new Vector2(0, -1);
            board.movePiece(whiteTest4.position(), whiteTest4.position().add(moveForward));
            assert(whiteTest4 != null);
        }

        for(int y = 5; y > 1; y--)
        {
            Vector2 moveForward = new Vector2(0, -1);
            board.movePiece(whiteTest5.position(), whiteTest5.position().add(moveForward));
            assert(whiteTest5 != null);
        }

        for(int y = 5; y > 1; y--)
        {
            Vector2 moveForward = new Vector2(0,-1);
            board.movePiece(whiteTest6.position(), whiteTest6.position().add(moveForward));
            assert(whiteTest6 != null);
        }

        for(int y = 5; y > 1; y--)
        {
            Vector2 moveForward = new Vector2(0, -1);
            board.movePiece(whiteTest7.position(), whiteTest7.position().add(moveForward));
            assert(whiteTest7 != null);
        }


        Vector2 pawnIWantToMove = new Vector2(0,4);
        board.addPiece(pawnIWantToMove, Piece.PAWN, Alliance.WHITE);
        Pawn testWhitePawn = (Pawn) board.getPiece(pawnIWantToMove);
        Vector2 amountOfYTilesIWantToMove = new Vector2(0, -1);

        Console.println("Position before moving" + testWhitePawn.position());
        board.movePiece(testWhitePawn.position(), testWhitePawn.position().add(amountOfYTilesIWantToMove));
        Console.println("Position after moving" + testWhitePawn.position());
        Console.println();

        assert(testWhitePawn != null);
    }
}
