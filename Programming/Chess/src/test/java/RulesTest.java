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
    // More complex tests break this naming convention (e.g. allyCantSetCheckWithMove)

    //Basic movement tests
    @Test public void rookN()                           { perform("rook",                    true,  new Move(0,7,0,4)); }
    @Test public void rookE()                           { perform("rook",                    true,  new Move(0,7,4,7)); }
    @Test public void rookCantMoveNE()                  { perform("rook",                    false, new Move(0,7,1,6)); }

    @Test public void bishopNE()                        { perform("bishop",                  true,  new Move(2,7,4,5)); }
    @Test public void bishopCantMoveN()                 { perform("bishop",                  false, new Move(2,7,2,6)); }

    @Test public void knight2NW()                       { perform("knight",                  true,  new Move(1,7,0,5)); }
    @Test public void knight2NE()                       { perform("knight",                  true,  new Move(1,7,2,5)); }

    @Test public void queenN()                          { perform("queen",                   true,  new Move(3,7,3,4)); }
    @Test public void queenE()                          { perform("queen",                   true,  new Move(3,7,5,7)); }
    @Test public void queenNE()                         { perform("queen",                   true,  new Move(3,7,5,5)); }

    @Test public void kingN()                           { perform("king",                    true,  new Move(4,7,4,6)); }
    @Test public void kingCantMove2E()                  { perform("king",                    false, new Move(4,7,2,7)); }
    @Test public void kingW()                           { perform("king",                    true,  new Move(4,7,3,7)); }

    @Test public void pawn2N()                          { perform("pawn",                    true,  new Move(7,6,7,5)); }
    @Test public void pawnN()                           { perform("pawn",                    true,  new Move(7,6,7,4)); }

    //Special movement tests
    @Test public void whiteEnPassant()                  { perform("whiteEnPassant",          true,  new Move(0,3,1,2)); }
    @Test public void blackEnPassant()                  { perform("blackEnPassant",          true,  new Move(1,4,0,5)); }

    @Test public void castlingKingSide()                { perform("castling",                true,  new Move(4,7,6,7)); }
    @Test public void castlingQueenSide()               { perform("castling",                true,  new Move(4,7,2,7)); }
    @Test public void cantCastleOverCheckedArea()       { perform("castlingOverCheckedArea", false, new Move(4,7,2,7)); }

    //Behaviour tests
    @Test public void allyCantSetCheckWithMove()        { perform("allyCantSetCheck",false, new Move(3,1,4,2)); }
    @Test public void kingCanAttackIntruder()           { perform("kingCanAttackIntruder",   true,  new Move(4,0,3,0)); }
    @Test public void pawnCanResolveCheck()             { perform("pawnCanResolveCheck",     true,  new Move(2,6,3,5)); }
    @Test public void pawnCantThreatenWithMove()        { perform("pawnCantThreatenWithMove",true,  new Move(3,7,3,6)); }
    @Test public void attackCanResolveCheck()           { perform("threatenedAttacker",      true,  new Move(2,1,3,2)); }

    @Test public void checkmateAgainstPlayer()          { perform("checkmate",               true, new Move(3,3,5,1)); }
    @Test public void checkmateAgainstEasyAI()          { perform("checkmate_easyAI",        true, new Move(3,3,5,1)); }
    @Test public void checkmateAgainstMediumAI()        { perform("checkmate_mediumAI",      true, new Move(3,3,5,1)); }
    @Test public void checkmateAgainstHardAI()          { perform("checkmate_hardAI",        true, new Move(3,3,5,1)); }

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
