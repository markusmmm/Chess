package management;

import resources.Move;
import resources.Vector2;

public class ChessComputerHard extends ChessComputer {
    private Stockfish ai = new Stockfish();
    private final int THINK_TIME = 2500;
    public ChessComputerHard(Board board) {
        super(board);
        ai.startEngine();
        ai.sendCommand("uci");
    }

    @Override
    public Move getMove() {
        ai.getBestMove(boardToFen(),THINK_TIME);
        //TODO ChessComputerHard.getMove
        throw new UnsupportedOperationException();
    }

    private String boardToFen() {
        StringBuilder fen = new StringBuilder();
        int spacecounter = 0;
        for (int y = 0; y < board.size(); y++) {
            for (int x = 0; x < board.size(); x++) {
                if(board.getPiece(new Vector2(x,y)) == null) {
                    spacecounter++;
                }
            }
        }
        return fen.toString();
    }
}
