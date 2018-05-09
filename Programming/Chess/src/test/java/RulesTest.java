import main.Main;
import management.Board;
import management.BoardLibrary;
import org.junit.Test;
import resources.Console;
import resources.Move;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertTrue;

public class RulesTest {
    BoardLibrary boards = new BoardLibrary(Main.SAVES_DIR);

    public class TestNode {
        public final File testFile;
        public final Move[] moves;
        public final boolean expectedResult;

        /**
         * Defines a node for testing a sequence of moves for a given board state
         * @param testName Name of board state to load in prior to testing (No file-extension)
         * @param expectedResult Expected result of performing the given move(s)
         * @param moves: Vararg collection of moves to attempt on the loaded board
         */
        public TestNode(String testName, boolean expectedResult, Move... moves) {
            testFile = new File(Main.TESTS_DIR, testName + Main.TEST_EXTENSION);
            this.moves = moves;
            this.expectedResult = expectedResult;
        }

        @Override
        public String toString() {
            String movesStr = "[ ";
            for(Move move : moves)
                movesStr += move.toString() + "  |  ";
            movesStr = movesStr.substring(0,movesStr.length() - 5) + " ]";

            return testFile.getName() + "\t" + movesStr + "\n\tExpected result: " + expectedResult;
        }
    }

    private TestNode[] tests = new TestNode[] {
            new TestNode("rook",                     true,  new Move(0,7,0,4)),  //vertical
            new TestNode("rook",                     true,  new Move(0,7,4,7)),  //horizontal
            new TestNode("bishop",                   true,  new Move(2,7,4,5)),  //positive diagonal
            new TestNode("knight",                   true,  new Move(1,7,0,5)),  //NW
            new TestNode("queen",                    true,  new Move(3,7,3,4)),  //vertical
            new TestNode("queen",                    true,  new Move(3,7,5,7)),  //horizontal
            new TestNode("queen",                    true,  new Move(3,7,5,5)),  //diagonal
            new TestNode("king",                     true,  new Move(4,7,4,6)),
            new TestNode("king",                     false, new Move(4,7,4,5)),
            new TestNode("king",                     true,  new Move(4,7,3,7)),
            new TestNode("pawn",                     true,  new Move(7,6,7,5)),
            new TestNode("pawn",                     true,  new Move(7,6,7,4)),

            new TestNode("allyCantSetCheck",         false, new Move(3,1,4,2)),
            new TestNode("enPassant",                true,  new Move(0,3,1,2)),
            new TestNode("kingCanAttackIntruder",    true,  new Move(4,0,3,0)),
            new TestNode("pawnCanResolveCheck",      true,  new Move(2,7,3,6)),
            new TestNode("pawnCantThreatenWithMove", true,  new Move(3,7,3,6)),
            new TestNode("threatenedAttacker",       true,  new Move(2,1,3,2)),
            new TestNode("castling",                 true,  new Move(4,7,6,7)),  //king-side
            new TestNode("castling",                 true,  new Move(4,7,2,7)),  //queen-side
            new TestNode("castlingOverCheckedArea",  false,  new Move(4,7,2,7))  //queen-side
    };

    @Test
    /**
     * Loops through all defined TestNode-objects, and asserts for each node, that performing all defined moves in
     * sequence will give the expectedResult
     */
    public void rulesTest() {
        int nTests = tests.length;
        int nSuccess = 0;

        for(TestNode test : tests) {
            boolean actual = false, expected = test.expectedResult;
            try {
                Board board = new Board(test.testFile);
                actual = true;
                for (Move move : test.moves) {
                    if (!board.movePiece(move)) {
                        actual = false;
                        break;
                    }
                }

                assertTrue(actual == expected);
            } catch (FileNotFoundException e) {
                Console.printError("Test-file " + test.testFile.getName() + " does not exist");
                continue;
            } catch (Exception e) {
                Console.printError(test + "\nActual result: " + actual);
                continue;
            }
            nSuccess++;
            Console.printSuccess(test + "\n SUCCESS");
        }
        Console.printNotice("\n\nRESULT: " + nSuccess + " / " + nTests + " tests succeeded");
    }
}
