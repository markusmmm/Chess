package management;

import resources.Alliance;
import resources.Move;

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
}
