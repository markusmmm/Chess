package management;

import resources.Alliance;
import resources.Move;

public abstract class ChessComputer {
    private final Alliance alliance = Alliance.BLACK;
    protected final Board board;

    public ChessComputer(Board board) {
        this.board = board;
    }

    public Alliance alliance() {
        return alliance;
    }

    public abstract Move getMove();
}
