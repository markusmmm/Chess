package pieces;

import management.AbstractBoard;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.HashSet;

public abstract class ChessPiece extends AbstractChessPiece {

    protected ChessPiece(Vector2 position, Alliance alliance, HashSet<Vector2> moves, ActionType actionType, AbstractBoard board, boolean canJump, Piece piece, int value, boolean hasMoved) {
        super(position, alliance, moves, actionType, board, canJump, piece, value, hasMoved);
    }
    protected ChessPiece(Vector2 position, Alliance alliance, HashSet<Vector2> moves, HashSet<Vector2> attacks, ActionType actionType, AbstractBoard board, boolean canJump, Piece piece, int value, boolean hasMoved) {
        super(position, alliance, moves, attacks, actionType, board, canJump, piece, value, hasMoved);
    }
    protected ChessPiece(ChessPiece other) {
        super(other);
    }

    @Override
    public boolean legalAction(Vector2 destination) {
        Vector2 delta = destination.sub(position());

        AbstractChessPiece other = board.getPiece(destination);
        boolean isMove = moves.contains(delta);
        boolean isAttack = attacks.contains(delta);
        boolean valid = (isMove && isAttack) ||
                isMove && other == null ||
                isAttack && other != null && other.alliance() != alliance();

        if(super.legalAction(destination)) {
            if (actionType == ActionType.STEP)
                return valid;
            else if (actionType == ActionType.LINE)
                for (Vector2 action : vectorTools.mergeSets(moves, attacks))
                    if (action.isParallelTo(delta))
                        return valid;
        }

        return false;
    }
}
