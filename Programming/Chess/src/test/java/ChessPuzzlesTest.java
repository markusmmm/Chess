import management.ChessPuzzles;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ChessPuzzlesTest {
    ChessPuzzles puzzles;

    @Before
    public void setup() {
       puzzles = new ChessPuzzles();

       puzzles.printFiles();
    }
    @Test
    public void sizeOfDirectoryTest(){
        int i =  puzzles.getSizeOfDirectory();
        Assert.assertEquals(5,i);
    }
}
