import main.Main;
import management.Board;
import management.BoardLibrary;
import org.junit.Test;
import resources.Console;
import resources.Move;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertTrue;

public class RulesTest {
    BoardLibrary boards = new BoardLibrary(Main.TESTS_DIR);

    // Naming convention: pieceToTest|cantMove(Expected result: false)|relativeMoveDestination
    // More general tests break this naming convention (e.g. allyCantSetCheck)
    @Test public void rookN()                           { perform("rook",                    true,  new Move(0,7,0,4)); }
    @Test public void rookE()                           { perform("rook",                    true,  new Move(0,7,4,7)); }
    @Test public void bishopNE()                        { perform("bishop",                  true,  new Move(2,7,4,5)); }
    @Test public void knightNW()                        { perform("knight",                  true,  new Move(1,7,0,5)); }
    @Test public void knightNE()                        { perform("knight",                  true,  new Move(1,7,2,5)); }
    @Test public void queenN()                          { perform("queen",                   true,  new Move(3,7,3,4)); }
    @Test public void queenE()                          { perform("queen",                   true,  new Move(3,7,5,7)); }
    @Test public void queenNE()                         { perform("queen",                   true,  new Move(3,7,5,5)); }
    @Test public void kingN()                           { perform("king",                    true,  new Move(4,7,4,6)); }
    @Test public void kingCantMove2N()                  { perform("king",                    false, new Move(4,7,4,5)); }
    @Test public void kingW()                           { perform("king",                    true,  new Move(4,7,3,7)); }
    @Test public void pawn2N()                          { perform("pawn",                    true,  new Move(7,6,7,5)); }
    @Test public void pawnN()                           { perform("pawn",                    true,  new Move(7,6,7,4)); }
    @Test public void allyCantSetCheck()                { perform("allyCantSetCheck",        false, new Move(3,1,4,2)); }
    @Test public void enPassant()                       { perform("enPassant",               true,  new Move(0,3,1,2)); }
    @Test public void kingCanAttackIntruder()           { perform("kingCanAttackIntruder",   true,  new Move(4,0,3,0)); }
    @Test public void pawnCanResolveCheck()             { perform("pawnCanResolveCheck",     true,  new Move(2,6,3,5)); }
    @Test public void pawnCantThreatenWithMove()        { perform("pawnCantThreatenWithMove",true,  new Move(3,7,3,6)); }
    @Test public void attackCanResolveCheck()           { perform("threatenedAttacker",      true,  new Move(2,1,3,2)); }
    @Test public void castlingKingSide()                { perform("castling",                true,  new Move(4,7,6,7)); }
    @Test public void castlingQueenSide()               { perform("castling",                true,  new Move(4,7,2,7)); }
    @Test public void cantCastleOverCheckedArea()       { perform("castlingOverCheckedArea", false, new Move(4,7,2,7)); }

    public void perform(String testName, boolean expectedResult, Move... moves) {
        boolean actual = true;
        for(Move move : moves) {
            if (!boards.get(testName).movePiece(move)) {
                actual = false;
                break;
            }
        }
        assert(actual == expectedResult);
    }
}
