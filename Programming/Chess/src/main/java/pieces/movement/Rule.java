package pieces.movement;

import management.Board;
import pieces.AbstractChessPiece;
import resources.Vector2;
import resources.Move;

public class Rule {
    public final Vector2 relativeMovement;
    public final Vector2 relativeAttack;

    protected Rule(Vector2 relativeMovement, Vector2 relativeAttack) {
        this.relativeMovement = relativeMovement;
        this.relativeAttack = relativeAttack;
    }

    /**
     * Evaluates the rule (Ignores external constraints, e.g. board size, if king is in check, etc.)
     * @param move Move to perform
     * @return Whether or not the relativeMovement follows the rule
     */
    public boolean evaluate(Board board, Move move) {
        AbstractChessPiece piece = board.getPiece(move.start),
                           otherPiece = board.getPiece(move.end);

        if(piece == null || relativeMovement == null) return piece == null && relativeMovement == null;
        if(otherPiece == null || relativeAttack == null) return otherPiece == null && relativeAttack == null;

        if(piece.alliance() == otherPiece.alliance()) return false;

        return move.difference().equals(relativeMovement);
    }

    public boolean evaluate(Board board, Vector2 start, Vector2 end) {
        return evaluate(board, new Move(start, end));
    }
}
