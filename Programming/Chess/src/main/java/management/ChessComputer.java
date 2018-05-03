package management;

import resources.Alliance;
import resources.Console;
import resources.Move;

import java.util.*;

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

    public abstract Move getMove();

    public Move resolveMove(Move move) {
        List<Move> validMoves = new ArrayList<>();
        validMoves.addAll(board.getAllPossibleMoves(alliance));

        if(move == null && validMoves.size() != 0) {
            Console.printWarning("Computer found no valid moves in time. Choosing a random valid move...");

            Random rand = new Random();
            int r = rand.nextInt(validMoves.size());

            move = validMoves.get(r);
        }
        return move;
    }
}
