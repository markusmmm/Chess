package management;

import resources.Alliance;
import resources.Console;
import resources.Move;

import java.util.*;

/**
 * A player that is controller by an AI
 */
public abstract class ChessComputer {
    private final Alliance alliance;
    protected final Board board;

    public ChessComputer(Board board) {
        this.board = board;
        alliance = Alliance.BLACK;
    }
    public ChessComputer(Board board, Alliance alliance) {
        this.board = board;
        this.alliance = alliance;
    }

    public Alliance alliance() {
        return alliance;
    }

    /**
     * @return Move that is evaluated as the best move by the AI
     */
    public abstract Move getMove();

    /**
     * Controls if the move found by the AI can be used. If not, choose a legal move at random
     * @param move Move to evaluate
     * @return Final chosen move
     */
    public Move resolveMove(Move move) {
        List<Move> validMoves = new ArrayList<>();
        validMoves.addAll(board.getAllLegalActions(alliance));

        if(move == null && validMoves.size() != 0) {
            Console.printWarning("Computer found no valid moves in time. Choosing a random valid move...");

            Random rand = new Random();
            int r = rand.nextInt(validMoves.size());

            move = validMoves.get(r);
        }
        return move;
    }
}
