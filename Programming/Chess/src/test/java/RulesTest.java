import main.Main;
import management.Board;
import org.junit.Test;
import resources.Console;
import resources.Move;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertTrue;

public class RulesTest {
    public class TestNode {
        public final File testFile;
        public final Move move;
        public final boolean expectedResult;

        public TestNode(String testName, Move move, boolean expectedResult) {
            testFile = new File(Main.TESTS_DIR, testName + Main.TEST_EXTENSION);
            this.move = move;
            this.expectedResult = expectedResult;
        }
    }

    public TestNode[] tests = new TestNode[] {
            new TestNode("allyCantSetCheck", new Move(3, 1, 4, 2), false),
            new TestNode("enPassant", new Move(0,3,1,2), true),
            new TestNode("kingCanAttackIntruder", new Move(4,0,3,0), true),
            new TestNode("pawnCanResolveCheck", new Move(2,7,3,6), true),
            new TestNode("pawnCantThreatenWithMove", new Move(3,7,3,6), true),
            new TestNode("threatenedAttacker", new Move(2,1,3,2), true)
    };

    @Test
    public void rulesTest() {
        for(TestNode test : tests) {
            try {
                Board board = new Board(test.testFile);
                boolean expected = test.expectedResult;
                boolean actual = board.movePiece(test.move);

                Console.println("\nTesting move " + test.move + " in " + test.testFile.getName() + "\n\tExpected result: " + expected + "\n\tActual result: " + actual);
                assertTrue(actual == expected);
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
                Console.printError("Test-file" + test.testFile.getName() + " does not exist");
                continue;
            }
        }
    }
}
