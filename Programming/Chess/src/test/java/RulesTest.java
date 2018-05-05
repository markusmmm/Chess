import main.Main;
import management.Board;
import org.junit.Test;
import resources.Console;
import resources.Move;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RulesTest {
    public class TestNode {
        public final File testFile;
        public final Move[] moves;
        public final boolean expectedResult;

        public TestNode(String testName, boolean expectedResult, Move... moves) {
            testFile = new File(Main.TESTS_DIR, testName + Main.TEST_EXTENSION);
            this.moves = moves;
            this.expectedResult = expectedResult;
        }

        @Override
        public String toString() {
            String movesStr = "[ ";
            for(Move move : moves)
                movesStr += move + "  |  ";
            movesStr = movesStr.substring(0,movesStr.length() - 5) + " ]";

            return testFile.getName() + ": " + movesStr + "\texpected: " + expectedResult;
        }
    }

    public TestNode[] tests = new TestNode[] {
            new TestNode("rook",                     true,  new Move(0,7,0,4)),
            new TestNode("rook",                     true,  new Move(0,7,4,7)),
            new TestNode("bishop",                   true,  new Move(2,7,4,5)),
            new TestNode("knight",                   true,  new Move(1,7,0,5)),
            new TestNode("queen",                    true,  new Move(3,7,3,4)),
            new TestNode("queen",                    true,  new Move(3,7,5,7)),
            new TestNode("queen",                    true,  new Move(3,7,5,5)),
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
            new TestNode("castling",                 true,  new Move(4,7,5,7)),
            new TestNode("castling",                 true,  new Move(4,7,2,7)),
    };

    @Test
    public void rulesTest() {
        for(TestNode test : tests) {
            try {
                Board board = new Board(test.testFile);
                boolean expected = test.expectedResult;
                boolean actual = true;
                for (Move move : test.moves) {
                    if (!board.movePiece(move)) {
                        actual = false;
                        break;
                    }
                }

                Console.println("\nTesting moves " + test.moves + " in " + test.testFile.getName() + "\n\tExpected result: " + expected + "\n\tActual result: " + actual);
                assertTrue(actual == expected);
            } catch (FileNotFoundException e) {
                Console.printError("Test-file " + test.testFile.getName() + " does not exist");
            }
        }
    }
}
