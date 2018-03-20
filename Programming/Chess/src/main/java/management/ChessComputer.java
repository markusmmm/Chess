package management;

import resources.Alliance;
import resources.Move;

public abstract class ChessComputer {
    private final Alliance alliance;
    protected final Board board;

    public ChessComputer(Alliance alliance, Board board) {
        this.alliance = alliance;
        this.board = board;
    }

    public Alliance alliance() {
        return alliance;
    }

    public abstract Move getMove();
}
