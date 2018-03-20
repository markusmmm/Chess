package management;

import pieces.IChessPiece;
import resources.Alliance;
import resources.Move;
import resources.Vector2;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ChessComputer {
    private final Alliance alliance;
    private final Board board;

    public ChessComputer(Alliance alliance, Board board) {
        this.alliance = alliance;
        this.board = board;
    }

    public Move getMove() {
        HashMap<Vector2, IChessPiece> pieces = board.getPieces(alliance);
        Random rand = new Random();

        Vector2[] keys = (Vector2[]) pieces.keySet().toArray();
        IChessPiece piece = pieces.get(keys[rand.nextInt(pieces.size())]);

        List<Vector2> moves = piece.getPossibleMoves();
        Vector2 destination = moves.get(rand.nextInt(moves.size()));

        return new Move(piece.position(), destination);
    }
}
