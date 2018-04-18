import management.Stockfish;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.fail;

public class StockfishTest {
    private Stockfish ai;
    private String fen;
    @Before
    public void setUp() {
        ai = new Stockfish();
        fen = "8/6pk/8/1R5p/3K3P/8/6r1/8 b - - 0 42";

    }

    @Test
    public void startEngine() {
        if(!ai.startEngine()){
            fail();
        }
    }
    @Test
    public void getBestMove(){
        ai.startEngine();
        ai.sendCommand("uci");
        System.out.println(ai.getBestMove(fen, 3000));
    }
}
