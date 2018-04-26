import management.Board;
import management.ChessComputerMedium;
import management.ChessPuzzles;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

public class chessPuzzlesTest {

    String path;
    ChessPuzzles puzzles;


    @Before
    public void setup() {
       path = "src/main/resources/chesspuzzles/checkmateinthree/";
       puzzles = new ChessPuzzles();
       puzzles.printFiles();
       System.out.println(puzzles.getRandomFile());


    }
    @Test
    public void testSizeOfDircetory(){
        int i =  puzzles.getSizeOfDirectory();
        Assert.assertEquals(5,i);
    }
}
